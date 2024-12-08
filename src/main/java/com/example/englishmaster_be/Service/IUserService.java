package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.DTO.User.ChangePassDTO;
import com.example.englishmaster_be.DTO.User.ChangeProfileDTO;
import com.example.englishmaster_be.DTO.User.UserFilterRequest;
import com.example.englishmaster_be.Model.Response.AuthResponse;
import com.example.englishmaster_be.Model.Response.CountMockTestTopicResponse;
import com.example.englishmaster_be.Model.Response.InformationUserResponse;
import com.example.englishmaster_be.Model.User;
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

    InformationUserResponse informationCurrentUser();

    InformationUserResponse informationUserOf(User user);

    InformationUserResponse changeProfile(ChangeProfileDTO changeProfileDTO);

    void changePass(ChangePassDTO changePassDTO);

    FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest);

    FilterResponse<?> getAllUser(UserFilterRequest filterRequest);

    void deleteUser(UUID userId);

    User findUser(UserDetails userDetails);

    User currentUser();

    User findUserByEmail(String email);

    User findUserById(UUID userId);

    void logoutUserOf(UserLogoutDTO userLogoutDTO);

    boolean logoutUser();

    boolean existsEmail(String email);

    boolean updatePassword(String otp,String newPassword);

    void enableUser(UUID userId, Boolean enable);

    List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId);

}
