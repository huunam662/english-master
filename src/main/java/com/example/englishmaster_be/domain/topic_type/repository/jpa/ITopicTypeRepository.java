package com.example.englishmaster_be.domain.topic_type.repository.jpa;

import com.example.englishmaster_be.domain.topic_type.dto.response.ITopicTypeKeyProjection;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITopicTypeRepository extends JpaRepository<TopicTypeEntity, UUID> {

    @Query(value = """
        SELECT id as topicTypeId
        FROM topic_type
        WHERE LOWER(type_name) = LOWER(:typeName)
    """, nativeQuery = true)
    ITopicTypeKeyProjection findIdByTypeName(@Param("typeName") String typeName);

}
