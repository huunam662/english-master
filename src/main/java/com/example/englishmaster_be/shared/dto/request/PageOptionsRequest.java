package com.example.englishmaster_be.shared.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ParameterObject
public class PageOptionsRequest {

    @Min(value = 1, message = "Page value required min is 1.")
    int page;
    @Min(value = 1, message = "Page size required min is 1.")
    int size;
    String sort;
    Sort.Direction direction;
}
