package com.example.englishmaster_be.domain.pack.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ParameterObject
public class PackOptionsFilterRequest {

    int pageNumber;

    int pageSize;

    Sort.Direction direction;

    String sortBy;

}
