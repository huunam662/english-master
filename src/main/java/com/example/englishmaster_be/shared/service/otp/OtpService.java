package com.example.englishmaster_be.shared.service.otp;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.util.OtpUtil;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService implements IOtpService {

    IUserService userService;

    OtpRepository otpRepository;


    @Override
    public OtpEntity getByOtp(String otp) {

        return otpRepository.findByOtp(otp).orElseThrow(
                () -> new ErrorHolder(Error.BAD_REQUEST, "OTP invalid.")
        );
    }

    @Override
    public OtpEntity getByEmailAndOtp(String email, String otp) {

        return otpRepository.findByEmailAndOtp(email, otp).orElseThrow(
                () -> new ErrorHolder(Error.BAD_REQUEST, "OTP invalid.")
        );
    }

    @Transactional
    @Override
    public OtpEntity generateOtp(String email) {

        otpRepository.deleteByEmail(email);

        UserEntity user = userService.getUserByEmail(email);

        int codeLength = 6;

        String otpCode = OtpUtil.generateOtpCode(codeLength);

        OtpEntity otp = OtpEntity.builder()
                .otp(otpCode)
                .email(email)
                .user(user)
                .status(OtpStatus.UN_VERIFIED)
                .expirationTime(LocalDateTime.now().plusMinutes(2))
                .createdAt(LocalDateTime.now())
                .build();

        return otpRepository.save(otp);
    }

    @Override
    public boolean isValidOtp(OtpEntity otpEntity) {

        if(otpEntity == null)
            throw new ErrorHolder(Error.BAD_REQUEST, "OTP invalid.");

        return LocalDateTime.now().isBefore(otpEntity.getExpirationTime());
    }

    @Transactional
    @Override
    public void updateOtpStatus(String email, String otp, OtpStatus status) {

        OtpEntity otpEntity = getByEmailAndOtp(email, otp);

        otpEntity.setStatus(status);

        otpRepository.save(otpEntity);
    }

    @Transactional
    @Override
    public void deleteOtp(String otp) {

        OtpEntity otpEntity = getByOtp(otp);

        otpRepository.delete(otpEntity);
    }

}
