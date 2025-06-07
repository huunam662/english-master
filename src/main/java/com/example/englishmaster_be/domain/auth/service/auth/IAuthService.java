package com.example.englishmaster_be.domain.auth.service.auth;

import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;

import java.util.UUID;

public interface IAuthService {

    UserAuthResponse login(UserLoginRequest userLoginRequest);

    void registerUser(UserRegisterRequest userRegisterRequest);

    void confirmRegister(UUID sessionActiveCode);

    void sendOtp(String email);

    void verifyOtp(String otp);

    UserAuthResponse changePassword(UserChangePasswordRequest changePasswordRequest);

    void changePasswordForgot(UserChangePwForgotRequest changePasswordRequest);

    UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenRequest);

    void logoutOf(UserLogoutRequest userLogoutRequest);

}
