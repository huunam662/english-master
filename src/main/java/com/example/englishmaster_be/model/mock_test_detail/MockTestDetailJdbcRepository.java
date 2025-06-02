package com.example.englishmaster_be.model.mock_test_detail;

import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockTestDetailJdbcRepository {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsertMockTestDetail(List<MockTestDetailEntity> mockTestDetails){

        if(mockTestDetails == null || mockTestDetails.isEmpty()) return;

        String sql = """
                INSERT INTO mock_test_detail(
                    id, answer_content, is_correct_answer, score_achieved,
                    create_at, update_at, create_by, update_by, answer_choice_id,
                    question_child_id, result_mock_test_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int mockTestDetailsSize = mockTestDetails.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < mockTestDetailsSize){

            int endIndex = startIndex + batchSize;

            if(endIndex > mockTestDetailsSize)
                endIndex = mockTestDetailsSize;

            List<MockTestDetailEntity> mockTestDetailsSub = mockTestDetails.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    MockTestDetailEntity mockTestDetail = mockTestDetailsSub.get(i);
                    ps.setObject(1, mockTestDetail.getMockTestDetailId());
                    ps.setString(2, mockTestDetail.getAnswerContent());
                    ps.setBoolean(3, mockTestDetail.getIsCorrectAnswer());
                    ps.setInt(4, mockTestDetail.getScoreAchieved());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setObject(7, mockTestDetail.getUserCreate().getUserId());
                    ps.setObject(8, mockTestDetail.getUserCreate().getUserId());
                    ps.setObject(9, mockTestDetail.getAnswerChoice().getAnswerId());
                    ps.setObject(10, mockTestDetail.getQuestionChild().getQuestionId());
                    ps.setObject(11, mockTestDetail.getResultMockTest().getMockTestResultId());
                }

                @Override
                public int getBatchSize() {
                    return mockTestDetailsSub.size();
                }
            });

            startIndex = endIndex;
        }

    }

}
