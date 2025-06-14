package com.example.englishmaster_be.domain.comment.dto.response;

import com.example.englishmaster_be.domain.news.dto.response.AuthorCommentResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {

    UUID commentId;

    UUID topicId;

    UUID newsId;

    String contentComment;

    String tagAuthorParent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    AuthorCommentResponse authorComment;

    Boolean hasCommentParent;

    Boolean hasCommentChildren;

}
