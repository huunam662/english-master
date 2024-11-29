package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.impl.AnswerBlankService;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/answer-blank")
@RequiredArgsConstructor
public class AnswerBlankController {
    private final AnswerBlankService service;


    @GetMapping("/get-list-answer/{questionId}")
    public ResponseEntity<ResponseModel> getAnswer(@PathVariable UUID questionId){
        ResponseModel responseModel=new ResponseModel();
        Object ob=service.getAnswerWithQuestionBlank(questionId);
        responseModel.setMessage("List answer to question successfully");
        responseModel.setStatus("success");
        responseModel.setResponseData(ob);
        return ResponseEntity.status(HttpStatus.SC_OK).body(responseModel);
    }

    @PostMapping("/create-answer-blank")
    public void createAnswer(@RequestBody UserAnswerRequest request){
        service.createAnswerBlank(request);
    }

}
