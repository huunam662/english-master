package com.example.englishmaster_be.domain.news.news.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdateNewsReq extends CreateNewsReq {

    @NotNull(message = "News Id is required.")
    private UUID newsId;

    @NotNull(message = "Enable status of news is required.")
    @Schema(example = "true")
    private Boolean enable;

}
