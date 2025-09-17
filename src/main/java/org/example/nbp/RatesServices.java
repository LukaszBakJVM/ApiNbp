package org.example.nbp;

import org.example.nbp.dto.RequestRatesBody;
import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface RatesServices {
    Mono<ResponseCurrency> saveRates(RequestRatesBody requestRatesBody);

    Flux<ResponseAllSavedRates> allSavedRates();

    Flux<ResponseCurrency> findByCurrencyAndTime(String code, LocalDate date);
    Mono<List<String>> findRates();
}
