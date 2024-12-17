package com.example.englishmaster_be.shared.service.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.JwtUtil;
import com.example.englishmaster_be.value.JwtValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionActiveService implements ISessionActiveService {

    JwtValue jwtValue;

    IUserService userService;

    SessionActiveRepository sessionActiveRepository;

    JwtUtil jwtUtil;


    @Override
    public SessionActiveEntity getByCode(UUID code) {

        return sessionActiveRepository.findByCodeAndType(code, SessionActiveTypeEnum.REFRESH_TOKEN);
    }

    @Override
    public void deleteSessionCode(UUID sessionCode) {

        SessionActiveEntity confirmationToken = sessionActiveRepository.findByCodeAndType(sessionCode, SessionActiveTypeEnum.REFRESH_TOKEN);

        if(confirmationToken != null)
            sessionActiveRepository.delete(confirmationToken);

    }

    @Override
    public SessionActiveEntity saveSessionActive(UserDetails userDetails) {

        UserEntity user = userService.getUserByEmail(userDetails.getUsername());

        String tokenJwt = jwtUtil.generateToken(userDetails);

        SessionActiveEntity sessionActiveEntity = SessionActiveEntity.builder()
                .createAt(LocalDateTime.now())
                .user(user)
                .code(UUID.randomUUID())
                .token(tokenJwt)
                .type(SessionActiveTypeEnum.REFRESH_TOKEN)
                .build();

        return sessionActiveRepository.save(sessionActiveEntity);
    }

    @Override
    public void verifyExpiration(SessionActiveEntity token) {

        if(token.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Refresh token was expired. Please make a new sign in request");

    }

    @Override
    public void deleteAllTokenExpired(UserEntity user) {
        try{
            List<SessionActiveEntity> confirmationTokenList = sessionActiveRepository.findAllByUserAndType(user, SessionActiveTypeEnum.REFRESH_TOKEN);

            for(SessionActiveEntity confirmationToken : confirmationTokenList){
                if(confirmationToken.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now())){

                    sessionActiveRepository.delete(confirmationToken);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Don't delete token!");
        }

    }

    public List<SessionActiveEntity> getSessionActiveList(UUID userId, SessionActiveTypeEnum sessionActiveType){

        UserEntity user = userService.getUserById(userId);

        return sessionActiveRepository.findAllByUserAndType(user, sessionActiveType);
    }

}
