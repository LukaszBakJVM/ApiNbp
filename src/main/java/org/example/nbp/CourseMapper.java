package org.example.nbp;

import org.example.nbp.dto.ResponseCurrency;
import org.example.nbp.dto.WriteInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CourseMapper {
    private final Instant timestamp;

    public CourseMapper(Instant timestamp) {
        this.timestamp = timestamp;
    }


    CourseInfo writeInfo(WriteInfo writeInfo) {
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setCurrency(writeInfo.currency());
        courseInfo.setPersonalData(writeInfo.name());
        courseInfo.setTimeStamp(timestamp);
        courseInfo.setCourse(writeInfo.value());
        return courseInfo;
    }


    ResponseCurrency response(CourseInfo courseInfo) {
        return new ResponseCurrency(courseInfo.getCourse());
    }


}
