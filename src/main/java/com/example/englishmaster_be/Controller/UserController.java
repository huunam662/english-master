package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.UserLoginDTO;
import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseModel register(@RequestBody UserRegisterDTO registerDTO) {
        return userService.registerUser(registerDTO);
    }

    @GetMapping( "/register/confirm")
    public ResponseModel confirmRegister(@RequestParam("token")String confirmationToken) {
        return userService.confirmRegister(confirmationToken);
    }

    @PostMapping ( "/login")
    public ResponseModel login(@RequestBody UserLoginDTO loginDTO) {
        return userService.loginUser(loginDTO);
    }
}
