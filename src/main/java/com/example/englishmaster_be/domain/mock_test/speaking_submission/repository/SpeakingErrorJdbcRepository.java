package com.example.englishmaster_be.domain.mock_test.speaking_submission.repository;

import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.value.AppValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class SpeakingErrorJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AppValue appValue;

    public SpeakingErrorJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate, AppValue appValue) {
        this.jdbcTemplate = jdbcTemplate;
        this.appValue = appValue;
    }

    @Transactional
    public void batchInsertSpeakingError(List<SpeakingErrorEntity> speakingErrors){
        if(speakingErrors == null) return;

        String sql = """
                    INSERT INTO speaking_errors(
                        id, speaking_submission_id, error_type, word,
                        word_recommend, pronunciation, feedback,
                        create_at, update_at
                    )
                    VALUES(
                        :id, :speakingSubmissionId, CAST(:errorType AS speaking_error_type), 
                        :word, :wordRecommend, :pronunciation, :feedback,
                        now(), now()
                    )
                """;

        int speakingErrorsSize = speakingErrors.size();
        int batchSize = appValue.getBatchSize();
        int startIndex = 0;

        while(startIndex < speakingErrorsSize){
            int endIndex = startIndex + batchSize;
            if(endIndex > speakingErrorsSize)
                endIndex = speakingErrorsSize;

            List<SpeakingErrorEntity> speakingErrorSub = speakingErrors.subList(startIndex, endIndex);
            List<MapSqlParameterSource> mapSqlParameters = speakingErrorSub.stream().map(
                    elm -> new MapSqlParameterSource()
                            .addValue("id", elm.getId())
                            .addValue("speakingSubmissionId", elm.getSpeakingSubmission() != null ? elm.getSpeakingSubmission().getId() : elm.getSpeakingSubmissionId())
                            .addValue("errorType", elm.getSpeakingErrorType() != null ? elm.getSpeakingErrorType().name() : null)
                            .addValue("word", elm.getWord())
                            .addValue("wordRecommend", elm.getWordRecommend())
                            .addValue("pronunciation", elm.getPronunciation())
                            .addValue("feedback", elm.getFeedback())
            ).toList();

            jdbcTemplate.batchUpdate(sql, mapSqlParameters.toArray(MapSqlParameterSource[]::new));
            startIndex = endIndex;
        }
    }

}
