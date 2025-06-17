package com.example.englishmaster_be.domain.speaking_submission.controller;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingSubmitRequest;
import com.example.englishmaster_be.domain.speaking_submission.service.ISpeakingSubmissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/speaking-submission")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Speaking submission")
public class SpeakingSubmissionController {

    ISpeakingSubmissionService speakingSubmissionService;

    @PostMapping("/submit-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public MockTestKeyResponse speakingSubmission(@RequestBody @Valid SpeakingSubmitRequest request) {

        return speakingSubmissionService.speakingSubmitTest(request);
    }

}
