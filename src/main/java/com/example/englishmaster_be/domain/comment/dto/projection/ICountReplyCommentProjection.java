package com.example.englishmaster_be.domain.comment.dto.projection;

import java.util.UUID;

public interface ICountReplyCommentProjection {

    UUID getCommentId();

    int getCountReplies();

}
