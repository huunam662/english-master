package com.example.englishmaster_be.domain.user.auth.controller;

import com.example.englishmaster_be.domain.user.auth.service.auth.IAuthService;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.user.auth.dto.req.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public UserAuthRes login(@RequestBody @Valid UserLoginReq loginDTO) {

        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    public void register(
            @Valid @RequestBody UserRegisterReq userRegisterRequest
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
    public UserAuthRes changePassword(@Valid @RequestBody UserChangePasswordReq changePasswordRequest){

        return authService.changePassword(changePasswordRequest);
    }


    @PostMapping("/change/password/forgot")
    public void changePasswordForgot(@Valid @RequestBody UserChangePwForgotReq changePwForgotRequest) {

        authService.changePasswordForgot(changePwForgotRequest);
    }



    @PostMapping("/refresh/token")
    public UserAuthRes refreshToken(@RequestBody UserRefreshTokenReq refreshTokenDTO) {

        return authService.refreshToken(refreshTokenDTO);
    }



    @PostMapping("/logout")
    public void logoutUser(@RequestBody UserLogoutReq userLogoutDTO) {

        authService.logoutOf(userLogoutDTO);
    }


}
