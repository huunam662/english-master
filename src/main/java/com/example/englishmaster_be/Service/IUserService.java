package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Model.Request.*;
import com.example.englishmaster_be.Model.Request.User.ChangePasswordRequest;
import com.example.englishmaster_be.Model.Request.User.ChangeProfileRequest;
import com.example.englishmaster_be.Model.Request.User.UserFilterRequest;
import com.example.englishmaster_be.Model.Response.AuthResponse;
import com.example.englishmaster_be.Model.Response.CountMockTestTopicResponse;
import com.example.englishmaster_be.Model.Response.InformationUserResponse;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.UUID;


public interface IUserService {

    void registerUser(UserRegisterDTO userRegisterDTO);

    void confirmRegister(String confirmationToken);

    AuthResponse login(UserLoginDTO userLoginDTO);

    void forgotPassword(String email);

    void verifyOtp(String otp);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    String confirmForgetPassword(String token);

    AuthResponse refreshToken(RefreshTokenDTO refreshTokenDTO);

    UserEntity changeProfile(ChangeProfileRequest changeProfileRequest);

    void changePass(ChangePasswordRequest changePassDTO);

    FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest);

    FilterResponse<?> getAllUser(UserFilterRequest filterRequest);

    void deleteUser(UUID userId);

    UserEntity findUser(UserDetails userDetails);

    UserEntity currentUser();

    UserEntity findUserByEmail(String email);

    UserEntity findUserById(UUID userId);

    void logoutUserOf(UserLogoutDTO userLogoutDTO);

    boolean logoutUser();

    boolean existsEmail(String email);

    boolean updatePassword(String otp,String newPassword);

    void enableUser(UUID userId, Boolean enable);

    List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId);

}
