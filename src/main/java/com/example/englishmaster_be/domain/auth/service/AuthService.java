package com.example.englishmaster_be.domain.auth.service;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
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
import com.example.englishmaster_be.advice.exception.template.BadRequestException;
import com.example.englishmaster_be.advice.exception.template.CustomException;
import com.example.englishmaster_be.converter.ConfirmationTokenConverter;
import com.example.englishmaster_be.converter.UserConverter;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.shared.service.mailer.MailerService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
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
            throw new CustomException(ErrorEnum.ACCOUNT_DISABLED);

        UserEntity user = userService.getUserByEmail(userDetails.getUsername());

        String jwtToken = jwtUtil.generateToken(user);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        return UserConverter.INSTANCE.toUserAuthResponse(sessionActive, jwtToken);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        UserEntity user = userRepository.findByEmail(userRegisterRequest.getEmail()).orElse(null);

        if(user != null && user.getEnabled())
            throw new BadRequestException("Email đã được sử dụng");

        UserEntity userRegister = UserConverter.INSTANCE.toUserEntity(userRegisterRequest);
        userRegister.setUserId(user != null ? user.getUserId() : UUID.randomUUID());
        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setRole(roleRepository.findByRoleName(RoleEnum.USER));
        userRegister.setEnabled(Boolean.FALSE);

        if(user != null && user.getConfirmTokens() != null)
            sessionActiveRepository.deleteByUserAndType(user, SessionActiveTypeEnum.CONFIRM);

        userRegister = userRepository.save(userRegister);

        UserConfirmTokenResponse confirmationTokenResponse = this.createConfirmationToken(userRegister);

        try {
            mailerUtil.sendConfirmationEmail(userRegister.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new CustomException(ErrorEnum.SEND_EMAIL_FAILURE);
        }
    }


    @Transactional
    @Override
    public UserConfirmTokenResponse createConfirmationToken(UserEntity user) {

        SessionActiveEntity sessionActive = SessionActiveEntity.builder()
                .type(SessionActiveTypeEnum.CONFIRM)
                .user(user)
                .code(UUID.randomUUID())
                .createAt(LocalDateTime.now())
                .build();

        sessionActive = sessionActiveRepository.save(sessionActive);

        return ConfirmationTokenConverter.INSTANCE.toConfirmationTokenResponse(sessionActive);
    }


    @Transactional
    @Override
    public void confirmRegister(UUID sessionActiveCode) {

        SessionActiveEntity confirmToken = sessionActiveRepository.findByCodeAndType(sessionActiveCode, SessionActiveTypeEnum.CONFIRM);

        if (confirmToken == null)
            throw new BadRequestException("Không tồn tại");

        if (confirmToken.getUser().getEnabled())
            throw new BadRequestException("Tài khoản đã được xác thực");

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Phiên xác thực đã hết hạn, vui lòng đăng ký lại");

        UserEntity user = confirmToken.getUser();

        user.setEnabled(Boolean.TRUE);

        userRepository.save(user);

        sessionActiveRepository.deleteByUserAndType(user, SessionActiveTypeEnum.CONFIRM);
    }


    @Transactional
    @SneakyThrows
    @Override
    public void forgotPassword(String email) {

        if (email == null || email.isEmpty())
            throw new BadRequestException("Email là bắt buộc");

        boolean emailExisting = userService.existsEmail(email);

        if(!emailExisting)
            throw new BadRequestException("Email bạn đã đăng ký không tồn tại");

        OtpEntity otpEntity = otpService.generateOtp(email);

        mailerUtil.sendOtpToEmail(email, otpEntity.getOtp());

    }


    @Transactional
    @Override
    public void verifyOtp(String otp) {

        if (otp == null || otp.isEmpty())
            throw new BadRequestException("Mã OTP là bắt buộc");

        OtpEntity otpEntity = otpService.getByOtp(otp);

        if (!otpService.isValidOtp(otpEntity))
            throw new BadRequestException("Mã OTP đã hết hiệu lực");

        otpService.updateOtpStatus(otpEntity.getEmail(), otp, OtpStatusEnum.VERIFIED);
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
            throw new BadRequestException("Mật khẩu cũ không hợp lệ");

        return authUtil.saveNewPassword(user, changePasswordRequest.getNewPassword(), changePasswordRequest.getOtpCode());
    }


    @Transactional
    @Override
    public UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenDTO) {

        UUID refresh = refreshTokenDTO.getRequestRefresh();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(refresh);

        if (sessionActive == null)
            throw new BadRequestException("Mã làm mới không hợp lệ");

        sessionActiveService.verifyExpiration(sessionActive);

        String newToken = jwtUtil.generateToken(sessionActive.getUser());

        SessionActiveEntity sessionActiveNew = sessionActiveService.saveSessionActive(sessionActive.getUser(), newToken);

        invalidTokenService.insertInvalidToken(sessionActive, InvalidTokenTypeEnum.REPLACED);

        sessionActiveRepository.delete(sessionActive);

        return UserConverter.INSTANCE.toUserAuthResponse(sessionActiveNew, newToken);
    }


    @Override
    public void logoutOf(UserLogoutRequest userLogoutRequest) {

        authUtil.logoutUser();

        SessionActiveEntity sessionActive = sessionActiveService.getByCode(userLogoutRequest.getRefreshToken());

        if(sessionActive != null){

            invalidTokenService.insertInvalidToken(sessionActive, InvalidTokenTypeEnum.LOGOUT);

            sessionActiveRepository.delete(sessionActive);
        }
    }
}
