package com.example.englishmaster_be.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateCommentRequest {

    @NotNull(message = "Comment id is required.")
    UUID commentId;

    @NotNull(message = "Comment content is required.")
    @NotBlank(message = "Comment content is required.")
    String commentContent;

}
