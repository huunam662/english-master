package com.example.englishmaster_be.domain.mock_test.speaking_submission.service;

import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestKeyRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.req.SpeakingSubmitReq;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;

import java.util.List;

public interface ISpeakingSubmissionService {

    MockTestKeyRes speakingSubmitTest(SpeakingSubmitReq speakingSubmitRequest);

    List<SpeakingSubmissionEntity> evaluateSpeakingTest(List<SpeakingSubmissionEntity> speakingSubmissions);
}
