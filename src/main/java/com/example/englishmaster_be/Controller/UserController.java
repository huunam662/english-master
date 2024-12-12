package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.UserMapper;
import com.example.englishmaster_be.Model.Request.*;
import com.example.englishmaster_be.Model.Request.User.ChangeProfileRequest;
import com.example.englishmaster_be.Model.Request.User.UserFilterRequest;
import com.example.englishmaster_be.Model.Response.AuthResponse;
import com.example.englishmaster_be.Model.Response.InformationUserResponse;
import com.example.englishmaster_be.Service.IUserService;
import com.example.englishmaster_be.entity.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    IUserService userService;


    @PostMapping("/register")
    @MessageResponse("Sent a confirmation mail")
    public void register(
            @Valid @RequestBody UserRegisterRequest registerDTO
    ) {

        userService.registerUser(registerDTO);
    }

    @GetMapping("/register/confirm")
    @MessageResponse("Account has been successfully verified")
    public void confirmRegister(@RequestParam("token") String confirmationToken) {

        userService.confirmRegister(confirmationToken);
    }

    @PostMapping("/login")
    @MessageResponse("login successfully")
    public AuthResponse login(@RequestBody UserLoginRequest loginDTO) {

        return userService.login(loginDTO);
    }

    @PostMapping("/forgetPassword")
    @MessageResponse("Please check your email to get OTP code for verify your account")
    public void forgetPassword(@RequestParam("email") String email) {

        userService.forgotPassword(email);
    }

    @PostMapping("/verifyOtp")
    @MessageResponse("Your verify is successfully")
    public void verifyOtp(@RequestParam String otp) {

        userService.verifyOtp(otp);
    }

    @PostMapping("/changePassword")
    @MessageResponse("Update your password successfully")
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordDTO) {

        userService.changePassword(changePasswordDTO);
    }


    @GetMapping("/forgetPass/confirm")
    @MessageResponse("Confirm successfully")
    public String confirmForgetPassword(@RequestParam String token) {

        return userService.confirmForgetPassword(token);
    }


    @PostMapping("/refreshToken")
    @MessageResponse("Created new access token")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenDTO) {

        return userService.refreshToken(refreshTokenDTO);
    }


    @GetMapping("/information")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Information UserEntity successfully")
    public InformationUserResponse informationUser() {

        UserEntity currentUser = userService.currentUser();

        return UserMapper.INSTANCE.toInformationUserResponse(currentUser);
    }

    @PatchMapping(value = "/changeProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Change profile UserEntity successfully")
    public InformationUserResponse changeProfile(@ModelAttribute("profileUser") ChangeProfileRequest changeProfileRequest) {

        UserEntity user = userService.changeProfile(changeProfileRequest);

        return UserMapper.INSTANCE.toInformationUserResponse(user);
    }


    @PatchMapping(value = "/changePass")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Change pass UserEntity successfully")
    public void changePass(@RequestBody com.example.englishmaster_be.Model.Request.User.ChangePasswordRequest changePassDTO) {

        userService.changePass(changePassDTO);
    }


    @GetMapping(value = "/listExamResultsUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show list exam result successfully")
    public FilterResponse<?> getExamResultsUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection
    ) {

        UserFilterRequest userFilterRequest = UserFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return userService.getExamResultsUser(userFilterRequest);
    }

    @PostMapping("/logout")
    @MessageResponse("Logout successfully")
    public void logoutUser(@RequestBody UserLogoutRequest userLogoutDTO) {

        userService.logoutUserOf(userLogoutDTO);
    }

}
