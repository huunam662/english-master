package com.example.englishmaster_be.domain.mock_test.speaking_submission.repository;

import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.value.AppValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class SpeakingSubmissionJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final AppValue appValue;

    public SpeakingSubmissionJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, AppValue appValue) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.appValue = appValue;
    }

    @Transactional
    public void batchInsertSpeakingSubmission(List<SpeakingSubmissionEntity> speakingSubmissions){
        if(speakingSubmissions == null) return;

        String sql = """
                    INSERT INTO speaking_submissions(
                        id, question_id, mock_test_id, audio_url,
                        speaking_text, feedback, reached_percent, level,
                        status, create_at, update_at
                    )
                    VALUES(
                        :id, :questionId, :mockTestId, :audioUrl,
                        :speakingText, :feedback, :reachedPercent, 
                        CAST(:levelSpeaker AS level_speaker_type), 
                        CAST(:status AS status_submission_type), 
                        now(), now()
                    )
                """;

        int speakingSubmissionSize = speakingSubmissions.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while (startIndex < speakingSubmissionSize) {
            int endIndex = startIndex + batchSize;
            if(endIndex > speakingSubmissionSize)
                endIndex = speakingSubmissionSize;

            List<SpeakingSubmissionEntity> speakingSubmissionSub = speakingSubmissions.subList(startIndex, endIndex);

            List<MapSqlParameterSource> mapSqlParameters = speakingSubmissionSub.stream().map(
                    elm -> new MapSqlParameterSource()
                            .addValue("id", elm.getId())
                            .addValue("questionId", elm.getQuestion() != null ? elm.getQuestion().getQuestionId() : elm.getQuestionId())
                            .addValue("mockTestId", elm.getMockTest() != null ? elm.getMockTest().getMockTestId() : elm.getMockTestId())
                            .addValue("audioUrl", elm.getAudioUrl())
                            .addValue("speakingText", elm.getSpeakingText())
                            .addValue("feedback", elm.getFeedback())
                            .addValue("levelSpeaker", elm.getLevelSpeaker() != null ? elm.getLevelSpeaker().getLevel() : null)
                            .addValue("reachedPercent", elm.getReachedPercent() != null ? elm.getReachedPercent() : 0)
                            .addValue("status", elm.getStatus().name())
            ).toList();

            namedParameterJdbcTemplate.batchUpdate(sql, mapSqlParameters.toArray(MapSqlParameterSource[]::new));

            startIndex = endIndex;
        }
    }

    @Transactional
    public void updateSpeakingSubmission(UUID speakingSubmissionId, String speakingText){
        if(speakingSubmissionId == null) return;

        String sql = """
                    UPDATE speaking_submissions
                    SET speaking_text = :speakingText,
                        update_at = now()
                    WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("speakingText", speakingText)
                        .addValue("id", speakingSubmissionId)
        );
    }

    @Transactional
    public void updateSpeakingSubmission(SpeakingSubmissionEntity speakingSubmission){
        if(speakingSubmission == null) return;

        String sql = """
                    UPDATE speaking_submissions
                    SET audio_url = :audioUrl,
                        speaking_text = :speakingText,
                        feedback = :feedback,
                        reached_percent = :reachedPercent,
                        level = CAST(:levelSpeaker AS level_speaker_type),
                        status = CAST(:status AS status_submission_type),
                        update_at = now()
                    WHERE id = :id
                """;

        namedParameterJdbcTemplate.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("audioUrl", speakingSubmission.getAudioUrl())
                        .addValue("speakingText", speakingSubmission.getSpeakingText())
                        .addValue("feedback", speakingSubmission.getFeedback())
                        .addValue("reachedPercent", speakingSubmission.getReachedPercent())
                        .addValue("levelSpeaker", speakingSubmission.getLevelSpeaker().getLevel())
                        .addValue("status", speakingSubmission.getStatus().name())
                        .addValue("id", speakingSubmission.getId())
        );

    }

}
