package com.example.englishmaster_be.batch;

import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcAnswerBatchProcessor {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsert(List<AnswerEntity> answers) {

        String sql = """
                INSERT INTO answer(
                    id, correct_answer, create_at, update_at,
                    create_by, update_by, content, explain_details,
                    question_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int answersSize = answers.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < answersSize) {

            int endIndex = startIndex + batchSize;

            if(endIndex > answersSize)
                endIndex = answersSize;

            List<AnswerEntity> answersSub = answers.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AnswerEntity answer = answersSub.get(i);
                    ps.setObject(1, answer.getAnswerId());
                    ps.setBoolean(2, answer.getCorrectAnswer());
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setObject(5, answer.getUserCreate().getUserId());
                    ps.setObject(6, answer.getUserUpdate().getUserId());
                    ps.setString(7, answer.getAnswerContent());
                    ps.setString(8, answer.getExplainDetails());
                    ps.setObject(9, answer.getQuestion().getQuestionId());
                }

                @Override
                public int getBatchSize() {

                    return answersSub.size();
                }
            });

            startIndex = endIndex;
        }
    }
}
