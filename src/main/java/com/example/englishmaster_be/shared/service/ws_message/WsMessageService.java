package com.example.englishmaster_be.shared.service.ws_message;

import com.example.englishmaster_be.domain.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.comment.util.CommentUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WsMessageService implements IWsMessageService{

    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendToUsersCommentInNews(NewsEntity news, CommentEntity commendForSend, List<UserEntity> usersNotSend) {

        if(news == null) return;

        if(commendForSend == null) return;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

                        List<UserEntity> usersToSend = CommentUtil.getUsersFromCommentList(news.getComments().stream().toList(), usersNotSend);

                        usersToSend.forEach(
                                userToSendMs -> simpMessagingTemplate.convertAndSendToUser(
                                        userToSendMs.getUserId().toString(),
                                        "/news/ws/notification/fresh-comment",
                                        CommentMapper.INSTANCE.toCommentResponse(commendForSend)
                                )
                        );
                    }
                }
        );
    }

    @Override
    public void sendToUsers(UserEntity userTag, CommentEntity commentForSend) {

        simpMessagingTemplate.convertAndSendToUser(
                userTag.getUserId().toString(),
                "/comment/ws/notification",
                CommentMapper.INSTANCE.toCommentResponse(commentForSend)
        );
    }

}
