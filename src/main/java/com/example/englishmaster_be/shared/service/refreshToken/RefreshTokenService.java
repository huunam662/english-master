package com.example.englishmaster_be.shared.service.refreshToken;

import com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import com.example.englishmaster_be.model.user.UserEntity;
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
public class RefreshTokenService implements IRefreshTokenService {

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

        if(token.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Refresh token was expired. Please make a new sign in request");

    }

    @Override
    public void deleteAllTokenExpired(UserEntity user) {
        try{
            List<ConfirmationTokenEntity> confirmationTokenList = confirmationTokenRepository.findAllByUserAndType(user, ConfirmRegisterTypeEnum.REFRESH_TOKEN);

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
