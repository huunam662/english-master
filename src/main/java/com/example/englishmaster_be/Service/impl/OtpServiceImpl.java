package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Otp;
import com.example.englishmaster_be.Repository.OtpRepository;
import com.example.englishmaster_be.Service.IOtpService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.Random;
@Service
@AllArgsConstructor
public class OtpServiceImpl implements IOtpService {

    private final OtpRepository otpRepository;

    @Override
    public String generateOtp(String email) {

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otpObj = new Otp();

        otpObj.setOtp(otpCode);
        otpObj.setEmail(email);
        otpObj.setStatus("Unverified");
        otpObj.setExpirationTime(LocalDateTime.now().plusMinutes(1));
        otpObj.setCreatedAt(LocalDateTime.now());
        otpRepository.save(otpObj);

        return otpCode;
    }

    @Override
    public boolean validateOtp(String otp) {

        Optional<Otp> otpObj = otpRepository.findById(otp);

        if (otpObj.isEmpty()) {
            return false;
        }

        Otp foundOtp = otpObj.get();

        boolean isExpired = foundOtp.getExpirationTime().isBefore(LocalDateTime.now());

        return !isExpired;
    }

    @Override
    public void updateOtpStatusToVerified(String otp) {
        Optional<Otp> otpObj = otpRepository.findById(otp);
        if (otpObj.isPresent()) {
            Otp foundOtp = otpObj.get();
            foundOtp.setStatus("Verified");
            otpRepository.save(foundOtp);
        }
    }
    @Override
    public void deleteOtp(String otp) {
        otpRepository.deleteById(otp);
    }

}
