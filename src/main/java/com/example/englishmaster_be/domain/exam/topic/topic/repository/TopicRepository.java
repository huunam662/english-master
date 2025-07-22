package com.example.englishmaster_be.domain.exam.topic.topic.repository;

import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicFieldView;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicKeyView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID>, JpaSpecificationExecutor<TopicEntity> {

    @Query(value = """
        SELECT EXISTS(SELECT id FROM topics WHERE id = :topicId)
    """, nativeQuery = true)
    boolean existsById(@Param("topicId") UUID topicId);

    @Query("""
        SELECT workTime from TopicEntity WHERE topicId = :topicId
    """)
    LocalTime findWorkTimeById(@Param("topicId") UUID topicId);

    @Query(value = """
        SELECT topic_image FROM topics WHERE id = :topicId
    """, nativeQuery = true)
    String findTopicImageById(@Param("topicId") UUID topicId);

    @Query(value = """
        SELECT topic_audio FROM topics WHERE id = :topicId
    """, nativeQuery = true)
    String findTopicAudioById(@Param("topicId") UUID topicId);

    Page<TopicEntity> findAll(Pageable pageable);

    List<TopicEntity> findAllByPack(PackEntity pack);

    @Query("SELECT t FROM TopicEntity t WHERE LOWER(t.topicName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TopicEntity> findTopicsByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.topicName = :topicName")
    Optional<TopicEntity> findByTopicName(@Param("topicName") String topicName);

    @Query("""
        SELECT t FROM TopicEntity t
        LEFT JOIN FETCH t.topicType
        WHERE t.topicId = :topicId
    """)
    Optional<TopicEntity> findByTopicId(@Param("topicId") UUID topicId);

    @Query(value = """
        SELECT t.id as topicId, t.pack_id as packId
        FROM topics t
        WHERE LOWER(t.topic_name) = LOWER(:topicName)
        LIMIT 1
    """, nativeQuery = true)
    ITopicKeyView findTopicIdByName(@Param("topicName") String topicName);

    @Query(value = """
        SELECT COALESCE(number_question, 0) FROM topics
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
        ORDER BY t.topicName
    """)
    List<TopicEntity> findAllTopicWithJoinParent(Pageable pageable);

    @Query(value = """
        SELECT t.id as topicId, tt.type_name as topicType
        FROM topics t
        INNER JOIN topic_type tt ON t.topic_type_id = tt.id
        WHERE t.id = :topicId
    """, nativeQuery = true)
    ITopicFieldView findTopicTypeById(@Param("topicId") UUID topicId);

}

