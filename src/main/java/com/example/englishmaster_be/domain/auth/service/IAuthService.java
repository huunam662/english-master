package com.example.englishmaster_be.domain.auth.service;

import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;

public interface IAuthService {

    UserAuthResponse login(UserLoginRequest userLoginRequest);

    void registerUser(UserRegisterRequest userRegisterRequest);

    void confirmRegister(String confirmationToken);

    void forgotPassword(String email);

    void verifyOtp(String otp);

    void changePass(UserChangePasswordRequest changePasswordRequest);

    void changePassword(UserChangePasswordRequest changePasswordRequest);

    String confirmForgetPassword(String token);

    UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenRequest);

    void logoutOf(UserLogoutRequest userLogoutRequest);

    boolean logoutUser();

    boolean updatePassword(String otp,String newPassword);

    UserConfirmTokenResponse createConfirmationToken(UserConfirmTokenRequest confirmationTokenRequest);

}
