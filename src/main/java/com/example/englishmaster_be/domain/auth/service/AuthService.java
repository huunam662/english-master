package com.example.englishmaster_be.domain.auth.service;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.role.RoleRepository;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
import com.example.englishmaster_be.util.JwtUtil;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.MailerUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {


    JwtUtil jwtUtil;

    MailerUtil mailerUtil;

    AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder;

    UserDetailsService userDetailsService;

    IUserService userService;

    ISessionActiveService sessionActiveService;

    IInvalidTokenService invalidTokenService;

    IOtpService otpService;

    OtpRepository otpRepository;

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

        sessionActiveService.deleteAllTokenExpired(user);
        sessionActiveRepository.deleteByUserAndType(user, SessionActiveTypeEnum.CONFIRM);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(userDetails);

        return UserAuthResponse.builder()
                .accessToken(sessionActive.getToken())
                .refreshToken(sessionActive.getCode())
                .build();
    }


    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        UserEntity user = userRepository.findByEmail(userRegisterRequest.getEmail()).orElse(null);

        if(user != null && user.getEnabled())
            throw new BadRequestException("Email đã được sử dụng");

        UserEntity userRegister = UserMapper.INSTANCE.toUserEntity(userRegisterRequest);
        userRegister.setUserId(user != null ? user.getUserId() : UUID.randomUUID());
        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setRole(roleRepository.findByRoleName(RoleEnum.USER));
        userRegister.setEnabled(Boolean.FALSE);

        if(user != null && user.getConfirmTokens() != null)
            userRegister.setConfirmTokens(new ArrayList<>());

        userRegister = userRepository.save(userRegister);

        UserConfirmTokenResponse confirmationTokenResponse = this.createConfirmationToken(
                UserConfirmTokenRequest.builder().user(userRegister).build()
        );

        try {
            mailerUtil.sendConfirmationEmail(userRegister.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new CustomException(ErrorEnum.SEND_EMAIL_FAILURE);
        }
    }


    @Transactional
    @Override
    public UserConfirmTokenResponse createConfirmationToken(UserConfirmTokenRequest confirmationTokenRequest) {

        SessionActiveEntity sessionActive = ConfirmationTokenMapper.INSTANCE.toConfirmationTokenEntity(confirmationTokenRequest);

        sessionActive.setType(SessionActiveTypeEnum.CONFIRM);
        sessionActive.setUser(confirmationTokenRequest.getUser());
        sessionActive.setCode(UUID.randomUUID());
        sessionActive.setCreateAt(LocalDateTime.now());
        sessionActive = sessionActiveRepository.save(sessionActive);

        return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(sessionActive);
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

        otpRepository.deleteByEmailAndStatus(email, OtpStatusEnum.UnVerified);

        OtpEntity otpEntity = otpService.generateOtp(email);

        mailerUtil.sendOtpToEmail(email, otpEntity.getOtp());

    }


    @Transactional
    @Override
    public void verifyOtp(String otp) {

        if (otp == null || otp.isEmpty())
            throw new BadRequestException("Mã OTP là bắt buộc");

        if (otpService.isValidateOtp(otp))
            throw new BadRequestException("Mã OTP đã hết hiệu lực");

        otpService.updateOtpStatusToVerified(otp);

    }


    @Transactional
    @Override
    public UserAuthResponse changePassword(UserChangePasswordRequest changePasswordRequest) {

        UserEntity user = userService.currentUser();

        if (!changePasswordRequest.getConfirmNewPassword().equals(changePasswordRequest.getNewPassword()))
            throw new BadRequestException("Mật khẩu không khớp với mật khẩu xác nhận");

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Mật khẩu cũ không hợp lệ");

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword()))
            throw new BadRequestException("Mật khẩu mới không được khớp với mật khẩu cũ");

        updatePassword(changePasswordRequest.getOtpCode(), changePasswordRequest.getNewPassword());

        List<SessionActiveEntity> sessionActiveEntityList = sessionActiveService.getSessionActiveList(user.getUserId(), SessionActiveTypeEnum.REFRESH_TOKEN);

        invalidTokenService.insertInvalidTokenList(sessionActiveEntityList, InvalidTokenTypeEnum.CHANGE_PASSWORD);

        sessionActiveRepository.deleteAll(sessionActiveEntityList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive((UserDetails) authentication.getPrincipal());

        return UserAuthResponse.builder()
                .accessToken(sessionActive.getToken())
                .refreshToken(sessionActive.getCode())
                .build();
    }


    @Override
    public UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenDTO) {

        UUID refresh = refreshTokenDTO.getRequestRefresh();

        SessionActiveEntity token = sessionActiveService.getByCode(refresh);

        if (token == null)
            throw new BadRequestException("Refresh token isn't existed");

        sessionActiveService.verifyExpiration(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());

        String newToken = jwtUtil.generateToken(userDetails);

        return UserAuthResponse.builder()
                .accessToken(newToken)
                .refreshToken(refresh)
                .build();
    }



    @Override
    public void logoutOf(UserLogoutRequest userLogoutRequest) {

        boolean logoutSuccessfully = logoutUser();

        if(!logoutSuccessfully)
            throw new AuthenticationServiceException("Bạn chưa đăng nhập");

        invalidTokenService.insertInvalidToken(userLogoutRequest.getRefreshToken(), InvalidTokenTypeEnum.LOGOUT);

        UUID refreshToken = userLogoutRequest.getRefreshToken();

        sessionActiveService.deleteSessionCode(refreshToken);

    }


    @Override
    public boolean logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return true;
        }

        return false;
    }

    @Transactional
    @Override
    public void updatePassword(String otp, String newPassword) {

        OtpEntity otpRecord = otpService.getOtp(otp);

        if (!OtpStatusEnum.Verified.equals(otpRecord.getStatus()))
            throw new BadRequestException("Vui lòng xác thực mã OTP");

        UserEntity user = userService.getUserByEmail(otpRecord.getEmail());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus(OtpStatusEnum.Verified);
        otpRepository.save(otpRecord);
    }


}
