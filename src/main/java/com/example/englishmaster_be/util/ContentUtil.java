package com.example.englishmaster_be.util;

import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class ContentUtil {

    FileUtil fileUtil;

    public ContentEntity makeQuestionContentEntity(UserEntity byUser, QuestionEntity refQuestion, String contentData) {

        return ContentEntity.builder()
                .contentId(UUID.randomUUID())
                .contentData(contentData)
                .contentType(fileUtil.mimeTypeFile(contentData))
                .userCreate(byUser)
                .userUpdate(byUser)
                .question(refQuestion)
                .build();
    }

    public ContentEntity makeTopicContentEntity(UserEntity byUser, TopicEntity refTopic, String contentData) {

        return ContentEntity.builder()
                .contentId(UUID.randomUUID())
                .contentData(contentData)
                .contentType(fileUtil.mimeTypeFile(contentData))
                .userCreate(byUser)
                .userUpdate(byUser)
                .topic(refTopic)
                .build();
    }

}
