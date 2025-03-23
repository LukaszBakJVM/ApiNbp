package org.example.nbp;

import org.example.nbp.api.ResponseCourse;
import org.example.nbp.dto.*;
import org.example.nbp.exception.CurrencyCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RatesServices {
    private final WebClient webClient;
    private final RatesMapper ratesMapper;
    private final RatesInfoRepository repository;

    public RatesServices(WebClient.Builder webClient, RatesMapper ratesMapper, RatesInfoRepository repository) {
        this.webClient = webClient.build();
        this.ratesMapper = ratesMapper;
        this.repository = repository;
    }

    Mono<ResponseCurrency> saveRates(RequestRatesBody requestRatesBody) {
        return nbpCourseInfo(requestRatesBody.currency()).flatMap(responseCourse -> {
            double mid = responseCourse.rates().getFirst().mid();
            SaveRatesInfo saveRatesInfo = new SaveRatesInfo(requestRatesBody.currency(), requestRatesBody.name(), mid);
            RatesInfo ratesInfo = ratesMapper.writeInfo(saveRatesInfo);
            return repository.save(ratesInfo).map(ratesMapper::response);
        });
    }

    Flux<ResponseAllSavedRates> allSavedRates() {
        return repository.findAll().map(ratesMapper::entityToDto);
    }


    private Mono<ResponseCourse> nbpCourseInfo(String currency) {
        return webClient.get().uri("/api/exchangerates/rates/a/{code}", currency).header("Accept", "application/json").retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> {
            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                return Mono.error(new CurrencyCodeException(String.format("Currency code %s not found", currency)));
            }
            return Mono.error(new ResponseStatusException(response.statusCode()));
        }).bodyToMono(ResponseCourse.class);
    }

    Mono<List<String>> findRates() {
        return webClient.get().uri("/api/exchangerates/tables/A").header("Accept", "application/json").retrieve().bodyToFlux(TableA.class).flatMapIterable(TableA::rates).map(Rates::code).sort().collectList();

    }

    Flux<ResponseCurrency> findByCurrencyAndTime(String code, LocalDate data) {
        if ((code == null || code.isEmpty()) && data != null) {
            return findByData(data);
        } else if (data == null) {
            return findByCurrency(code);
        }
        return repository.findByCurrencyAndTimeStamp(code, data).map(ratesMapper::response).distinct();
    }

    private Flux<ResponseCurrency> findByCurrency(String code) {
        return repository.findByCurrency(code).map(ratesMapper::response).distinct();
    }

    private Flux<ResponseCurrency> findByData(LocalDate data) {
        return repository.findByTimeStampContaining(data).map(ratesMapper::response).distinct();
    }


    @Scheduled(cron = "0 0 11 * * ?")
    private void scheduling() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = localDate.format(formatter);
        findRates().flatMapMany(Flux::fromIterable).flatMap(s -> saveRates(new RequestRatesBody(s, formattedDate))).subscribe();

    }
}
