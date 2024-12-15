package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.domain.user.dto.request.UserConfirmTokenRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.model.user.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.UUID;


public interface IUserService {

    void registerUser(UserRegisterRequest userRegisterRequest);

    void confirmRegister(String confirmationToken);

    UserConfirmTokenResponse createConfirmationToken(UserConfirmTokenRequest confirmationTokenRequest);

    UserAuthResponse login(UserLoginRequest userLoginRequest);

    void sendMail(String recipientEmail) throws MessagingException;

    void forgotPassword(String email);

    void verifyOtp(String otp);

    void changePassword(UserChangePasswordRequest changePasswordRequest);

    String confirmForgetPassword(String token);

    UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenRequest);

    UserEntity changeProfile(UserChangeProfileRequest changeProfileRequest);

    void changePass(UserChangePasswordRequest changePasswordRequest);

    FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest);

    UserEntity findUser(UserDetails userDetails);

    UserEntity currentUser();

    UserEntity findUserByEmail(String email);

    UserEntity findUserById(UUID userId);

    void logoutUserOf(UserLogoutRequest userLogoutRequest);

    void sendMail(String to, String subject, String body);

    boolean logoutUser();

    boolean existsEmail(String email);

    boolean updatePassword(String otp,String newPassword);

    List<UserEntity> findUsersInactiveForDays(int inactiveDays);

    void sendNotificationEmail(UserEntity user);

}
