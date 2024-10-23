package org.example.nbp;

import org.example.nbp.api.ResponseCourse;
import org.example.nbp.dto.RequestRatesBody;
import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.SaveRatesInfo;
import org.example.nbp.exception.CurrencyCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseInfoServices {
    private final WebClient webClient;
    private final CourseMapper courseMapper;
    private final RatesInfoRepository repository;

    public CourseInfoServices(WebClient.Builder webClient, CourseMapper courseMapper, RatesInfoRepository repository) {
        this.webClient = webClient.build();
        this.courseMapper = courseMapper;
        this.repository = repository;
    }

    Mono<ResponseCurrency> ratesMono(RequestRatesBody requestRatesBody) {
        return nbpCourseInfo(requestRatesBody.currency()).flatMap(responseCourse -> {
            double mid = responseCourse.rates().getFirst().mid();
            SaveRatesInfo saveRatesInfo = new SaveRatesInfo(requestRatesBody.currency(), requestRatesBody.name(), mid);
            RatesInfo ratesInfo = courseMapper.writeInfo(saveRatesInfo);
            return repository.save(ratesInfo).map(courseMapper::response);
        });
    }

    Flux<ResponseAllSavedRates> allSavedRates() {
        return repository.findAll().map(courseMapper::entityToDto);
    }


    private Mono<ResponseCourse> nbpCourseInfo(String currency) {
        return webClient.get().uri("/api/exchangerates/rates/a/{code}", currency).header("Accept", "application/json").retrieve().onStatus(HttpStatusCode::is4xxClientError, response -> {
            if (response.statusCode() == HttpStatus.NOT_FOUND) {
                return Mono.error(new CurrencyCodeException(String.format("Currency code %s not found", currency)));
            }
            return Mono.error(new ResponseStatusException(response.statusCode()));
        }).bodyToMono(ResponseCourse.class);
    }
}
