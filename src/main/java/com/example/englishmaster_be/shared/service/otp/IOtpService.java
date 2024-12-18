package com.example.englishmaster_be.shared.service.otp;


import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.model.otp.OtpEntity;

public interface IOtpService {

    OtpEntity getOtp(String otp);

    OtpEntity getByEmailAndOtp(String email, String otp);

    OtpEntity generateOtp(String email);

    boolean isValidOtp(String email, String otp);

    void updateOtpStatus(String email, String otp, OtpStatusEnum status);

    void deleteOtp(String otp);
}
