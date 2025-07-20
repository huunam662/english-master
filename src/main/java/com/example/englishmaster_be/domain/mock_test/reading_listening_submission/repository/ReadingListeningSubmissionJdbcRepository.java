package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.repository;

import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.value.AppValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ReadingListeningSubmissionJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final IUserService userService;
    private final AppValue appValue;

    public ReadingListeningSubmissionJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, IUserService userService, AppValue appValue) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
        this.appValue = appValue;
    }

    @Transactional
    public void batchInsert(List<ReadingListeningSubmissionEntity> readingListeningSubmissions) {
        UserEntity currentUser = userService.currentUser();
        if(readingListeningSubmissions == null || readingListeningSubmissions.isEmpty()) return;
        String sql = """
                    INSERT INTO reading_listening_submissions(
                        id, answer_content, is_correct_answer, score_achieved,
                        answer_choice_id, mock_test_id, create_by, update_by, create_at, update_at
                    )
                    VALUES(
                        :id, :answerContent, :isCorrectAnswer, :scoreAchieved,
                        :answerChoiceId, :mockTestId, :createBy, :updateBy, now(), now()
                    )
                """;
        int listSize = readingListeningSubmissions.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;
        while (startIndex < listSize){
            int endIndex = Math.min(startIndex + batchSize, listSize);
            List<ReadingListeningSubmissionEntity> mockTestDetailsSub = readingListeningSubmissions.subList(startIndex, endIndex);
            List<MapSqlParameterSource> params = mockTestDetailsSub.stream().map(
                    m -> new MapSqlParameterSource()
                            .addValue("id", m.getId())
                            .addValue("answerContent", m.getAnswerContent())
                            .addValue("isCorrectAnswer", m.getIsCorrectAnswer())
                            .addValue("scoreAchieved", m.getScoreAchieved())
                            .addValue("answerChoiceId", m.getAnswerChoice().getAnswerId())
                            .addValue("mockTestId", m.getMockTest().getMockTestId())
                            .addValue("createBy", currentUser.getUserId())
                            .addValue("updateBy", currentUser.getUserId())
            ).toList();
            jdbcTemplate.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
            startIndex = endIndex;
        }
    }

}
