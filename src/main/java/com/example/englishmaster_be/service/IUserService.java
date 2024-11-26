package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.*;
import com.example.englishmaster_be.model.response.UserResponse;
import com.example.englishmaster_be.model.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.IOException;
import java.util.UUID;


public interface IUserService {

    UserResponse createUser(UserRegisterDTO userRegisterDTO);

    void save(User user);

    void delete(User user);

    void changePassword(User user, String newpass);

    User findUser(UserDetails userDetails);

    User currentUser();

    User findUserByEmail(String email);

    User findUserById(UUID userId);

    void logoutUser();

    boolean existsEmail(String email);

    boolean updatePassword(String otp,String newPassword);

}
