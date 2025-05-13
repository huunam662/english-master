package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;


public interface IUserService {

    UserEntity changeProfile(UserChangeProfileRequest changeProfileRequest);

    FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest);

    UserEntity currentUser();

    Boolean currentUserIsAdmin();

    Boolean currentUserIsAdmin(UserDetails userDetails);

    UserEntity getUserById(UUID userId);

    UserEntity getUserByEmail(String email);

    boolean existsEmail(String email);

}
