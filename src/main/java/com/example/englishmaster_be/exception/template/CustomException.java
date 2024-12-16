package com.example.englishmaster_be.exception.template;

import com.example.englishmaster_be.common.constant.error.ErrorEnum;
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
