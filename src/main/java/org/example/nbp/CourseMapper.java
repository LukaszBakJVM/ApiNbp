package org.example.nbp;

import org.example.nbp.dto.ResponseAllSavedRates;
import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.SaveRatesInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CourseMapper {


    RatesInfo writeInfo(SaveRatesInfo saveRatesInfo) {
        Instant timestamp = Instant.now();
        RatesInfo ratesInfo = new RatesInfo();
        ratesInfo.setCurrency(saveRatesInfo.currency());
        ratesInfo.setPersonalData(saveRatesInfo.name());
        ratesInfo.setTimeStamp(timestamp);
        ratesInfo.setCourse(saveRatesInfo.value());
        return ratesInfo;
    }


    ResponseCurrency response(RatesInfo ratesInfo) {
        return new ResponseCurrency(ratesInfo.getCourse());
    }

    ResponseAllSavedRates entityToDto(RatesInfo ratesInfo) {
        return new ResponseAllSavedRates(ratesInfo.getCurrency(), ratesInfo.getPersonalData(), ratesInfo.getTimeStamp(), ratesInfo.getCourse());
    }


}
