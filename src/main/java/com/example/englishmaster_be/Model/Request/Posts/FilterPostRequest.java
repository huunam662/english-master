package com.example.englishmaster_be.Model.Request.Posts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Getter
@Setter
public class FilterPostRequest extends SelectPostRequest {
    @Schema(description = "Title of the PostEntity", example = "PostEntity 1")
    private String title;

    @Schema(description = "Slug of the PostEntity category", example = "PostEntity-category-1")
    private String postCategorySlug;

    @Schema(description = "Min date of the PostEntity", example = "2024-11-22T02:55:20.063Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant minDate;

    @Schema(description = "Max date of the PostEntity", example = "2024-11-22T02:55:20.063Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant maxDate;
}
