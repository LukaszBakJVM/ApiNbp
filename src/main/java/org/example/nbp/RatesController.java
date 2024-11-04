package org.example.nbp;

import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.RequestRatesBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/currencies")
public class RatesController {
    private final RatesServices services;

    public RatesController(RatesServices services) {
        this.services = services;
    }

    @PostMapping("/get-current-currency-value-command")
    Mono<ResponseEntity<ResponseCurrency>> currency(@RequestBody RequestRatesBody requestRatesBody) {
        return services.saveRates(requestRatesBody).map(s->ResponseEntity.created(URI.create("/currencies")).body(s));
    }
    @GetMapping("/requests")
    Flux<ResponseAllSavedRates>allRatesInfo(){
        return services.allSavedRates();
    }
}
