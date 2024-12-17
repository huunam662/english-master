package com.example.englishmaster_be.shared.service.otp;


import com.example.englishmaster_be.model.otp.OtpEntity;

public interface IOtpService {

    OtpEntity getOtp(String otp);

    OtpEntity generateOtp(String email);

    boolean isValidateOtp(String otp);

    void updateOtpStatusToVerified(String otp);

    void deleteOtp(String otp);
}
