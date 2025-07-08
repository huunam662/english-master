package com.example.englishmaster_be.domain.speaking_submission.service;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingSubmitRequest;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;

import java.util.List;

public interface ISpeakingSubmissionService {

    MockTestKeyResponse speakingSubmitTest(SpeakingSubmitRequest speakingSubmitRequest);

    List<SpeakingSubmissionEntity> evaluateSpeakingTest(List<SpeakingSubmissionEntity> speakingSubmissions);
}
