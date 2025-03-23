package org.example.nbp.dto;

import java.time.LocalDate;

public record ResponseCurrency(String currency, double value, LocalDate date) {
}
