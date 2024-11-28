package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.impl.AnswerBlankService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/answer-blank")
@RequiredArgsConstructor
public class AnswerBlankController {
    private final AnswerBlankService service;


    @GetMapping("get-list-answer/{questionId}")
    public ResponseEntity<ResponseModel> getAnswer(@PathVariable UUID questionId){
        Object ob=service.getAnswerWithQuestionBlank(questionId);

        ResponseModel responseModel = ResponseModel.builder()
                .message("List Answer to Question successfully")
                .responseData(ob)
                .build();

        return ResponseEntity.status(HttpStatus.SC_OK).body(responseModel);
    }

}
