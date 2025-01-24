package com.example.englishmaster_be.model.question;

import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<QuestionEntity> selectFromQuestionEntity(){

        return jpaQueryFactory.selectFrom(QQuestionEntity.questionEntity);
    }

    public List<QuestionEntity> findAllQuestionsParentBy(TopicEntity topic, List<PartEntity> partTopicList){

        BooleanExpression conditionQueryPattern = QQuestionEntity.questionEntity.isQuestionParent
                .and(QQuestionEntity.questionEntity.topics.contains(topic))
                .and(QQuestionEntity.questionEntity.part.in(partTopicList));

        return selectFromQuestionEntity().where(conditionQueryPattern).fetch();
    }

}
