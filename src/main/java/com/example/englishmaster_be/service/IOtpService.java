package com.example.englishmaster_be.service;


public interface IOtpService {

    String generateOtp(String email);

    boolean validateOtp(String otp);

    void updateOtpStatusToVerified(String otp);

    void deleteOtp(String otp);
}
