package com.example.englishmaster_be.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCmToCommentRequest {

    @NotNull(message = "Comment owner id is required.")
    UUID commentOwnerId;

    @NotNull(message = "Comment content is required.")
    @NotBlank(message = "Comment content is required.")
    String commentContent;
}
