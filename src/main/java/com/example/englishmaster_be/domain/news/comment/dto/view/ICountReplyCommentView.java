package com.example.englishmaster_be.domain.news.comment.dto.view;

import java.util.UUID;

public interface ICountReplyCommentView {

    UUID getCommentId();

    int getCountReplies();

}
