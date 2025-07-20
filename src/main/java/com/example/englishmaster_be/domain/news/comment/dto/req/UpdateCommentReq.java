package com.example.englishmaster_be.domain.news.comment.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UpdateCommentReq {

    @NotNull(message = "Comment id is required.")
    private UUID commentId;

    @NotNull(message = "Comment content is required.")
    @NotBlank(message = "Comment content is required.")
    private String commentContent;

}
