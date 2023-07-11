package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;


public interface IUserService {

    User createUser(UserRegisterDTO userRegisterDTO);

    void changePassword(User user, String newpass);

    User findUser(UserDetails userDetails);

    User currentUser();

    User findUserById(UUID userId);
}
