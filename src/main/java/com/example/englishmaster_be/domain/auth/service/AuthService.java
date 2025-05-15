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

    UserRepository userRepository;

    SessionActiveRepository sessionActiveRepository;

    RoleRepository roleRepository;


    @Transactional
    @Override
    public UserAuthResponse login(UserLoginRequest userLoginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled())
            throw new ErrorHolder(Error.ACCOUNT_DISABLED);

        UserEntity user = userService.getUserByEmail(userDetails.getUsername());

        String jwtToken = jwtUtil.generateToken(user);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActive, jwtToken);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        UserEntity user = userRepository.findByEmail(userRegisterRequest.getEmail()).orElse(null);

        if(user != null && user.getEnabled())
            throw new ErrorHolder(Error.BAD_REQUEST, "Email is used.");

        UserEntity userRegister = UserMapper.INSTANCE.toUserEntity(userRegisterRequest);
        userRegister.setUserId(user != null ? user.getUserId() : UUID.randomUUID());
        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setRole(roleRepository.findByRoleName(Role.USER));
        userRegister.setEnabled(Boolean.FALSE);

        if(user != null && user.getConfirmTokens() != null)
            sessionActiveRepository.deleteByUserAndType(user, SessionActiveType.CONFIRM);

        userRegister = userRepository.save(userRegister);

        UserConfirmTokenResponse confirmationTokenResponse = this.createConfirmationToken(userRegister);

        try {
            mailerUtil.sendConfirmationEmail(userRegister.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new ErrorHolder(Error.SEND_EMAIL_FAILURE);
        }
    }


    @Transactional
    @Override
    public UserConfirmTokenResponse createConfirmationToken(UserEntity user) {

        SessionActiveEntity sessionActive = SessionActiveEntity.builder()
                .type(SessionActiveType.CONFIRM)
                .user(user)
                .code(UUID.randomUUID())
                .createAt(LocalDateTime.now())
                .build();

        sessionActive = sessionActiveRepository.save(sessionActive);

        return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(sessionActive);
    }


    @Transactional
    @Override
    public void confirmRegister(UUID sessionActiveCode) {

        SessionActiveEntity confirmToken = sessionActiveRepository.findByCodeAndType(sessionActiveCode, SessionActiveType.CONFIRM);

        if (confirmToken == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND);

        if (confirmToken.getUser().getEnabled())
            throw new ErrorHolder(Error.BAD_REQUEST, "Account had been verified.");

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Verification session had been expired, try again.");

        UserEntity user = confirmToken.getUser();

        user.setEnabled(Boolean.TRUE);

        userRepository.save(user);

        sessionActiveRepository.deleteByUserAndType(user, SessionActiveType.CONFIRM);
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
    public UserAuthResponse changePasswordForgot(UserChangePwForgotRequest changePasswordRequest) {

        OtpEntity otpEntity = otpService.getByOtp(changePasswordRequest.getOtpCode());

        return authUtil.saveNewPassword(otpEntity.getUser(), changePasswordRequest.getNewPassword(), changePasswordRequest.getOtpCode());
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

        UUID refresh = refreshTokenDTO.getRequestRefresh();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(refresh);

        if (sessionActive == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid refresh.");

        sessionActiveService.verifyExpiration(sessionActive);

        String newToken = jwtUtil.generateToken(sessionActive.getUser());

        SessionActiveEntity sessionActiveNew = sessionActiveService.saveSessionActive(sessionActive.getUser(), newToken);

        invalidTokenService.insertInvalidToken(sessionActive, InvalidTokenType.REPLACED);

        sessionActiveRepository.delete(sessionActive);

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActiveNew, newToken);
    }


    @Override
    public void logoutOf(UserLogoutRequest userLogoutRequest) {

        authUtil.logoutUser();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(userLogoutRequest.getRefreshToken());

        if(sessionActive != null){

            invalidTokenService.insertInvalidToken(sessionActive, InvalidTokenType.LOGOUT);

            sessionActiveRepository.delete(sessionActive);
        }
    }
}
