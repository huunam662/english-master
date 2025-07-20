package com.example.englishmaster_be.domain.mock_test.evaluator_writing.repository.jdbc;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class EssaySubmissionJdbcRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public EssaySubmissionJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insertEssaySubmission(String userEssay, String essayFeedback,
                                      UUID questionId, UUID userId) {
        UUID id = UUID.randomUUID();
        String sql = """
        INSERT INTO essay_submission (id, user_essay, essay_feedback, question_id, user_id)
        VALUES (:id, :userEssay, :essayFeedback, :questionId, :userId)
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userEssay", userEssay)
                .addValue("essayFeedback", essayFeedback)
                .addValue("questionId", questionId)
                .addValue("userId", userId);

        jdbc.update(sql, params);
    }
}
