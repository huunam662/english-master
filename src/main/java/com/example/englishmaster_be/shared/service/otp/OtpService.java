package com.example.englishmaster_be.shared.service.otp;

import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.helper.OtpHelper;
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


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService implements IOtpService {

    OtpRepository otpRepository;


    @Override
    public OtpEntity getOtp(String otp) {

        return otpRepository.findByOtp(otp).orElseThrow(
                () -> new BadRequestException("Mã OTP không hợp lệ")
        );
    }

    @Transactional
    @Override
    public OtpEntity generateOtp(String email) {

        int codeLength = 6;

        String otpCode = OtpHelper.generateOtpCode(codeLength);

        OtpEntity otp = OtpEntity.builder()
                .otp(otpCode)
                .email(email)
                .status(OtpStatusEnum.UnVerified)
                .expirationTime(LocalDateTime.now().plusMinutes(2))
                .createdAt(LocalDateTime.now())
                .build();

        return otpRepository.save(otp);
    }

    @Override
    public boolean isValidateOtp(String otp) {

        OtpEntity otpEntity = getOtp(otp);

        return otpEntity.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void updateOtpStatusToVerified(String otp) {

        OtpEntity otpEntity = getOtp(otp);

        otpEntity.setStatus(OtpStatusEnum.Verified);

        otpRepository.save(otpEntity);
    }
    @Override
    public void deleteOtp(String otp) {

        OtpEntity otpEntity = otpRepository.findByOtp(otp).orElseThrow(
                () -> new RuntimeException("Không tìm thấy mã OTP")
        );

        otpRepository.delete(otpEntity);
    }

}
