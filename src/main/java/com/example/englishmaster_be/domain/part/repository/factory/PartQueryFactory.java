package com.example.englishmaster_be.domain.part.repository.factory;

import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.part.model.QPartEntity;
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
import java.util.Optional;

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
