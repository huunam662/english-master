package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.common.constaint.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.IRefreshTokenService;
import com.example.englishmaster_be.service.IUserService;
import com.example.englishmaster_be.entity.ConfirmationTokenEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.value.JwtValue;
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

    JwtValue jwtValue;

    ConfirmationTokenRepository confirmationTokenRepository;

    IUserService userService;

    @Override
    public ConfirmationTokenEntity findByToken(String token) {
        return confirmationTokenRepository.findByCodeAndType(token, ConfirmRegisterTypeEnum.REFRESH_TOKEN);
    }

    @Override
    public void deleteRefreshToken(String token) {
        ConfirmationTokenEntity confirmationToken = confirmationTokenRepository.findByCodeAndType(token, ConfirmRegisterTypeEnum.REFRESH_TOKEN);
        if(confirmationToken != null){
            confirmationTokenRepository.delete(confirmationToken);
        }
    }

    @Override
    public ConfirmationTokenEntity createRefreshToken(String email) {

        UserEntity user = userService.findUserByEmail(email);

        ConfirmationTokenEntity confirmationToken = ConfirmationTokenEntity.builder()
                .createAt(LocalDateTime.now())
                .user(user)
                .code(UUID.randomUUID().toString())
                .type(ConfirmRegisterTypeEnum.REFRESH_TOKEN)
                .build();

        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void verifyExpiration(ConfirmationTokenEntity token) {

        if(token.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs() / 1000).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Refresh token was expired. Please make a new sign in request");

    }

    @Override
    public void deleteAllTokenExpired(UserEntity user) {
        try{
            List<ConfirmationTokenEntity> confirmationTokenList = confirmationTokenRepository.findAllByUserAndType(user, "REFRESH_TOKEN");

            for(ConfirmationTokenEntity confirmationToken : confirmationTokenList){
                if(confirmationToken.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now())){

                    confirmationTokenRepository.delete(confirmationToken);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Don't delete token!");
        }

    }

}
