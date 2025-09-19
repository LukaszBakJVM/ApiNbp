package org.example.nbp.dto;

import java.time.Instant;

public record ResponseAllSavedRates(String currency, Instant date, double value) {
}
