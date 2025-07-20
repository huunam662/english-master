package com.example.englishmaster_be.domain.user.auth.service.auth;

import com.example.englishmaster_be.domain.user.auth.dto.req.*;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;

import java.util.UUID;

public interface IAuthService {

    UserAuthRes login(UserLoginReq userLoginRequest);

    void registerUser(UserRegisterReq userRegisterRequest);

    void confirmRegister(UUID sessionActiveCode);

    void sendOtp(String email);

    void verifyOtp(String otp);

    UserAuthRes changePassword(UserChangePasswordReq changePasswordRequest);

    void changePasswordForgot(UserChangePwForgotReq changePasswordRequest);

    UserAuthRes refreshToken(UserRefreshTokenReq refreshTokenRequest);

    void logoutOf(UserLogoutReq userLogoutRequest);

}
