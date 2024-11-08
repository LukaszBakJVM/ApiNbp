package org.example.nbp;

import org.example.nbp.dto.RequestRatesBody;
import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currencies")
public class RatesController {
    private final RatesServices services;

    public RatesController(RatesServices services) {
        this.services = services;
    }

    @PostMapping("/get-current-currency-value-command")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ResponseCurrency> currency(@RequestBody RequestRatesBody requestRatesBody) {
        return services.saveRates(requestRatesBody);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    Flux<ResponseAllSavedRates> allRatesInfo() {
        return services.allSavedRates();
    }
}
