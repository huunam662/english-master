package com.example.englishmaster_be.advice.exception.template;

import com.example.englishmaster_be.common.constant.error.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ErrorHolder extends RuntimeException {

    Error error;

    @NonFinal
    Boolean toClient;

    public ErrorHolder(Error error) {
        this(error, error.getMessage(), true);
    }

    public ErrorHolder(Error error, String message) {
        this(error, message, true);
    }

    public ErrorHolder(Error error, String message, Boolean toClient) {
        super(message);
        this.toClient = toClient;
        this.error = error;
    }
}
