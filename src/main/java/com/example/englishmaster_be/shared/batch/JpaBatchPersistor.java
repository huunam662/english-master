package com.example.englishmaster_be.shared.batch;

import com.example.englishmaster_be.value.AppValue;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaBatchPersistor {

    EntityManager entityManager;

    AppValue appValue;

    @Transactional
    public <T> void saveAll(Set<T> entitySet) {

        if(entitySet == null) return;

        int batchSize = appValue.getBatchSize();
        int startIndex = 0;
        int entitySetSize = entitySet.size();

        List<T> questionList = entitySet.stream().toList();

        while (startIndex < entitySetSize){

            int endIndex = startIndex + batchSize;
            if(endIndex > entitySetSize)
                endIndex = entitySetSize;

            List<T> entityListSub = questionList.subList(startIndex, endIndex);
            for(T entity : entityListSub)
                entityManager.persist(entity);

            entityManager.flush();
            entityManager.clear();

            startIndex = endIndex;
        }
    }

}
