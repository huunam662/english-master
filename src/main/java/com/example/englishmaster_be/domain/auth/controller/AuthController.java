package com.example.englishmaster_be.domain.auth.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.auth.service.IAuthService;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {


    IAuthService authService;


    @PostMapping("/login")
    @DefaultMessage("Đăng nhập thành công")
    public UserAuthResponse login(@RequestBody UserLoginRequest loginDTO) {

        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    @DefaultMessage("Kiểm tra Email của bạn để xác thực đăng ký")
    public void register(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {

        authService.registerUser(userRegisterRequest);
    }

    @GetMapping("/register/confirm")
    @DefaultMessage("Xác thực đăng ký thành công. Cảm ơn bạn, một thành viên của chúng tôi")
    public void confirmRegister(@RequestParam("token") UUID sessionActiveCode) {

        authService.confirmRegister(sessionActiveCode);
    }


    @PostMapping("/forgot/password")
    @DefaultMessage("Hãy kiểm tra email của bạn để nhận mã xác thực")
    public void forgetPassword(@RequestParam("email") String email) {

        authService.forgotPassword(email);
    }


    @PostMapping("/verify/otp")
    @DefaultMessage("Xác thực mã OTP thành công")
    public void verifyOtp(@RequestParam String otp) {

        authService.verifyOtp(otp);
    }

    @PostMapping("/change/password")
    @DefaultMessage("Cập nhật mật khẩu thành công")
    public UserAuthResponse changePassword(@Valid @RequestBody UserChangePasswordRequest changePasswordRequest){

        return authService.changePassword(changePasswordRequest);
    }


    @PostMapping("/change/password/forgot")
    @DefaultMessage("Cập nhật mật khẩu thành công")
    public UserAuthResponse changePasswordForgot(@Valid @RequestBody UserChangePwForgotRequest changePwForgotRequest) {

        return authService.changePasswordForgot(changePwForgotRequest);
    }



    @PostMapping("/refresh/token")
    @DefaultMessage("Mã truy cập được tạo thành công")
    public UserAuthResponse refreshToken(@RequestBody UserRefreshTokenRequest refreshTokenDTO) {

        return authService.refreshToken(refreshTokenDTO);
    }



    @PostMapping("/logout")
    @DefaultMessage("Đăng xuất thành công")
    public void logoutUser(@RequestBody UserLogoutRequest userLogoutDTO) {

        authService.logoutOf(userLogoutDTO);
    }


}
