package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.service.IAuthService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.AuthMapper;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.invalid_token.service.IInvalidTokenService;
import com.example.englishmaster_be.shared.otp.service.IOtpService;
import com.example.englishmaster_be.shared.session_active.service.ISessionActiveService;
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
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class AuthUtil {

    JwtUtil jwtUtil;

    PasswordEncoder passwordEncoder;

    IAuthService authService;

    IUserService userService;

    IOtpService otpService;

    ISessionActiveService sessionActiveService;

    IInvalidTokenService invalidTokenService;

    SessionActiveRepository sessionActiveRepository;

    UserRepository userRepository;

    OtpRepository otpRepository;


    @Transactional
    public UserAuthResponse saveNewPassword(UserEntity user, String newPassword, String otpCode) {

        if (passwordEncoder.matches(newPassword, user.getPassword()))
            throw new BadRequestException("Mật khẩu mới không được khớp với mật khẩu cũ");

        this.updatePassword(otpCode, user.getEmail(), newPassword);

        List<SessionActiveEntity> sessionActiveEntityList = sessionActiveService.getSessionActiveList(user.getUserId(), SessionActiveTypeEnum.REFRESH_TOKEN);

        invalidTokenService.insertInvalidTokenList(sessionActiveEntityList, InvalidTokenTypeEnum.PASSWORD_CHANGE);

        sessionActiveRepository.deleteAll(sessionActiveEntityList);

        String jwtToken = jwtUtil.generateToken(user);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        logoutUser();

        return AuthMapper.INSTANCE.toUserAuthResponse(sessionActive, jwtToken);
    }


    @Transactional
    public void updatePassword(String otp, String email, String newPassword) {

        OtpEntity otpRecord = otpService.getByEmailAndOtp(email, otp);

        if (!OtpStatusEnum.VERIFIED.equals(otpRecord.getStatus()))
            throw new BadRequestException("Vui lòng xác thực mã OTP");

        UserEntity user = userService.getUserByEmail(otpRecord.getEmail());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus(OtpStatusEnum.USED);
        otpRepository.save(otpRecord);
    }


    public void logoutUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails)
            SecurityContextHolder.getContext().setAuthentication(null);

    }
}
