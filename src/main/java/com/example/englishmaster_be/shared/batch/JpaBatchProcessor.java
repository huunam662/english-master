package com.example.englishmaster_be.shared.batch;

import com.example.englishmaster_be.value.AppValue;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaBatchProcessor {

    EntityManager entityManager;

    AppValue appValue;

    @Transactional
    public <T> void saveAll(Set<T> entitySet) {

        if(entitySet == null) return;

        int batchSize = appValue.getBatchSize();
        int entitySetSize = entitySet.size();
        int next = 0;

        if(batchSize > entitySetSize)
            batchSize = entitySetSize;

        for (T entity : entitySet) {

            next++;
            entityManager.persist(entity);

            if (next % batchSize == 0 || next == entitySetSize) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }


}
