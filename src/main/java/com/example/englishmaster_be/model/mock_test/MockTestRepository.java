package com.example.englishmaster_be.model.mock_test;

import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MockTestRepository extends JpaRepository<MockTestEntity, UUID> {

    Page<MockTestEntity> findAll(Pageable pageable);

    List<MockTestEntity> findAllByTopic(TopicEntity topic);

    Page<MockTestEntity> findAllByUser(UserEntity user, Pageable pageable);

    Optional<MockTestEntity> findByMockTestId(UUID mockTestId);

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "(:day IS NULL OR DAY(p.createAt) = :day) AND "+
            "p.topic = :topic")
    List<MockTestEntity> findAllByYearMonthAndDay(
            @Param("year") String year,
            @Param("month") String month,
            @Param("day") String day,
            @Param("topic") TopicEntity topic
    );

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "p.topic = :topic")
    List<MockTestEntity> findAllByYearMonth(
            @Param("year") String year,
            @Param("month") String month,
            @Param("topic") TopicEntity topic);

    @Query(value = "SELECT p FROM MockTestEntity p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "p.topic = :topic")
    List<MockTestEntity> findAllByYear(
            @Param("year") String year,
            @Param("topic") TopicEntity topic
    );
}
