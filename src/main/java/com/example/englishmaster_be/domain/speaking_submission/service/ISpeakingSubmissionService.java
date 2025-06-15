package com.example.englishmaster_be.domain.speaking_submission.service;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingSubmitRequest;

public interface ISpeakingSubmissionService {

    MockTestKeyResponse speakingSubmitTest(SpeakingSubmitRequest speakingSubmitRequest) throws InterruptedException;
}
