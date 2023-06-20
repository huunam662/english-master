package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseExam;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExamController {

    private ResponseExam response =new ResponseExam();
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseExam test() {

        response.setResult("success");
        return response;
    }
}
