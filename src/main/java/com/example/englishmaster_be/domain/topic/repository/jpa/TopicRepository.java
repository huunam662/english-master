package com.example.englishmaster_be.domain.topic.repository.jpa;

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

    Optional<TopicEntity> findByTopicId(UUID topicId);

    @Query("SELECT t.topicImage FROM TopicEntity t order by t.topicId")
    List<String> findAllTopicImages();

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
}

