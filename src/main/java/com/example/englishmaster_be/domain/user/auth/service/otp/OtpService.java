package com.example.englishmaster_be.domain.user.auth.service.otp;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.OtpStatus;
import com.example.englishmaster_be.domain.user.auth.util.OtpUtil;
import com.example.englishmaster_be.domain.user.auth.model.OtpEntity;
import com.example.englishmaster_be.domain.user.auth.repository.OtpRepository;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
public class OtpService implements IOtpService {

    private final OtpRepository otpRepository;

    @Lazy
    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public OtpEntity getByOtp(String otp) {

        return otpRepository.findByOtp(otp).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid.")
        );
    }

    @Override
    public OtpEntity getByEmailAndOtp(String email, String otp) {

        return otpRepository.findByEmailAndOtp(email, otp).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid.")
        );
    }

    @Transactional
    @Override
    public OtpEntity generateOtp(UserEntity user) {

        otpRepository.deleteByEmail(user.getEmail());

        int codeLength = 6;

        String otpCode = OtpUtil.generateOtpCode(codeLength);

        OtpEntity otp = new OtpEntity();
        otp.setOtp(otpCode);
        otp.setEmail(user.getEmail());
        otp.setUser(user);
        otp.setStatus(OtpStatus.UN_VERIFIED);
        otp.setExpirationTime(LocalDateTime.now().plusMinutes(2));

        return otpRepository.save(otp);
    }

    @Override
    public boolean isExpiredOtp(OtpEntity otpEntity) {

        if(otpEntity == null)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid.");

        return !LocalDateTime.now().isBefore(otpEntity.getExpirationTime());
    }

    @Transactional
    @Override
    public void updateOtpStatus(String email, String otp, OtpStatus status) {

        if(email == null || email.isEmpty())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email required.");

        if(otp == null || otp.isEmpty())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP required.");

        otpRepository.updateStatus(email, otp, status.name());
    }

    @Transactional
    @Override
    public void deleteOtp(String otp) {

        OtpEntity otpEntity = getByOtp(otp);

        otpRepository.delete(otpEntity);
    }

    @Override
    public boolean isVerifiedOtpAndEmail(String otp, String email) {

        return otpRepository.isVerified(otp, email);
    }

    @Override
    public OtpEntity getOtpAndUserByOtpCode(String otp) {

        if(otp == null || otp.isEmpty())
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "OTP required.");

        return otpRepository.findOtpJoinUserByOtpCode(otp)
                .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "OTP invalid."));
    }
}
