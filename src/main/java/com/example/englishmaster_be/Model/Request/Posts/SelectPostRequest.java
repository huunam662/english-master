package com.example.englishmaster_be.Model.Request.Posts;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Data
public class SelectPostRequest extends PageOptionRequest {

    @Parameter(description = "StatusEntity of PostEntity", in = ParameterIn.QUERY, example = "1", required = false)
    private Integer status;

    @Parameter(description = "Exclude slug", in = ParameterIn.QUERY, example = "slug", required = false)
    private String excludeSlug;
}
