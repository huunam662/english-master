package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.domain.auth.dto.request.UserChangePasswordRequest;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.service.IAuthService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.AuthMapper;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class AuthUtil {

    JwtUtil jwtUtil;

    PasswordEncoder passwordEncoder;

    IAuthService authService;

    IOtpService otpService;

    ISessionActiveService sessionActiveService;

    IInvalidTokenService invalidTokenService;

    SessionActiveRepository sessionActiveRepository;


    public UserAuthResponse saveNewPassword(UserEntity user, String newPassword, String otpCode) {

        if (passwordEncoder.matches(newPassword, user.getPassword()))
            throw new BadRequestException("Mật khẩu mới không được khớp với mật khẩu cũ");

        authService.updatePassword(otpCode, user.getEmail(), newPassword);

        List<SessionActiveEntity> sessionActiveEntityList = sessionActiveService.getSessionActiveList(user.getUserId(), SessionActiveTypeEnum.REFRESH_TOKEN);

        invalidTokenService.insertInvalidTokenList(sessionActiveEntityList, InvalidTokenTypeEnum.CHANGE_PASSWORD);

        sessionActiveRepository.deleteAll(sessionActiveEntityList);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtil.generateToken(userDetails);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(userDetails, jwtToken);

        return AuthMapper.INSTANCE.toUserAuthResponse(sessionActive, jwtToken);
    }


    public void logoutUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return;
        }

        throw new BadRequestException("Bạn chưa đăng nhập");
    }
}
