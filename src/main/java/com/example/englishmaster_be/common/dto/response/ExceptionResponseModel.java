package com.example.englishmaster_be.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExceptionResponseModel extends ResponseModel {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object errors = null;

}
