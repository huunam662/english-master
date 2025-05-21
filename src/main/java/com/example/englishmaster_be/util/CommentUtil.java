package com.example.englishmaster_be.util;

import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CommentUtil {

    public static List<UserEntity> getUsersFromCommentList(List<CommentEntity> commentList, List<UserEntity> usersSkip) {

        List<UserEntity> usersToSend = commentList.stream().collect(
                        Collectors.toMap(
                                commentInstance -> commentInstance.getUserComment().getUserId(),
                                CommentEntity::getUserComment,
                                (existing, duplicate) -> existing
                        )
                ).values()
                .stream().toList();

        if(usersSkip != null && !usersSkip.isEmpty()){
            usersToSend = usersToSend.stream().filter(
                    u -> !usersSkip.contains(u)
            ).toList();
        }

        return usersToSend;
    }


}
