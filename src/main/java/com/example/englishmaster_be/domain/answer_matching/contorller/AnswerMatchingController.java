package com.example.englishmaster_be.domain.answer_matching.contorller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingResponse;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.domain.answer_matching.service.IAnswerMatchingService;
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

    IAnswerMatchingService answerMatchingService;


    @PostMapping
    @DefaultMessage("Save successfully")
    public AnswerMatchingResponse createAnswerMatching(@RequestBody AnswerMatchingQuestionRequest answerMatchingQuestionRequest) {

        AnswerMatchingEntity answerMatching = answerMatchingService.saveAnswerMatching(answerMatchingQuestionRequest);

        return AnswerMatchingMapper.INSTANCE.toAnswerMatchingResponse(answerMatching);
    }

    @GetMapping
    @DefaultMessage("Save successfully")
    public List<AnswerMatchingBasicResponse> getAnswerMatching(@RequestParam(name = "question_id") UUID questionId) {

        return answerMatchingService.getListAnswerMatchingWithShuffle(questionId);
    }
}
