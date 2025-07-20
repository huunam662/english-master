package com.example.englishmaster_be.domain.mock_test.speaking_submission.controller;

import com.example.englishmaster_be.domain.mock_test.mock_test.dto.res.MockTestKeyRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.req.SpeakingSubmitReq;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.service.ISpeakingSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/speaking-submission")
@Tag(name = "Speaking submission")
public class SpeakingSubmissionController {

    private final ISpeakingSubmissionService speakingSubmissionService;

    public SpeakingSubmissionController(ISpeakingSubmissionService speakingSubmissionService) {
        this.speakingSubmissionService = speakingSubmissionService;
    }

    @PostMapping("/submit-test")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Submit speaking recording for mock test.",
            description = "Submit speaking recording for mock test."
    )
    public MockTestKeyRes speakingSubmission(@RequestBody @Valid SpeakingSubmitReq request) {

        return speakingSubmissionService.speakingSubmitTest(request);
    }

}
