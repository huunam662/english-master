package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.Mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.entity.AnswerMatchingEntity;
import com.example.englishmaster_be.Model.Response.AnswerMatchingResponse;
import com.example.englishmaster_be.Model.Response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.Service.IAnswerMatching;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Answer Matching")
@RestController
@RequestMapping("/answer-matching")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerMatchingController {

    IAnswerMatching answerMatchingService;


    @PostMapping
    public AnswerMatchingResponse createAnswerMatching(@RequestBody AnswerMatchingQuestionRequest answerMatchingQuestionRequest) {

        AnswerMatchingEntity answerMatching = answerMatchingService.saveAnswerMatching(answerMatchingQuestionRequest);

        return AnswerMatchingMapper.INSTANCE.toAnswerMatchingResponse(answerMatching);
    }

    @GetMapping
    public List<AnswerMatchingBasicResponse> getAnswerMatching(@RequestParam(name = "question_id") UUID questionId) {

        return answerMatchingService.getListAnswerMatchingWithShuffle(questionId);
    }
}
