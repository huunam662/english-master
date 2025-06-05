package com.example.englishmaster_be.domain.comment.dto.response;

import com.example.englishmaster_be.domain.news.dto.response.AuthorCommentResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentChildResponse {

    UUID commentId;

    UUID commentParentId;

    Boolean isCommentParent;

    String commentContent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime timeOfComment;

    String commentToOwnerTag;

    AuthorCommentResponse authorComment;
}
