package com.example.englishmaster_be.domain.news.comment.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateCmToCommentReq {

    @NotNull(message = "Comment owner id is required.")
    private UUID commentOwnerId;

    @NotNull(message = "Comment content is required.")
    @NotBlank(message = "Comment content is required.")
    private String commentContent;
}
