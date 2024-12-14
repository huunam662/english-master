package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.PackEntity;
import com.example.englishmaster_be.entity.TopicEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.*;

public interface TopicRepository extends JpaRepository<TopicEntity, UUID>, QuerydslPredicateExecutor<TopicEntity> {
    Page<TopicEntity> findAll(Pageable pageable);

    List<TopicEntity> findAllByPack(PackEntity pack);
    @Query("SELECT t FROM TopicEntity t WHERE LOWER(t.topicName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TopicEntity> findTopicsByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM TopicEntity t WHERE t.startTime = :startTime")
    List<TopicEntity> findByStartTime(@Param("startTime") LocalDateTime startTime);

    Optional<TopicEntity> findByTopicId(UUID topicId);

    @Query("SELECT t.topicImage FROM TopicEntity t order by t.topicId")
    List<String> findAllTopicImages();
}

