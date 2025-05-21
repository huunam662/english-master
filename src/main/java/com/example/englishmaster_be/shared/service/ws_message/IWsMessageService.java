package com.example.englishmaster_be.shared.service.ws_message;

import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.List;

public interface IWsMessageService {

    void sendToUsersCommentInNews(NewsEntity news, CommentEntity commendForSend, List<UserEntity> usersNotSend);

    void sendToUsers(UserEntity userTag, CommentEntity commentForSend);

}
