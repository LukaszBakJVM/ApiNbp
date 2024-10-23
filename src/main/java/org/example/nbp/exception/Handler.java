package org.example.nbp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class Handler {

    @ExceptionHandler(CurrencyCodeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error badRequestException(CurrencyCodeException ex) {
        return new Error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }


    @ExceptionHandler(ResponseStatusException.class)
    public Error handleResponseStatusException(ResponseStatusException ex) {
        return new Error(ex.getStatusCode().value(), ex.getMessage());
    }

}
