package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.UUID;


public interface IUserService {

    UserEntity saveUser(UserEntity user);

    UserEntity changeProfile(UserChangeProfileRequest changeProfileRequest);

    UserEntity currentUser();

    Boolean currentUserIsAdmin(UserDetails userDetails);

    UserEntity getUserById(UUID userId);

    UserEntity getUserByEmail(String email);

    UserEntity getUserByEmail(String email, Boolean throwable);

    void enabledUser(UUID userId);

    void updateLastLoginTime(UUID userId, LocalDateTime lastLoginTime);

    UserAuthResponse updatePassword(UserEntity user, String oldPassword, String newPassword);

    void updatePasswordForgot(UserEntity user, String newPassword);
}
