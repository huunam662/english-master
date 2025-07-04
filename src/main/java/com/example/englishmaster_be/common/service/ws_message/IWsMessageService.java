package com.example.englishmaster_be.common.service.ws_message;

import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;

import java.util.List;

public interface IWsMessageService {

    void sendToUsersCommentInNews(NewsEntity news, CommentEntity commendForSend, List<UserEntity> usersNotSend);

    void sendToUsers(UserEntity userTag, CommentEntity commentForSend);

}
