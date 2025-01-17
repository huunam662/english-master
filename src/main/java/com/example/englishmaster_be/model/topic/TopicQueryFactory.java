package com.example.englishmaster_be.model.topic;

import com.example.englishmaster_be.model.pack.PackEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<TopicEntity> selectFromTopicEntity() {

        return jpaQueryFactory.selectFrom(QTopicEntity.topicEntity);
    }

    public Optional<TopicEntity> findTopicByNameAndPack(String topicName, PackEntity pack) {

        BooleanExpression conditionQueryPattern = QTopicEntity.topicEntity.topicName.equalsIgnoreCase(topicName)
                .and(QTopicEntity.topicEntity.pack.eq(pack));

        TopicEntity topicResult = selectFromTopicEntity().where(conditionQueryPattern).fetchOne();

        return Optional.ofNullable(topicResult);
    }

}
