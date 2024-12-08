package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.AnswerBlankResponse;
import com.example.englishmaster_be.Model.Response.QuestionBlankResponse;
import com.example.englishmaster_be.Service.IAnswerBlankService;
import com.example.englishmaster_be.Service.impl.AnswerBlankServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Answer Blank")
@RestController
@RequestMapping("/api/answer-blank")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankController {

    IAnswerBlankService answerService;


    @GetMapping("/get-list-answer/{questionId}")
    @MessageResponse("List Answer to Question successfully")
    public List<QuestionBlankResponse> getAnswer(@PathVariable UUID questionId){

        return answerService.getAnswerWithQuestionBlank(questionId);
    }

    @PostMapping("/create-answer-blank")
    public AnswerBlankResponse createAnswer(@RequestBody UserAnswerRequest request){

        return answerService.createAnswerBlank(request);
    }

}
