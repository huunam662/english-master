package com.example.englishmaster_be.domain.auth.controller;

import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.auth.service.auth.IAuthService;
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
    public UserAuthResponse login(@RequestBody UserLoginRequest loginDTO) {

        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {

        authService.registerUser(userRegisterRequest);
    }

    @GetMapping("/register/confirm")
    public void confirmRegister(@RequestParam("token") UUID sessionActiveCode) {

        authService.confirmRegister(sessionActiveCode);
    }


    @GetMapping("/mailer/otp")
    public void mailerOtp(@RequestParam("email") String email) {

        authService.sendOtp(email);
    }


    @GetMapping("/verify/otp")
    public void verifyOtp(@RequestParam("otp") String otp) {

        authService.verifyOtp(otp);
    }

    @PostMapping("/change/password")
    public UserAuthResponse changePassword(@Valid @RequestBody UserChangePasswordRequest changePasswordRequest){

        return authService.changePassword(changePasswordRequest);
    }


    @PostMapping("/change/password/forgot")
    public void changePasswordForgot(@Valid @RequestBody UserChangePwForgotRequest changePwForgotRequest) {

        authService.changePasswordForgot(changePwForgotRequest);
    }



    @PostMapping("/refresh/token")
    public UserAuthResponse refreshToken(@RequestBody UserRefreshTokenRequest refreshTokenDTO) {

        return authService.refreshToken(refreshTokenDTO);
    }



    @PostMapping("/logout")
    public void logoutUser(@RequestBody UserLogoutRequest userLogoutDTO) {

        authService.logoutOf(userLogoutDTO);
    }


}
