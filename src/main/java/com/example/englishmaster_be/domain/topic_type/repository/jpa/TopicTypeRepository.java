package com.example.englishmaster_be.domain.topic_type.repository.jpa;

import com.example.englishmaster_be.domain.topic_type.dto.response.ITopicTypeKeyProjection;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TopicTypeRepository extends JpaRepository<TopicTypeEntity, UUID> {


    @Query(value = """
        SELECT id FROM topic_type WHERE LOWER(type_name) = LOWER(:typeName)
    """, nativeQuery = true)
    UUID findIdByTypeName(@Param("typeName") String typeName);

    @Query(value = """
        SELECT EXISTS(
            SELECT id FROM topic_type
                      WHERE LOWER(type_name) = LOWER(:typeName)
                      LIMIT 1
        )
    """, nativeQuery = true)
    boolean existsByTypeName(@Param("typeName") String typeName);

    @Query(value = """
        SELECT EXISTS(
            SELECT id FROM topic_type
                      WHERE LOWER(type_name) = LOWER(:typeName)
                      AND id != :typeId
                      LIMIT 1
        )
    """, nativeQuery = true)
    boolean existsByTypeName(@Param("typeId") UUID typeId, @Param("typeName") String typeName);

    @Query(value = """
        SELECT id FROM topic_type WHERE LOWER(type_name) = LOWER(:topicTypeName)
    """, nativeQuery = true)
    UUID findIdByName(@Param("topicTypeName") String topicTypeName);
}
