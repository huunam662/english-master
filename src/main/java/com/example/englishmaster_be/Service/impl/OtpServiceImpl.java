package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Otp;
import com.example.englishmaster_be.Repository.OtpRepository;
import com.example.englishmaster_be.Service.IOtpService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpServiceImpl implements IOtpService {

    OtpRepository otpRepository;

    @Transactional
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

        if (otpObj.isEmpty()) return false;

        Otp foundOtp = otpObj.get();

        return foundOtp.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Transactional
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

        Otp otpEntity = otpRepository.findById(otp).orElseThrow(
                () -> new RuntimeException("Otp not found")
        );

        otpRepository.delete(otpEntity);
    }

}
