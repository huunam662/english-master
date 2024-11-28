package com.example.englishmaster_be.DTO.Posts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Getter
@Setter
public class FilterPostDto extends SelectPostDto {
    @Schema(description = "Title of the Post", example = "Post 1")
    private String title;

    @Schema(description = "Slug of the Post category", example = "Post-category-1")
    private String postCategorySlug;

    @Schema(description = "Min date of the Post", example = "2024-11-22T02:55:20.063Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant minDate;

    @Schema(description = "Max date of the Post", example = "2024-11-22T02:55:20.063Z")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant maxDate;
}
