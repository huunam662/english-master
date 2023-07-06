package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Model.User;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserService {

    User createUser(UserRegisterDTO userRegisterDTO);

    void changePassword(User user, String newpass);

    User findUser(UserDetails userDetails);
}
