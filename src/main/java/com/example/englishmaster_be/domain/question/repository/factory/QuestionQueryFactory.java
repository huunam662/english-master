package com.example.englishmaster_be.domain.question.repository.factory;

import com.example.englishmaster_be.domain.question.model.QQuestionEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<QuestionEntity> selectFromQuestionEntity(){

        return jpaQueryFactory.selectFrom(QQuestionEntity.questionEntity);
    }

    public List<QuestionEntity> findAllQuestionsParentBy(TopicEntity topic, List<PartEntity> partTopicList){

        BooleanExpression conditionQueryPattern = QQuestionEntity.questionEntity.isQuestionParent
                .and(QQuestionEntity.questionEntity.part.topic.eq(topic))
                .and(QQuestionEntity.questionEntity.part.in(partTopicList));

        return selectFromQuestionEntity().where(conditionQueryPattern).fetch();
    }

}
