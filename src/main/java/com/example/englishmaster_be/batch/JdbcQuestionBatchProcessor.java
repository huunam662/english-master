package com.example.englishmaster_be.batch;

import com.example.englishmaster_be.model.question.QuestionEntity;
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
public class JdbcQuestionBatchProcessor {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsert(List<QuestionEntity> questions){

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
                    ps.setObject(7, question.getUserUpdate().getUserId());
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

}
