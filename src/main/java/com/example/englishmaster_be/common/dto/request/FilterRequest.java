package com.example.englishmaster_be.common.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterRequest {

    @Schema(description = "Trang", example = "1")
    @Min(1)
    Integer page;

    @Schema(description = "Kích thước (số phần tử) của trang", example = "8")
    @Min(1) @Max(100)
    Integer pageSize;

}
