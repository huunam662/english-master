package com.example.englishmaster_be.shared.otp.service;


import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.model.otp.OtpEntity;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;

public interface IOtpService {

    OtpEntity getByOtp(String otp);

    OtpEntity getByEmailAndOtp(String email, String otp);

    OtpEntity generateOtp(String email);

    boolean isValidOtp(OtpEntity otpEntity);

    void updateOtpStatus(String email, String otp, OtpStatusEnum status);

    void deleteOtp(String otp);
}
