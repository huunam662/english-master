package com.example.englishmaster_be.domain.auth.service;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.role.RoleRepository;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
import com.example.englishmaster_be.helper.AuthHelper;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.shared.service.mailer.MailerService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {


    JwtService jwtUtil;

    MailerService mailerUtil;

    AuthHelper authUtil;

    AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder;

    IUserService userService;

    ISessionActiveService sessionActiveService;

    IInvalidTokenService invalidTokenService;

    IOtpService otpService;


    @Transactional
    @Override
    public UserAuthResponse login(UserLoginRequest userLoginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled())
            throw new ErrorHolder(Error.ACCOUNT_DISABLED);

        UserEntity user = (UserEntity) userDetails;

        String jwtToken = jwtUtil.generateToken(userDetails);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActive.getCode(), jwtToken, user);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        UserEntity user = userService.getUserByEmail(userRegisterRequest.getEmail(), false);

        if(user != null && user.getEnabled())
            throw new ErrorHolder(Error.BAD_REQUEST, "Email is used.");

        UserEntity userRegister = UserMapper.INSTANCE.toUserEntity(userRegisterRequest);

        if(user != null) userRegister.setUserId(user.getUserId());

        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setEnabled(false);

        if(user != null && user.getConfirmTokens() != null)
            sessionActiveService.deleteByUserIdAndType(user.getUserId(), SessionActiveType.CONFIRM);

        userRegister = userService.saveUser(userRegister);

        SessionActiveEntity sessionConfirm = sessionActiveService.saveForUser(userRegister, SessionActiveType.CONFIRM);

        try {
            mailerUtil.sendConfirmRegister(userRegister.getEmail(), sessionConfirm.getCode().toString());
        } catch (IOException | MessagingException e) {
            throw new ErrorHolder(Error.SEND_EMAIL_FAILURE);
        }
    }


    @Transactional
    @Override
    public void confirmRegister(UUID sessionActiveCode) {

        SessionActiveEntity sessionConfirm = sessionActiveService.getByCodeAndType(sessionActiveCode, SessionActiveType.CONFIRM);

        if ((sessionConfirm.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Verification session had been expired, try again.");

        UserEntity user = sessionConfirm.getUser();

        userService.enabledUser(user.getUserId());

        sessionActiveService.deleteByUserIdAndType(user.getUserId(), SessionActiveType.CONFIRM);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void forgotPassword(String email) {

        if (email == null || email.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Email is required.");

        boolean emailExisting = userService.existsEmail(email);

        if(!emailExisting)
            throw new ErrorHolder(Error.BAD_REQUEST, "Your email does not existing.");

        OtpEntity otpEntity = otpService.generateOtp(email);

        mailerUtil.sendOtpToEmail(email, otpEntity.getOtp());

    }


    @Transactional
    @Override
    public void verifyOtp(String otp) {

        if (otp == null || otp.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "OTP code is required.");

        OtpEntity otpEntity = otpService.getByOtp(otp);

        if (!otpService.isValidOtp(otpEntity))
            throw new ErrorHolder(Error.BAD_REQUEST, "OTP is expired.");

        otpService.updateOtpStatus(otpEntity.getEmail(), otp, OtpStatus.VERIFIED);
    }


    @Transactional
    @Override
    public void changePasswordForgot(UserChangePwForgotRequest changePasswordRequest) {

        OtpEntity otpEntity = otpService.getByOtp(changePasswordRequest.getOtpCode());

        authUtil.saveNewPassword(otpEntity.getUser(), changePasswordRequest.getNewPassword(), changePasswordRequest.getOtpCode());
    }

    @Transactional
    @Override
    public UserAuthResponse changePassword(UserChangePasswordRequest changePasswordRequest) {

        UserEntity user = userService.currentUser();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Old password invalid.");

        return authUtil.saveNewPassword(user, changePasswordRequest.getNewPassword(), changePasswordRequest.getOtpCode());
    }


    @Transactional
    @Override
    public UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenDTO) {

        UUID refreshToken = refreshTokenDTO.getRequestRefresh();

        SessionActiveEntity sessionActive = sessionActiveService.getByCodeAndType(refreshToken, SessionActiveType.REFRESH_TOKEN);

        if (sessionActive == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid refresh.");

        if(sessionActiveService.isExpirationToken(sessionActive))
            throw new ErrorHolder(Error.BAD_REQUEST, "Refresh session was expired.");

        String newToken = jwtUtil.generateToken(sessionActive.getUser());

        SessionActiveEntity sessionActiveNew = sessionActiveService.saveSessionActive(sessionActive.getUser(), newToken);

        invalidTokenService.saveInvalidToken(
                refreshTokenDTO.getRequestToken(),
                sessionActive.getUser().getUserId(),
                InvalidTokenType.EXPIRED
        );

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActiveNew.getCode(), newToken, sessionActive.getUser());
    }


    @Override
    public void logoutOf(UserLogoutRequest userLogoutRequest) {

        UserEntity user = userService.currentUser();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(userLogoutRequest.getRefreshToken());

        if(sessionActive != null){

            invalidTokenService.saveInvalidToken(userLogoutRequest.getAccessToken(), user.getUserId(), InvalidTokenType.LOGOUT);

            sessionActiveService.deleteByToken(userLogoutRequest.getAccessToken());

            sessionActiveService.deleteByCode(userLogoutRequest.getRefreshToken());
        }

        authUtil.logoutUser();
    }
}
