package com.example.englishmaster_be.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Configuration
@FieldDefaults(level = AccessLevel.PUBLIC)
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
