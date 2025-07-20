package com.example.englishmaster_be.domain.user.auth.service.otp;


import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.domain.user.auth.model.OtpEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;

public interface IOtpService {

    OtpEntity getByOtp(String otp);

    OtpEntity getByEmailAndOtp(String email, String otp);

    OtpEntity generateOtp(UserEntity user);

    boolean isExpiredOtp(OtpEntity otpEntity);

    void updateOtpStatus(String email, String otp, OtpStatus status);

    void deleteOtp(String otp);

    boolean isVerifiedOtpAndEmail(String otp, String email);

    OtpEntity getOtpAndUserByOtpCode(String otp);
}
