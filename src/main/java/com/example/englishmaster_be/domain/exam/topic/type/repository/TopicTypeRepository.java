package com.example.englishmaster_be.domain.exam.topic.type.repository;

import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicTypeRepository extends JpaRepository<TopicTypeEntity, UUID>, QuerydslPredicateExecutor<TopicTypeEntity> {

    @Query("""
        SELECT t FROM TopicTypeEntity t
        LEFT JOIN t.userCreate
        LEFT JOIN t.userUpdate
        WHERE t.topicTypeId = :topicTypeId
    """)
    Optional<TopicTypeEntity> findEntityById(@Param("topicTypeId") UUID id);

    @Query("""
        SELECT t FROM TopicTypeEntity t
        LEFT JOIN t.userCreate
        LEFT JOIN t.userUpdate
    """)
    List<TopicTypeEntity> findAllEntity();

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
