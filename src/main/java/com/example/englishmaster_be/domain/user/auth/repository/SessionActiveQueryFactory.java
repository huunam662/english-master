package com.example.englishmaster_be.domain.user.auth.repository;

import com.example.englishmaster_be.domain.user.auth.model.QSessionActiveEntity;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class SessionActiveQueryFactory {

    private final JPAQueryFactory jpaQueryFactory;

    @Lazy
    public SessionActiveQueryFactory(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private JPAQuery<SessionActiveEntity> selectFromSessionActiveEntity(){

        return jpaQueryFactory.selectFrom(QSessionActiveEntity.sessionActiveEntity);
    }

    public Optional<SessionActiveEntity> findByToken(String token){

        BooleanExpression conditionQueryPattern = QSessionActiveEntity.sessionActiveEntity.token.equalsIgnoreCase(token);

        SessionActiveEntity sessionActiveResult = selectFromSessionActiveEntity().where(conditionQueryPattern).fetchOne();

        return Optional.ofNullable(sessionActiveResult);
    }

}
