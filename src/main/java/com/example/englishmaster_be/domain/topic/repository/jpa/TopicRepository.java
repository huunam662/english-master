package com.example.englishmaster_be.domain.topic.repository.jpa;

import com.example.englishmaster_be.domain.topic.dto.projection.ITopicField1Projection;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicKeyProjection;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID>, JpaSpecificationExecutor<TopicEntity> {

    Page<TopicEntity> findAll(Pageable pageable);

    List<TopicEntity> findAllByPack(PackEntity pack);

    @Query("SELECT t FROM TopicEntity t WHERE LOWER(t.topicName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TopicEntity> findTopicsByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.topicName = :topicName")
    Optional<TopicEntity> findByTopicName(@Param("topicName") String topicName);

    @Query("SELECT t FROM TopicEntity t WHERE FUNCTION('DATE', t.startTime) = FUNCTION('DATE', :startTime)")
    List<TopicEntity> findByStartTime(@Param("startTime") LocalDateTime startTime);

    @Query("""
        SELECT t FROM TopicEntity t
        INNER JOIN FETCH t.topicType
        WHERE t.topicId = :topicId
    """)
    Optional<TopicEntity> findByTopicId(@Param("topicId") UUID topicId);

    @Query("""
        SELECT DISTINCT t.topicId as topicId, t.packId as packId
        FROM TopicEntity t
        WHERE LOWER(t.topicName) = LOWER(:topicName)
    """)
    ITopicKeyProjection findTopicIdByName(@Param("topicName") String topicName);

    @Query(value = """
        SELECT number_question FROM topics
        WHERE id = :topicId
    """, nativeQuery = true)
    Integer countQuestionsByTopicId(@Param("topicId") UUID topicId);

    @Query("""
        SELECT t FROM TopicEntity t
        INNER JOIN FETCH t.topicType tt
        INNER JOIN FETCH t.pack tp
        INNER JOIN FETCH tp.packType pt
        WHERE LOWER(tt.topicTypeName) = LOWER(:topicTypeName) 
        ORDER BY t.updateAt
    """)
    List<TopicEntity> findAllTopicWithJoinParent(@Param("topicTypeName") String topicTypeName, Pageable pageable);

    @Query("""
        SELECT t FROM TopicEntity t
        INNER JOIN FETCH t.topicType tt
        INNER JOIN FETCH t.pack tp
        INNER JOIN FETCH tp.packType pt
        WHERE t.topicId = :topicId
        ORDER BY t.updateAt
    """)
    Optional<TopicEntity> findAllTopicWithJoinParent(@Param("topicId") UUID  topicId);

    @Query("""
        SELECT t FROM TopicEntity t
        INNER JOIN FETCH t.topicType tt
        INNER JOIN FETCH t.pack tp
        INNER JOIN FETCH tp.packType pt
        ORDER BY t.updateAt
    """)
    List<TopicEntity> findAllTopicWithJoinParent(Pageable pageable);

    @Query(value = """
        SELECT t.id as topicId, tt.type_name as topicType
        FROM topics t
        INNER JOIN topic_type tt ON t.topic_type_id = tt.id
        WHERE t.id = :topicId
    """, nativeQuery = true)
    ITopicField1Projection findTopicTypeById(@Param("topicId") UUID topicId);

}

