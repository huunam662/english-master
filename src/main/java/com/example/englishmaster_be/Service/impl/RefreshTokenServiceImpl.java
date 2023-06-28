package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.RefreshTokenException;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.ConfirmationTokenRepository;
import com.example.englishmaster_be.Repository.UserRepository;
import com.example.englishmaster_be.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${masterE.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenRepository.findByCodeAndType(token, "REFRESH_TOKEN");
    }

    @Override
    public ConfirmationToken createRefreshToken(String email) {
        ConfirmationToken confirmationToken  = new ConfirmationToken(userRepository.findByEmail(email));

        confirmationToken.setCode(UUID.randomUUID().toString());
        confirmationToken.setType("REFRESH_TOKEN");

        confirmationToken = confirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public ResponseModel verifyExpiration(ResponseModel responseModel, ConfirmationToken token) {
        System.out.println(token.getCreateAt().plusNanos(refreshTokenDurationMs));
        if(token.getCreateAt().plusNanos(refreshTokenDurationMs).isBefore(LocalDateTime.now())){
            confirmationTokenRepository.delete(token);
            responseModel.setStatus("fail");
            responseModel.setMessage("Refresh token was expired. Please make a new signin request");
        }
        return responseModel;
    }

    @Override
    public int deleteByUserId(UUID userId) {
        return 0;
    }
}
