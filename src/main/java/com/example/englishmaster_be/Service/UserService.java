package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Model.User;
import org.springframework.stereotype.Service;


public interface UserService {
    ResponseModel registerUser(UserRegisterDTO userRegisterDTO);

    ResponseModel confirmRegister(String confirmationToken);

    ResponseModel loginUser(UserLoginDTO userLoginDTO);

    ResponseModel forgetPassword(String email);
}
