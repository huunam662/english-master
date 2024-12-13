package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Model.Request.*;
import com.example.englishmaster_be.Model.Request.User.ChangeProfileRequest;
import com.example.englishmaster_be.Model.Request.User.UserFilterRequest;
import com.example.englishmaster_be.Model.Response.AuthResponse;
import com.example.englishmaster_be.Model.Response.CountMockTestTopicResponse;
import com.example.englishmaster_be.entity.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.UUID;


public interface IUserService {

    void registerUser(UserRegisterRequest userRegisterRequest);

    void confirmRegister(String confirmationToken);

    AuthResponse login(UserLoginRequest userLoginRequest);

    void sendMail(String recipientEmail) throws MessagingException;

    void forgotPassword(String email);

    void verifyOtp(String otp);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    String confirmForgetPassword(String token);

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    UserEntity changeProfile(ChangeProfileRequest changeProfileRequest);

    void changePass(ChangePasswordRequest changePasswordRequest);

    FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest);

    FilterResponse<?> getAllUser(UserFilterRequest filterRequest);

    void deleteUser(UUID userId);

    UserEntity findUser(UserDetails userDetails);

    UserEntity currentUser();

    UserEntity findUserByEmail(String email);

    UserEntity findUserById(UUID userId);

    void logoutUserOf(UserLogoutRequest userLogoutRequest);

    void sendMail(String to, String subject, String body);

    boolean logoutUser();

    boolean existsEmail(String email);

    boolean updatePassword(String otp,String newPassword);

    void enableUser(UUID userId, Boolean enable);

    List<UserEntity> findUsersInactiveForDays(int inactiveDays);

    List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId);

    void sendNotificationEmail(UserEntity user);

    List<UserEntity> findUsersInactiveForDaysAndNotify(int inactiveDays);

    List<UserEntity> getUsersNotLoggedInLast10Days();

    void notifyInactiveUsers();
}
