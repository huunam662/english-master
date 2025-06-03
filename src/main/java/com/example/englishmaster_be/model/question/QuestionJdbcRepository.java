package com.example.englishmaster_be.model.question;


import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "QUESTION-JDBC-REPOSITORY")
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionJdbcRepository {

    JdbcTemplate jdbcTemplate;

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsertQuestion(List<QuestionEntity> questions){

        if(questions == null || questions.isEmpty()) return;

        String sql = """
                INSERT INTO question(
                    id, number_choice, question_score, create_at, update_at,
                    create_by, update_by, question_title, question_content,
                    content_audio, content_image, question_type, is_question_parent,
                    question_group, part_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int questionsSize = questions.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < questionsSize){

            int endIndex = startIndex + batchSize;

            if(endIndex > questionsSize)
                endIndex = questionsSize;

            List<QuestionEntity> questionsSub = questions.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    QuestionEntity question = questionsSub.get(i);
                    ps.setObject(1, question.getQuestionId());
                    ps.setInt(2, question.getIsQuestionParent() ? 0 : question.getNumberChoice());
                    ps.setInt(3, question.getQuestionScore());
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setObject(6, question.getUserCreate().getUserId());
                    ps.setObject(7, question.getUserCreate().getUserId());
                    ps.setString(8, question.getQuestionTitle());
                    ps.setString(9, question.getQuestionContent());
                    ps.setString(10, question.getContentAudio());
                    ps.setString(11, question.getContentImage());
                    ps.setString(12, question.getQuestionType().name());
                    ps.setBoolean(13, question.getIsQuestionParent());
                    ps.setObject(14, question.getQuestionGroupParent() != null ? question.getQuestionGroupParent().getQuestionId() : null);
                    ps.setObject(15, question.getPart().getPartId());
                }

                @Override
                public int getBatchSize() {

                    return questionsSub.size();
                }
            });

            startIndex = endIndex;
        }
    }

    @Async
    @Transactional
    public void batchInsertQuestionContent(List<QuestionEntity> questions){

        if(questions == null || questions.isEmpty()) return;

        String sql = """
                INSERT INTO question_content(
                    question_id, content_id
                ) VALUES (?, ?)
                """;

        List<Map.Entry<UUID, UUID>> questionContentKeys = questions.stream().filter(
                question -> question.getContentCollection() != null && !question.getContentCollection().isEmpty()
        ).flatMap(
                question -> question.getContentCollection().stream().map(
                        content -> Map.entry(question.getQuestionId(), content.getContentId())
                )
        ).toList();

        int questionContentKeysSize = questionContentKeys.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < questionContentKeysSize) {

            int endIndex = startIndex + batchSize;

            if(endIndex > questionContentKeysSize)
                endIndex = questionContentKeysSize;

            List<Map.Entry<UUID, UUID>> questionContentKeysSub = questionContentKeys.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Map.Entry<UUID, UUID> questionContentKey = questionContentKeysSub.get(i);
                    ps.setObject(1, questionContentKey.getKey());
                    ps.setObject(2, questionContentKey.getValue());
                }

                @Override
                public int getBatchSize() {
                    return questionContentKeysSub.size();
                }
            });

            startIndex = endIndex;
        }
    }

    @Transactional
    public void batchUpdateQuestion(List<QuestionEntity> questions){

        if(questions == null || questions.isEmpty()) return;

        String sql = """
                        UPDATE question
                        SET question_title = :questionTitle,
                            question_content = :questionContent,
                            content_audio = :contentAudio,
                            content_image = :contentImage,
                            update_at = now()
                        WHERE id = :questionId
                    """;

        namedParameterJdbcTemplate.batchUpdate(sql, questions.stream().map(
                question -> new MapSqlParameterSource()
                        .addValue("questionTitle", question.getQuestionTitle())
                        .addValue("questionContent", question.getQuestionContent())
                        .addValue("contentAudio", question.getContentAudio())
                        .addValue("contentImage", question.getContentImage())
                        .addValue("questionId", question.getQuestionId())
                ).toArray(MapSqlParameterSource[]::new)
        );
    }

}
