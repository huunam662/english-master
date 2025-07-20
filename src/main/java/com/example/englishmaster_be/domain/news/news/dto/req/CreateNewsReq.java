package com.example.englishmaster_be.domain.news.news.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
public class CreateNewsReq {

    @NotNull(message = "Title is required.")
    @NotBlank(message = "Title is required.")
    private String title;

    @NotNull(message = "Content is required.")
    @NotBlank(message = "Content is required.")
    private String content;

    private String image;

}
