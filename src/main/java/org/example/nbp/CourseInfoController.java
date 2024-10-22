package org.example.nbp;

import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.WriteBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/currencies")
public class CourseInfoController {
    private final CourseInfoServices services;

    public CourseInfoController(CourseInfoServices services) {
        this.services = services;
    }

    @PostMapping
    Mono<ResponseCurrency> curency(@RequestBody WriteBody writeBody) {
        return services.ratesMono(writeBody);
    }
}
