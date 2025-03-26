package com.example.englishmaster_be.model.mock_test;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class MockTestJdbcRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public MockTestJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insertMockTest(UUID id, UUID userId, UUID topicId, Integer totalScore, Float correctPercent) {
        String sql = """
                    INSERT INTO mock_test (id, user_id, topic_id, total_score, answers_correct_percent,
                    create_at, update_at)
                    VALUES (:id, :userId, :topicId, :totalScore, :correctPercent, :now, :now)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("topicId", topicId)
                .addValue("totalScore", totalScore)
                .addValue("correctPercent", correctPercent)
                .addValue("now", Timestamp.valueOf(LocalDateTime.now()));

        jdbc.update(sql, params);
    }
}
