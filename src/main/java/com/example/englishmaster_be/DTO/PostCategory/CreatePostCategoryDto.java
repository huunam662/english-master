package com.example.englishmaster_be.DTO.PostCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostCategoryDto {
    @Schema(description = "Tên danh mục bài viết", example = "Tin tức")
    private String name;
    @Schema(description = "Slug", example = "tin-tuc")
    private String slug;
    @Schema(description = "Hình ảnh", example = "https://www.google.com.vn")
    private String image;
    @Schema(description = "Mô tả", example = "Tin tức")
    private String description;
    @Schema(description = "Thứ tự", example = "1")
    private int order;
    @Schema(description = "Trạng thái", example = "true")
    private boolean status;
}

