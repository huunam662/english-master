package com.example.englishmaster_be.shared.service.otp;


public interface IOtpService {

    String generateOtp(String email);

    boolean validateOtp(String otp);

    void updateOtpStatusToVerified(String otp);

    void deleteOtp(String otp);
}
