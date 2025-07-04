package com.example.englishmaster_be.domain.upload.cloudinary.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@ParameterObject
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryOptionsRequest {

    @Parameter(description = "Token to load next page.")
    String nextPageToken;

    @Parameter(description = "Page size to show.", example = "1", required = true)
    @Min(value = 1, message = "Page size required min 1.")
    Integer size;

    @Parameter(
            description = "Type file to load.",
            schema = @Schema(allowableValues = {"image", "video", "raw"}),
            example = "image"
    )
    String type;
}
