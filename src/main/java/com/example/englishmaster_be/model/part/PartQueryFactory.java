package com.example.englishmaster_be.model.part;

import com.example.englishmaster_be.model.topic.TopicEntity;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<PartEntity> selectFromPartEntity() {

        return jpaQueryFactory.selectFrom(QPartEntity.partEntity);
    }

    public Optional<PartEntity> findPartByNameAndType(String partName, String partType) {

        BooleanExpression conditionQueryPattern = QPartEntity.partEntity.partName.equalsIgnoreCase(partName)
                .and(QPartEntity.partEntity.partType.equalsIgnoreCase(partType));

        PartEntity partResult = selectFromPartEntity().where(conditionQueryPattern).fetchOne();

        return Optional.ofNullable(partResult);
    }

    public List<PartEntity> findAllPartsByNameAndTopic(String partName, TopicEntity topic){

        BooleanExpression conditionQueryPattern = QPartEntity.partEntity.partName.equalsIgnoreCase(partName)
                .and(QPartEntity.partEntity.topics.contains(topic));

        return selectFromPartEntity().where(conditionQueryPattern).fetch();

    }

}
