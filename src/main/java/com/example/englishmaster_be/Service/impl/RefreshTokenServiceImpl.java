package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

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
        if(token.getCreateAt().plusNanos(refreshTokenDurationMs).isBefore(LocalDateTime.now())){
            confirmationTokenRepository.delete(token);
            responseModel.setStatus("fail");
            responseModel.setMessage("Refresh token was expired. Please make a new signin request");
        }
        return responseModel;
    }

    @Override
    public void deleteAllTokenExpired(User user) {
        try{
            List<ConfirmationToken> confirmationTokenList = confirmationTokenRepository.findAllByUserAndType(user, "REFRESH_TOKEN");

            for(ConfirmationToken confirmationToken : confirmationTokenList){
                if(confirmationToken.getCreateAt().plusSeconds(refreshTokenDurationMs/1000).isBefore(LocalDateTime.now())){

                    confirmationTokenRepository.delete(confirmationToken);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Don't delete token!");
        }

    }

}
