package org.example.nbp;

import org.example.nbp.dto.Rates;
import org.example.nbp.dto.ResponseInfo;
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


    ResponseInfo responseAllInfo(CourseInfo courseInfo) {
        return new ResponseInfo(courseInfo.getCurrency(), courseInfo.getPersonalData(), courseInfo.getTimeStamp(), courseInfo.getCourse());
    }

    private Rates rates(CourseInfo courseInfo) {
        return new Rates(courseInfo.getCourse());
    }


}
