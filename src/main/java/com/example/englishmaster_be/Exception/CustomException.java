package com.example.englishmaster_be.Exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomException extends RuntimeException {

    private final Error error;

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
    }




}
