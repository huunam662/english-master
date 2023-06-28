package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class ExamController {

    private ResponseModel response =new ResponseModel();

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userAccess() {
        return "User Content.";
    }
}
