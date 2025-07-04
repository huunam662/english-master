package com.example.englishmaster_be.common.batch;

import com.example.englishmaster_be.value.AppValue;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaBatchProcessor {

    EntityManager entityManager;

    AppValue appValue;

    @Transactional
    public <T> void saveBatch(Collection<T> entities) {

        if(entities == null || entities.isEmpty()) return;

        List<T> entityList = new ArrayList<>(entities);

        int batchSize = appValue.getBatchSize();
        int entitiesSize = entities.size();
        int next = 0;

        while (next < entitiesSize) {
            int endNext = Math.min(next + batchSize, entitiesSize);
            List<T> subEntity = entityList.subList(next, endNext);
            for(T entity : subEntity) {
                entityManager.persist(entity);
            }
            entityManager.flush();
            entityManager.clear();
            next = endNext;
        }
    }


}
