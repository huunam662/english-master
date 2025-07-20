package com.example.englishmaster_be.domain.mock_test.mock_test.dto.res;

import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res.ReadingListeningSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionResultRes;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class MockTestInforRes {

    private MockTestFullRes mockTestResponse;

    private List<ReadingListeningSubmissionRes> mockTestResultResponses;

    private List<SpeakingSubmissionResultRes> speakingSubmissionResults;
}
