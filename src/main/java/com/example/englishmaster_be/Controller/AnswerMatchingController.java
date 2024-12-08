package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.Model.Answer;
import com.example.englishmaster_be.Model.AnswerMatching;
import com.example.englishmaster_be.Service.IAnswerMatching;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/answer-matching")
@RequiredArgsConstructor
public class AnswerMatchingController {

    private final IAnswerMatching service;

    @PostMapping("/create-answer-matching")
    public ResponseEntity<AnswerMatching> createAnswerMatching(@RequestBody AnswerMatchingRequest request) {
        return ResponseEntity.ok(service.createAnswerMatching(request));
    }

    @GetMapping("/get-answer-matching")
    public ResponseEntity<Map<String,String>> getAnswerMatching(@RequestParam(name = "question_id") UUID questionId) {
        return ResponseEntity.ok(service.getListAnswerMatching(questionId));
    }
}
