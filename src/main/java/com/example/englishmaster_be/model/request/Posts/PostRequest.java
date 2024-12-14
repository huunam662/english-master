package com.example.englishmaster_be.model.request.Posts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PostRequest {
    @Schema(description = "Title of the PostEntity", example = "PostEntity title")
    private String title;
    @Schema(description = "Slug of the PostEntity", example = "PostEntity-title")
    private String slug;
    @Schema(description = "Description of the PostEntity", example = "PostEntity description")
    private String description;
    @Schema(description = "Short ContentEntity of the PostEntity", example = "PostEntity short ContentEntity")
    private String shortContent;
    @Schema(description = "ContentEntity of the PostEntity", example = "PostEntity ContentEntity")
    private String content;
    @Schema(description = "Image of the PostEntity", example = "https://www.google.com.vn")
    private String image;
    @Schema(description = "StatusEntity of the PostEntity", example = "true")
    private boolean status;
    @Schema(description = "PostEntity category id", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID postCategoryId;
}
