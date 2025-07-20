package com.example.englishmaster_be.domain.upload.cloudinary.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;

@Data
@NoArgsConstructor
@ParameterObject
public class CloudinaryOptionsReq {

    @Parameter(description = "Token to load next page.")
    private String nextPageToken;

    @Parameter(description = "Page size to show.", example = "1", required = true)
    @Min(value = 1, message = "Page size required min 1.")
    private Integer size;

    @Parameter(
            description = "Type file to load.",
            schema = @Schema(allowableValues = {"image", "video", "raw"}),
            example = "image"
    )
    private String type;
}
