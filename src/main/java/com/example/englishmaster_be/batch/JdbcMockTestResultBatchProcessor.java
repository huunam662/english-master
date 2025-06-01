package com.example.englishmaster_be.batch;

import com.example.englishmaster_be.model.mock_test_result.MockTestResultEntity;
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

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JdbcMockTestResultBatchProcessor {

    JdbcTemplate jdbcTemplate;

    AppValue appValue;

    @Transactional
    public void batchInsert(List<MockTestResultEntity> mockTestResults){

        if(mockTestResults == null || mockTestResults.isEmpty()) return;

        String sql = """
                INSERT INTO mock_test_result(
                    id, create_at, update_at, total_score, total_correct,
                    create_by, update_by, mock_test_id, part_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        int mockTestResultsSize = mockTestResults.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while(startIndex < mockTestResultsSize){

            int endIndex = startIndex + batchSize;

            if(endIndex > mockTestResultsSize)
                endIndex = mockTestResultsSize;

            List<MockTestResultEntity> mockTestResultsSub = mockTestResults.subList(startIndex, endIndex);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    MockTestResultEntity mockTestResult = mockTestResultsSub.get(i);
                    ps.setObject(1, mockTestResult.getMockTestResultId());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setInt(4, mockTestResult.getTotalScoreResult());
                    ps.setInt(5, mockTestResult.getTotalCorrect());
                    ps.setObject(6, mockTestResult.getUserCreate().getUserId());
                    ps.setObject(7, mockTestResult.getUserCreate().getUserId());
                    ps.setObject(8, mockTestResult.getMockTest().getMockTestId());
                    ps.setObject(9, mockTestResult.getPart().getPartId());
                }

                @Override
                public int getBatchSize() {
                    return mockTestResultsSub.size();
                }
            });

            startIndex = endIndex;
        }

    }

}
