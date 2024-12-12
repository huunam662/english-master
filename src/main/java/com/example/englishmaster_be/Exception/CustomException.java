package com.example.englishmaster_be.Exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomException extends RuntimeException {

    Error error;

    public CustomException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

}
