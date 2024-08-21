package com.example.englishmaster_be.Service;
import com.example.englishmaster_be.Model.Response.Otp;
import org.springframework.stereotype.Service;

@Service
public interface IOtpService {
    String generateOtp(String email);
    String getEmailByOtp(String otp);
    public boolean validateOtp(String email,String otp);
    Otp getOtpObject(String otp);
}
