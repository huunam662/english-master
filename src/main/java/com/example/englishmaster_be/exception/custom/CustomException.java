package com.example.englishmaster_be.exception.custom;

import com.example.englishmaster_be.exception.enums.Error;
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
