package org.example.nbp.dto;

import java.time.Instant;

public record ResponseInfo(String currency, String name, Instant date, double value) {
}
