package com.example.englishmaster_be.domain.content.helper;

import com.example.englishmaster_be.domain.content.model.ContentEntity;
import com.example.englishmaster_be.domain.content.model.QContentEntity;
import com.example.englishmaster_be.shared.helper.FileHelper;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ContentHelper {

    JPAQueryFactory jpaQueryFactory;

    FileHelper fileUtil;

    public ContentEntity makeContentEntity(UserEntity byUser, TopicEntity refTopic, String contentData) {

        ContentEntity contentEntity = jpaQueryFactory.selectFrom(QContentEntity.contentEntity)
                .where(
                        QContentEntity.contentEntity.contentData.eq(contentData)
                                .and(QContentEntity.contentEntity.contentType.eq(fileUtil.mimeTypeFile(contentData)))
                )
                .fetchOne();

        if(contentEntity != null) return contentEntity;

        return ContentEntity.builder()
                .contentData(contentData)
                .contentType(fileUtil.mimeTypeFile(contentData))
                .userCreate(byUser)
                .userUpdate(byUser)
                .topic(refTopic)
                .build();
    }

}
