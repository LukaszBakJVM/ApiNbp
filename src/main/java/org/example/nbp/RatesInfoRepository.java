package org.example.nbp;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface RatesInfoRepository extends R2dbcRepository<RatesInfo, Long> {
    @Query("select *from rates_info r WHERE r.currency = :code order by id")
    Flux<RatesInfo> findByCurrency(String currency);

    @Query("SELECT * FROM rates_info r WHERE r.currency = :code AND  r.time_stamp::date = :date")
    Flux<RatesInfo> findByCurrencyAndTimeStamp(@Param("code") String code, @Param("date") LocalDate date);

    @Query("SELECT * FROM rates_info r WHERE r.time_stamp::date = :date")
    Flux<RatesInfo> findByTimeStampContaining(@Param("date") LocalDate date);


}
