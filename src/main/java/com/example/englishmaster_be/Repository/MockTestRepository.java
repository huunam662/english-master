package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MockTestRepository extends JpaRepository<MockTest, UUID> {
    Page<MockTest> findAll(Pageable pageable);

    List<MockTest> findAllByTopic(Topic topic);

    Page<MockTest> findAllByUser(User user, Pageable pageable);
    MockTest findByMockTestId(UUID mockTestId);

    @Query(value = "SELECT p FROM MockTest p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "(:day IS NULL OR DAY(p.createAt) = :day) AND "+
            "( p.topic = :topic)")
    List<MockTest> findAllByYearMonthAndDay(
            @Param("year") String year,
            @Param("month") String month,
            @Param("day") String day,
            @Param("topic") Topic topic
    );

    @Query(value = "SELECT p FROM MockTest p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "(:month IS NULL OR MONTH(p.createAt) = :month) AND " +
            "( p.topic = :topic)")
    List<MockTest> findAllByYearMonth(
            @Param("year") String year,
            @Param("month") String month,
            @Param("topic") Topic topic);

    @Query(value = "SELECT p FROM MockTest p WHERE " +
            "(:year IS NULL OR YEAR(p.createAt) = :year) AND " +
            "( p.topic = :topic)")
    List<MockTest> findAllByYear(
            @Param("year") String year,
            @Param("topic") Topic topic
    );
}
