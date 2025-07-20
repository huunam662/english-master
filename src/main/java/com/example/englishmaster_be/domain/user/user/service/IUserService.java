package com.example.englishmaster_be.domain.user.user.service;

import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.user.user.dto.req.UserChangeProfileReq;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.UUID;


public interface IUserService {

    UserEntity saveUser(UserEntity user);

    UserEntity changeProfile(UserChangeProfileReq changeProfileRequest);

    UserEntity currentUser();

    Boolean currentUserIsAdmin(UserDetails userDetails);

    UserEntity getUserById(UUID userId);

    UserEntity getUserByEmail(String email);

    UserEntity getUserByEmail(String email, Boolean throwable);

    void enabledUser(UUID userId);

    void updateLastLoginTime(UUID userId, LocalDateTime lastLoginTime);

    UserAuthRes updatePassword(UserEntity user, String oldPassword, String newPassword);

    void updatePasswordForgot(UserEntity user, String newPassword);

    boolean isExistingEmail(String email);

    void saveAllUsersToExcel(MultipartFile file);

}
