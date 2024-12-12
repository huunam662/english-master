package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IRefreshTokenService;
import com.example.englishmaster_be.Service.IUserService;
import com.example.englishmaster_be.entity.ConfirmationTokenEntity;
import com.example.englishmaster_be.entity.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Value("${masterE.jwtRefreshExpirationMs}")
    static long refreshTokenDurationMs;

    ConfirmationTokenRepository confirmationTokenRepository;

    IUserService userService;

    @Override
    public ConfirmationTokenEntity findByToken(String token) {
        return confirmationTokenRepository.findByCodeAndType(token, "REFRESH_TOKEN");
    }

    @Override
    public void deleteRefreshToken(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findByCodeAndType(token, "REFRESH_TOKEN");
        if(confirmationToken != null){
            confirmationTokenRepository.delete(confirmationToken);
        }
    }

    @Override
    public ConfirmationTokenEntity createRefreshToken(String email) {

        UserEntity user = userService.currentUser();

        ConfirmationTokenEntity confirmationToken = ConfirmationTokenEntity.builder()
                .createAt(LocalDateTime.now())
                .user(user)
                .code(UUID.randomUUID().toString())
                .type("REFRESH_TOKEN")
                .build();

        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void verifyExpiration(ConfirmationTokenEntity token) {

        if(token.getCreateAt().plusSeconds(refreshTokenDurationMs/1000).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Refresh token was expired. Please make a new sign in request");

    }

    @Override
    public void deleteAllTokenExpired(UserEntity user) {
        try{
            List<ConfirmationTokenEntity> confirmationTokenList = confirmationTokenRepository.findAllByUserAndType(user, "REFRESH_TOKEN");

            for(ConfirmationTokenEntity confirmationToken : confirmationTokenList){
                if(confirmationToken.getCreateAt().plusSeconds(refreshTokenDurationMs/1000).isBefore(LocalDateTime.now())){

                    confirmationTokenRepository.delete(confirmationToken);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Don't delete token!");
        }

    }

}
