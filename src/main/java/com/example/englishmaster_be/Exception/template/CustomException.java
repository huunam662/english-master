package com.example.englishmaster_be.Exception.template;

import com.example.englishmaster_be.Common.enums.ErrorEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomException extends RuntimeException {

    ErrorEnum error;

    public CustomException(ErrorEnum error) {
        super(error.getMessage());
        this.error = error;
    }

}
