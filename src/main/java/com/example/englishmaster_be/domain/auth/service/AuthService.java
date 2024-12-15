package com.example.englishmaster_be.domain.auth.service;

import com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.domain.auth.dto.request.*;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenRepository;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.role.RoleRepository;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.refreshToken.IRefreshTokenService;
import com.example.englishmaster_be.util.JwtUtil;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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

    IRefreshTokenService refreshTokenService;

    IInvalidTokenService invalidTokenService;

    IOtpService otpService;

    OtpRepository otpRepository;

    UserRepository userRepository;

    ConfirmationTokenRepository confirmationTokenRepository;

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

        String jwt = jwtUtil.generateToken(userDetails);

        UserEntity user = userService.findUser(userDetails);

        refreshTokenService.deleteAllTokenExpired(user);
        confirmationTokenRepository.deleteByUserAndType(user, ConfirmRegisterTypeEnum.ACTIVE);

        ConfirmationTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return UserAuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getCode())
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

        if(user != null && user.getConfirmToken() != null)
            userRegister.setConfirmToken(new ArrayList<>());

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

        ConfirmationTokenEntity confirmationToken = ConfirmationTokenMapper.INSTANCE.toConfirmationTokenEntity(confirmationTokenRequest);

        confirmationToken.setType(ConfirmRegisterTypeEnum.ACTIVE);
        confirmationToken.setUser(confirmationTokenRequest.getUser());
        confirmationToken.setCode(UUID.randomUUID().toString());
        confirmationToken.setCreateAt(LocalDateTime.now());
        confirmationToken = confirmationTokenRepository.save(confirmationToken);

        return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(confirmationToken);
    }


    @Transactional
    @Override
    public void confirmRegister(String confirmationToken) {

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, ConfirmRegisterTypeEnum.ACTIVE);

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
            throw new BadRequestException("Please fill your email");

        boolean emailExisting = userService.existsEmail(email);

        if(!emailExisting)
            throw new BadRequestException("Your email isn't found");

        String otp = otpService.generateOtp(email);

        mailerUtil.sendOtpToEmail(email, otp);

    }


    @Transactional
    @Override
    public void verifyOtp(String otp) {

        if (otp == null || otp.isEmpty())
            throw new BadRequestException("Please fill your otp");

        boolean isOtpValid = otpService.validateOtp(otp);

        if (!isOtpValid)
            throw new BadRequestException("The OTP code is expired");

        otpService.updateOtpStatusToVerified(otp);

    }


    @Transactional
    @Override
    public void changePass(UserChangePasswordRequest changePasswordRequest) {

        UserEntity user = userService.currentUser();

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcherOldPassword = pattern.matcher(changePasswordRequest.getOldPassword());
        Matcher matcherNewPassword = pattern.matcher(changePasswordRequest.getNewPassword());
        Matcher matcherConfirmPassword = pattern.matcher(changePasswordRequest.getConfirmNewPassword());

        if(!matcherOldPassword.matches())
            throw new BadRequestException("Old password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherNewPassword.matches())
            throw new BadRequestException("New password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherConfirmPassword.matches())
            throw new BadRequestException("The confirm password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!changePasswordRequest.getConfirmNewPassword().equals(changePasswordRequest.getNewPassword()))
            throw new BadRequestException("Your new password doesn't match with your confirm password");

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Your old password doesn't correct");

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword()))
            throw new BadRequestException("New password can't be the same as old password");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        userRepository.save(user);

    }




    @Transactional
    @Override
    public void changePassword(UserChangePasswordRequest changePasswordRequest) {

        String otp = changePasswordRequest.getCode();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmNewPassword();

        // Regex để kiểm tra mật khẩu
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

        if (!otpService.validateOtp(otp))
            throw new BadRequestException("The OTP code is expired");

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty())
            throw new BadRequestException("Please fill your password and your confirm password");

        // Kiểm tra mật khẩu mới có đúng định dạng theo regex hay không
        if (!newPassword.matches(regexPassword))
            throw new BadRequestException("The new password must contain at least 1 digit, " +
                    "1 lowercase letter, 1 uppercase letter, 1 special character, " +
                    "and must not contain any spaces, with a length of 8 to 20 characters.");

        if (!newPassword.equals(confirmPassword))
            throw new BadRequestException("The new password doesn't match confirm password");

        boolean isPasswordUpdated = updatePassword(otp, newPassword);

        if (!isPasswordUpdated)
            throw new BadRequestException("Cannot update your password, please try again");

        otpService.deleteOtp(otp);
    }


    @Override
    public String confirmForgetPassword(String token) {

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(token, ConfirmRegisterTypeEnum.RESET_PASSWORD);

        if (confirmToken == null)
            throw new BadRequestException("Invalid reset code");

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Reset code has expired");

        return token;
    }


    @Override
    public UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenDTO) {

        String refresh = refreshTokenDTO.getRequestRefresh();

        ConfirmationTokenEntity token = refreshTokenService.findByToken(refresh);

        if (token == null)
            throw new BadRequestException("Refresh token isn't existed");

        refreshTokenService.verifyExpiration(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());

        String newToken = jwtUtil.generateToken(userDetails);

        return UserAuthResponse.builder()
                .accessToken(newToken)
                .build();
    }



    @Override
    public void logoutOf(UserLogoutRequest userLogoutRequest) {

        boolean logoutSuccessfully = logoutUser();

        if(!logoutSuccessfully)
            throw new AuthenticationServiceException("You aren't logged in");

        invalidTokenService.insertInvalidToken(userLogoutRequest.getAccessToken());

        String refreshToken = userLogoutRequest.getRefreshToken();

        refreshTokenService.deleteRefreshToken(refreshToken);

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


    @Override
    public boolean updatePassword(String otp, String newPassword) {

        OtpEntity otpRecord = otpRepository.findById(otp).orElse(null);

        if (otpRecord == null || !"Verified".equals(otpRecord.getStatus()))
            return false;

        UserEntity user = userRepository.findByEmail(otpRecord.getEmail()).orElse(null);

        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus("Used");
        otpRepository.save(otpRecord);

        return true;
    }


}
