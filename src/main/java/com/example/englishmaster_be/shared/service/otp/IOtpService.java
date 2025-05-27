package com.example.englishmaster_be.shared.service.otp;


import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.user.UserEntity;

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
