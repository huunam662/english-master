package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class AuthHelper {

    JwtService jwtUtil;

    PasswordEncoder passwordEncoder;

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
            throw new ErrorHolder(Error.BAD_REQUEST, "New password mustn't match with old password.");

        this.updatePassword(otpCode, user.getEmail(), newPassword);

        List<SessionActiveEntity> sessionActiveEntityList = sessionActiveService.getSessionActiveList(user.getUserId(), SessionActiveType.REFRESH_TOKEN);

        invalidTokenService.saveInvalidTokenList(sessionActiveEntityList, InvalidTokenType.PASSWORD_CHANGE);

        sessionActiveRepository.deleteAll(sessionActiveEntityList);

        String jwtToken = jwtUtil.generateToken(user);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        logoutUser();

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActive.getCode(), jwtToken, null);
    }


    @Transactional
    public void updatePassword(String otp, String email, String newPassword) {

        OtpEntity otpRecord = otpService.getByEmailAndOtp(email, otp);

        if (!OtpStatus.VERIFIED.equals(otpRecord.getStatus()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Please verification OTP code.");

        UserEntity user = userService.getUserByEmail(otpRecord.getEmail());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus(OtpStatus.USED);
        otpRepository.save(otpRecord);
    }


    public void logoutUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails)
            SecurityContextHolder.getContext().setAuthentication(null);

    }
}
