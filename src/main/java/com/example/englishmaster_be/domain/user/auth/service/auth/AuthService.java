package com.example.englishmaster_be.domain.user.auth.service.auth;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.domain.user.auth.dto.req.*;
import com.example.englishmaster_be.domain.user.user.repository.RoleRepository;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.user.auth.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.domain.user.auth.service.otp.IOtpService;
import com.example.englishmaster_be.domain.user.auth.service.session_active.ISessionActiveService;
import com.example.englishmaster_be.domain.user.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.auth.model.OtpEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.auth.service.mailer.MailerService;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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
import java.util.concurrent.CompletableFuture;


@Slf4j(topic = "AUTH-SERVICE")
@Service
public class AuthService implements IAuthService {


    private final JwtService jwtUtil;
    private final MailerService mailerUtil;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserService userService;
    private final ISessionActiveService sessionActiveService;
    private final IInvalidTokenService invalidTokenService;
    private final IOtpService otpService;

    @Lazy
    public AuthService(JwtService jwtUtil, MailerService mailerUtil, AuthenticationManager authenticationManager, RoleRepository roleRepository, PasswordEncoder passwordEncoder, IUserService userService, ISessionActiveService sessionActiveService, IInvalidTokenService invalidTokenService, IOtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.mailerUtil = mailerUtil;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.sessionActiveService = sessionActiveService;
        this.invalidTokenService = invalidTokenService;
        this.otpService = otpService;
    }

    @Transactional
    @Override
    public UserAuthRes login(UserLoginReq userLoginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled()) throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Account is disabled.");

        UserEntity user = (UserEntity) userDetails;

        String jwtToken = jwtUtil.generateToken(userDetails);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActive.getCode(), jwtToken, user);
    }


    @Transactional
    @Override
    public void registerUser(UserRegisterReq userRegisterRequest) {

        UserEntity user = userService.getUserByEmail(userRegisterRequest.getEmail(), false);

        if(user != null && user.getEnabled())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email is used.");

        UserEntity userRegister = UserMapper.INSTANCE.toUserEntity(userRegisterRequest);

        if(user != null) userRegister.setUserId(user.getUserId());

        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setEnabled(false);

        if(user != null && user.getSessionActives() != null)
            sessionActiveService.deleteByUserIdAndType(user.getUserId(), SessionActiveType.CONFIRM);

        userRegister.setRole(roleRepository.findByRoleName(Role.USER));
        userRegister.setUserType("MeU English");
        userRegister = userService.saveUser(userRegister);
        SessionActiveEntity sessionConfirm = sessionActiveService.saveForUserRegister(userRegister, SessionActiveType.CONFIRM);

        CompletableFuture.runAsync(() -> {
            try {
                mailerUtil.sendConfirmRegister(userRegisterRequest.getEmail(), sessionConfirm.getCode().toString());
            } catch (IOException | MessagingException e) {
                log.error("Failed to send confirmation email");
            }
        });
    }


    @Transactional
    @Override
    public void confirmRegister(UUID sessionActiveCode) {

        SessionActiveEntity sessionConfirm = sessionActiveService.getJoinUserByCodeAndType(sessionActiveCode, SessionActiveType.CONFIRM);

        if ((sessionConfirm.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Verification session had been expired, try again.");

        UserEntity user = sessionConfirm.getUser();

        userService.enabledUser(user.getUserId());

        sessionActiveService.deleteByUserIdAndType(user.getUserId(), SessionActiveType.CONFIRM);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void sendOtp(String email) {

        if (email == null || email.isEmpty())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email is required.");

        UserEntity user = userService.getUserByEmail(email);

        OtpEntity otpEntity = otpService.generateOtp(user);

        mailerUtil.sendOtpToEmail(email, otpEntity.getOtp());
    }


    @Transactional
    @Override
    public void verifyOtp(String otp) {

        OtpEntity otpEntity = otpService.getByOtp(otp);

        if(!otpEntity.getStatus().equals(OtpStatus.UN_VERIFIED))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP is used.");

        if (otpService.isExpiredOtp(otpEntity))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP is expired.");

        otpService.updateOtpStatus(otpEntity.getEmail(), otp, OtpStatus.VERIFIED);
    }


    @Transactional
    @Override
    public void changePasswordForgot(UserChangePwForgotReq changePasswordRequest) {

        OtpEntity otpEntity = otpService.getOtpAndUserByOtpCode(changePasswordRequest.getOtpCode());

        boolean isValidOtp = otpEntity.getStatus().equals(OtpStatus.VERIFIED);

        if(!isValidOtp) throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid.");

        otpService.updateOtpStatus(otpEntity.getEmail(), otpEntity.getOtp(), OtpStatus.USED);

        userService.updatePasswordForgot(otpEntity.getUser(), changePasswordRequest.getNewPassword());
    }

    @Transactional
    @Override
    public UserAuthRes changePassword(UserChangePasswordReq changePasswordRequest) {

        UserEntity user = userService.currentUser();

        OtpEntity otpEntity = otpService.getOtpAndUserByOtpCode(changePasswordRequest.getOtpCode());

        boolean isValidOtp = otpEntity.getStatus().equals(OtpStatus.VERIFIED);

        if(!isValidOtp) throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid.");

        otpService.updateOtpStatus(user.getEmail(), changePasswordRequest.getOtpCode(), OtpStatus.USED);

        return userService.updatePassword(
                user,
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword()
        );
    }


    @Transactional
    @Override
    public UserAuthRes refreshToken(UserRefreshTokenReq refreshTokenDTO) {

        UUID refreshToken = refreshTokenDTO.getRequestRefresh();

        SessionActiveEntity sessionActive = sessionActiveService.getJoinUserRoleByCodeAndType(refreshToken, SessionActiveType.REFRESH_TOKEN);

        if(sessionActiveService.isExpirationToken(sessionActive))
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Refresh session was expired.");

        String newToken = jwtUtil.generateToken(sessionActive.getUser());

        sessionActiveService.saveSessionActive(sessionActive.getUser().getUserId(), newToken);

        invalidTokenService.saveInvalidToken(
                refreshTokenDTO.getRequestToken(),
                sessionActive.getUser().getUserId(),
                InvalidTokenType.EXPIRED
        );

        return UserMapper.INSTANCE.toUserAuthResponse(refreshTokenDTO.getRequestRefresh(), newToken, sessionActive.getUser());
    }


    @Override
    public void logoutOf(UserLogoutReq userLogoutRequest) {

        UserEntity user = userService.currentUser();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(userLogoutRequest.getRefreshToken());

        if(sessionActive != null){

            invalidTokenService.saveInvalidToken(userLogoutRequest.getAccessToken(), user.getUserId(), InvalidTokenType.LOGOUT);

            sessionActiveService.deleteByToken(userLogoutRequest.getAccessToken());

            sessionActiveService.deleteByCode(userLogoutRequest.getRefreshToken());
        }

    }
}
