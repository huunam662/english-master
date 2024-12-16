package com.example.englishmaster_be.shared.service.otp;

import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.otp.OtpRepository;
import lombok.AccessLevel;
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
public class OtpService implements IOtpService {

    OtpRepository otpRepository;

    @Transactional
    @Override
    public String generateOtp(String email) {

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        OtpEntity otpObj = new OtpEntity();

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

        Optional<OtpEntity> otpObj = otpRepository.findById(otp);

        if (otpObj.isEmpty()) return false;

        OtpEntity foundOtp = otpObj.get();

        return foundOtp.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void updateOtpStatusToVerified(String otp) {
        Optional<OtpEntity> otpObj = otpRepository.findById(otp);
        if (otpObj.isPresent()) {
            OtpEntity foundOtp = otpObj.get();
            foundOtp.setStatus("Verified");
            otpRepository.save(foundOtp);
        }
    }
    @Override
    public void deleteOtp(String otp) {

        OtpEntity otpEntity = otpRepository.findById(otp).orElseThrow(
                () -> new RuntimeException("OtpEntity not found")
        );

        otpRepository.delete(otpEntity);
    }

}
