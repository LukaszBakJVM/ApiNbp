package org.example.nbp;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public class RatesInfo {

    @Id
    private long id;
    private String currency;

    private Instant timeStamp;
    private double course;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }
}
