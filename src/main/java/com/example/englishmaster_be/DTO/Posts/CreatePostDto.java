package com.example.englishmaster_be.DTO.Posts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePostDto {
    @Schema(description = "Title of the post", example = "Post title")
    private String title;
    @Schema(description = "Slug of the post", example = "post-title")
    private String slug;
    @Schema(description = "Description of the post", example = "Post description")
    private String description;
    @Schema(description = "Short content of the post", example = "Post short content")
    private String shortContent;
    @Schema(description = "Content of the post", example = "Post content")
    private String content;
    @Schema(description = "Image of the post", example = "https://www.google.com.vn")
    private String image;
    @Schema(description = "Status of the post", example = "true")
    private boolean status;
    @Schema(description = "Post category id", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID postCategoryId;
}
