package com.example.englishmaster_be.domain.evaluator_writing.controller;

import com.example.englishmaster_be.domain.evaluator_writing.dto.EvaluatorWritingRequest;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingFeedbackResponse;
import com.example.englishmaster_be.domain.evaluator_writing.dto.WritingPart;
import com.example.englishmaster_be.domain.evaluator_writing.service.EvaluatorWritingService;
import com.example.englishmaster_be.domain.evaluator_writing.service.IEvaluatorWritingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Evaluator Writing")
@RequestMapping("/evaluatorWriting")
public class EvaluatorWritingController {

    IEvaluatorWritingService evaluatorWritingService;
    Logger logger = LoggerFactory.getLogger(EvaluatorWritingService.class);

    public EvaluatorWritingController(IEvaluatorWritingService evaluatorWritingService) {
        this.evaluatorWritingService = evaluatorWritingService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    public WritingFeedbackResponse evaluateWritingTask(@RequestBody EvaluatorWritingRequest evaluatorWritingRequest) {
        return evaluatorWritingService.evaluateEssay(evaluatorWritingRequest);
    }

    @GetMapping("/userSubmissionResult/{mockTestId}")
    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    public List<WritingPart> getUserSubmissionResult(@PathVariable UUID mockTestId) {
        return evaluatorWritingService.getEssaySubmissionResult(mockTestId);
    }
}
