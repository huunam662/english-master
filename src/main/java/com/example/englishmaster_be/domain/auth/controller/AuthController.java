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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



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
    public void confirmRegister(@RequestParam("token") String confirmationToken) {

        authService.confirmRegister(confirmationToken);
    }


    @PostMapping("/forgetPassword")
    @DefaultMessage("Please check your email to get OTP code for verify your account")
    public void forgetPassword(@RequestParam("email") String email) {

        authService.forgotPassword(email);
    }


    @PostMapping("/verifyOtp")
    @DefaultMessage("Your verify is successfully")
    public void verifyOtp(@RequestParam String otp) {

        authService.verifyOtp(otp);
    }


    @PostMapping("/changePassword")
    @DefaultMessage("Update your password successfully")
    public void changePassword(@RequestBody UserChangePasswordRequest changePasswordDTO) {

        authService.changePassword(changePasswordDTO);
    }


    @GetMapping("/forgetPass/confirm")
    @DefaultMessage("Confirm successfully")
    public String confirmForgetPassword(@RequestParam String token) {

        return authService.confirmForgetPassword(token);
    }


    @PostMapping("/refreshToken")
    @DefaultMessage("Created new access token")
    public UserAuthResponse refreshToken(@RequestBody UserRefreshTokenRequest refreshTokenDTO) {

        return authService.refreshToken(refreshTokenDTO);
    }


    @PatchMapping(value = "/changePass")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Change pass UserEntity successfully")
    public void changePass(@RequestBody UserChangePasswordRequest changePasswordRequest) {

        authService.changePass(changePasswordRequest);
    }


    @PostMapping("/logout")
    @DefaultMessage("Logout successfully")
    public void logoutUser(@RequestBody UserLogoutRequest userLogoutDTO) {

        authService.logoutOf(userLogoutDTO);
    }


}
