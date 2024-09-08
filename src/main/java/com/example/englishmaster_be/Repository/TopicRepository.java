package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.*;

public interface TopicRepository extends JpaRepository<Topic, UUID>, QuerydslPredicateExecutor<Topic> {
    Page<Topic> findAll(Pageable pageable);

    List<Topic> findAllByPack(Pack pack);
    @Query("SELECT t FROM Topic t WHERE LOWER(t.topicName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Topic> findTopicsByQuery(@Param("query") String query, Pageable pageable);

    Optional<Topic> findByTopicId(UUID topicId);

    @Query("SELECT t.topicImage FROM Topic t order by t.topicId")
    List<String> findAllTopicImages();



}
