package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Model.Response.QuestionBlankResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.impl.AnswerBlankService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/answer-blank")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerBlankController {

    AnswerBlankService service;

    @GetMapping("get-list-answer/{questionId}")
    @MessageResponse("List Answer to Question successfully")
    public List<QuestionBlankResponse> getAnswer(@PathVariable UUID questionId){
        return service.getAnswerWithQuestionBlank(questionId);
    }

    @PostMapping("/create-answer-blank")
    public void createAnswer(@RequestBody UserAnswerRequest request){
        service.createAnswerBlank(request);
    }

}
