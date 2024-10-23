package org.example.nbp.dto;

import java.time.Instant;

public record ResponseAllSavedRates(String currency, String name, Instant date, double value) {
}
