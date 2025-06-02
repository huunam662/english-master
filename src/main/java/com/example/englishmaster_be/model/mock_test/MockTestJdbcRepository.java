package com.example.englishmaster_be.model.mock_test;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestJdbcRepository {

    NamedParameterJdbcTemplate jdbc;

    JdbcTemplate jdbcTemplate;

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

    public UUID insertMockTest(LocalTime workTime, LocalTime finishTime, UUID userId, UUID topicId){

        String sql = """
                    INSERT INTO mock_test(id, work_time, finish_time, user_id, topic_id, create_at, update_at)
                    VALUES(?, ?, ?, ?, ?, now(), now())
                """;

        UUID mockTestId = UUID.randomUUID();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setObject(1, mockTestId);
            ps.setObject(2, Time.valueOf(workTime));
            ps.setObject(3, Time.valueOf(finishTime));
            ps.setObject(4, userId);
            ps.setObject(5, topicId);
            return ps;
        });

        return mockTestId;
    }
}
