package org.example.nbp;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RatesInfoRepository extends ReactiveCrudRepository<RatesInfo, Long> {
    Mono<RatesInfo>findByCurrency(String currency);

}
