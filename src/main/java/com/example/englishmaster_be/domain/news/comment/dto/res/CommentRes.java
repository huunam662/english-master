package com.example.englishmaster_be.domain.news.comment.dto.res;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class CommentRes {

    private UUID commentId;

    private UUID topicId;

    private UUID newsId;

    private String contentComment;

    private String tagAuthorParent;

    
    private LocalDateTime updateAt;

    private AuthorCommentRes authorComment;

    private Boolean hasCommentParent;

    private Boolean hasCommentChildren;

}
