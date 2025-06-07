package com.example.englishmaster_be.domain.auth.repository.factory;

import com.example.englishmaster_be.domain.auth.model.QSessionActiveEntity;
import com.example.englishmaster_be.domain.auth.model.SessionActiveEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionActiveQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<SessionActiveEntity> selectFromSessionActiveEntity(){

        return jpaQueryFactory.selectFrom(QSessionActiveEntity.sessionActiveEntity);
    }

    public Optional<SessionActiveEntity> findByToken(String token){

        BooleanExpression conditionQueryPattern = QSessionActiveEntity.sessionActiveEntity.token.equalsIgnoreCase(token);

        SessionActiveEntity sessionActiveResult = selectFromSessionActiveEntity().where(conditionQueryPattern).fetchOne();

        return Optional.ofNullable(sessionActiveResult);
    }

}
