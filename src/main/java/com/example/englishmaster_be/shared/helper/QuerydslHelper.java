package com.example.englishmaster_be.shared.helper;

import com.querydsl.core.types.dsl.PathBuilderFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuerydslHelper {

    @PersistenceContext
    EntityManager entityManager;

    public <T> Querydsl getInstance(Class<T> entityClass) {

        return new Querydsl(entityManager, new PathBuilderFactory().create(entityClass));
    }

}
