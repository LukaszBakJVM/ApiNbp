package org.example.nbp;

import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.WriteBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/currencies")
public class CourseInfoController {
    private final CourseInfoServices services;

    public CourseInfoController(CourseInfoServices services) {
        this.services = services;
    }

    @PostMapping()
    Mono<ResponseEntity<ResponseCurrency>> currency(@RequestBody WriteBody writeBody) {
        return services.ratesMono(writeBody).map(s->ResponseEntity.created(URI.create("/currencies")).body(s));
    }
}
