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
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcQuestionContentBatchProcessor {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsert(List<QuestionEntity> questions){

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

}
