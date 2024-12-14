package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.exception.response.FieldErrorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.validation.FieldError;

import java.util.List;

@Mapper
public interface FieldErrorMapper {

    FieldErrorMapper INSTANCE = Mappers.getMapper(FieldErrorMapper.class);

    FieldErrorResponse toFieldErrorResponse(FieldError fieldError);

    List<FieldErrorResponse> toFieldErrorResponseList(List<FieldError> fieldErrors);

}
