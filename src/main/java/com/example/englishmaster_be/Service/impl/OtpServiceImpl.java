package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Response.Otp;
import com.example.englishmaster_be.Service.IOtpService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class OtpServiceImpl implements IOtpService {

    private final RedisTemplate<String,Otp> redisTemplate;

    @Override
    public String generateOtp(String email) {

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otpObj = new Otp();
        otpObj.setOtp(otpCode);
        otpObj.setEmail(email);
        otpObj.setStatus("Unverified");

        // Lưu OTP với TTL là 5 phút (300 giây)
        redisTemplate.opsForValue().set(otpCode,otpObj,5,TimeUnit.MINUTES);

        return otpCode;
    }


    @Override
    public String getEmailByOtp(String otp) {
        Otp otpObj = (Otp) redisTemplate.opsForValue().get(otp);
        return otpObj != null ? otpObj.getEmail() : null;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        Otp otpObj = (Otp) redisTemplate.opsForValue().get(otp);

        if (otpObj != null && otpObj.getEmail().equals(email)){
            // Cập nhật trạng thái OTP thành "Verified"
            otpObj.setStatus("Verified");
            redisTemplate.opsForValue().set(otp, otpObj, 5, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }


    @Override
    public Otp getOtpObject(String otp) {
        return redisTemplate.opsForValue().get(otp);
    }
}
