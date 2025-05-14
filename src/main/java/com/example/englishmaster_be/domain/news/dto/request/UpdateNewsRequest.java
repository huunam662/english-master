package com.example.englishmaster_be.domain.news.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateNewsRequest extends CreateNewsRequest {

    @NotNull(message = "News Id is required.")
    UUID newsId;

    @NotNull(message = "Enable status of news is required.")
    @Schema(example = "true")
    Boolean enable;

}
