package com.example.englishmaster_be.domain.news.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewsRequest {

    @NotNull(message = "Title is required.")
    @NotBlank(message = "Title is required.")
    String title;

    @NotNull(message = "Content is required.")
    @NotBlank(message = "Content is required.")
    String content;

    String image;

}
