package com.example.englishmaster_be.shared.dto.request;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ParameterObject
public class FilterRequest {

    @Parameter(description = "Trang", example = "1")
    @Min(value = 1, message = "Min value of page is 1.")
    Integer page;

    @Parameter(description = "Kích thước (số phần tử) của trang", example = "8")
    @Min(value = 1, message = "Min value of page size is 1.")
    @Max(value = 100, message = "Max value of page size is 100.")
    Integer pageSize;

}
