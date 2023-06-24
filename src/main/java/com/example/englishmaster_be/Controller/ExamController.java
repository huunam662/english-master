package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExamController {

    private ResponseModel response =new ResponseModel();
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseModel test() {

        response.setStatus("success");
        return response;
    }
}
