package com.example.englishmaster_be.Service;

import org.springframework.stereotype.Service;

@Service
public interface IOtpService {
    String generateOtp(String email);
    boolean validateOtp(String otp);
    void updateOtpStatusToVerified(String otp);
    void deleteOtp(String otp);
}
