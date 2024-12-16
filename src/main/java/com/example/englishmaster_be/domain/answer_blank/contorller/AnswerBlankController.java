package com.example.englishmaster_be.domain.answer_blank.contorller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;

import com.example.englishmaster_be.mapper.AnswerBlankMapper;
import com.example.englishmaster_be.model.answer_blank.AnswerBlankEntity;
import com.example.englishmaster_be.domain.answer_blank.dto.response.AnswerBlankResponse;
import com.example.englishmaster_be.domain.answer_blank.service.IAnswerBlankService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Answer Blank")
@RestController
@RequestMapping("/answer-blank")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankController {

    IAnswerBlankService answerService;


    @GetMapping("/get-list-answer/{questionId}")
    @DefaultMessage("List AnswerEntity to QuestionEntity successfully")
    public List<AnswerBlankResponse> getAnswer(@PathVariable UUID questionId){

        List<AnswerBlankEntity> answerBlankList = answerService.getAnswerBlankListByQuestionBlank(questionId);

        return AnswerBlankMapper.INSTANCE.toAnswerBlankResponseList(answerBlankList);
    }

    @PostMapping("/create-answer-blank")
    public AnswerBlankResponse createAnswer(@RequestBody AnswerBlankRequest request){

        AnswerBlankEntity answerBlank = answerService.saveAnswerBlank(request);

        return AnswerBlankMapper.INSTANCE.toAnswerBlankResponse(answerBlank);
    }

}
