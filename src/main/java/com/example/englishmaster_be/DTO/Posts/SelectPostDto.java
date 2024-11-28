package com.example.englishmaster_be.DTO.Posts;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Data
public class SelectPostDto extends PageOptionDto {

    @Parameter(description = "Status of Post", in = ParameterIn.QUERY, example = "1", required = false)
    private Integer status;

    @Parameter(description = "Exclude slug", in = ParameterIn.QUERY, example = "slug", required = false)
    private String excludeSlug;
}
