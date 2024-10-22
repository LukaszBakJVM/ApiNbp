package org.example.nbp;

import org.example.nbp.dto.ResponseCourse;
import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.WriteBody;
import org.example.nbp.dto.WriteInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CourseInfoServices {
    private final WebClient webClient;
    private final CourseMapper courseMapper;
    private final CourseInfoRepository repository;

    public CourseInfoServices(WebClient.Builder webClient, CourseMapper courseMapper, CourseInfoRepository repository) {
        this.webClient = webClient.build();
        this.courseMapper = courseMapper;
        this.repository = repository;
    }

    Mono<ResponseCurrency> ratesMono(WriteBody writeBody) {
        return nbpCourseInfo(writeBody.currency()).flatMap(responseCourse -> {
            double mid = responseCourse.rates().getFirst().mid();
            WriteInfo writeInfo = new WriteInfo(writeBody.currency(), writeBody.name(), mid);
            CourseInfo courseInfo = courseMapper.writeInfo(writeInfo);
            return repository.save(courseInfo).map(courseMapper::response);
        });
    }


    Mono<ResponseCourse> nbpCourseInfo(String currency) {
        return webClient.get().uri("/api/exchangerates/rates/a/{code}", currency).header("Accept", "application/json").retrieve().bodyToMono(ResponseCourse.class);
    }
}
