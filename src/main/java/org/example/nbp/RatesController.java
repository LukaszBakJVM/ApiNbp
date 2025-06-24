package org.example.nbp;

import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/currencies")
public class RatesController {
    private final RatesServices services;

    public RatesController(RatesServices services) {
        this.services = services;
    }


    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    Flux<ResponseAllSavedRates> allRatesInfo() {
        return services.allSavedRates();
    }

    @GetMapping("/code")
    @ResponseStatus(HttpStatus.OK)
    Mono<List<String>> code() {
        return services.findRates();
    }

    @GetMapping("/find-data")
    @ResponseStatus(HttpStatus.OK)
    Flux<ResponseCurrency> findData(@RequestParam(required = false) String code, @RequestParam(required = false) LocalDate date) {

        return services.findByCurrencyAndTime(code, date);
    }
}
