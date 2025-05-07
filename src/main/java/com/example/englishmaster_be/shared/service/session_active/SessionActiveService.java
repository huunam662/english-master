package com.example.englishmaster_be.shared.service.session_active;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.model.session_active.SessionActiveQueryFactory;
import com.example.englishmaster_be.model.session_active.SessionActiveRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.value.JwtValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionActiveService implements ISessionActiveService {

    JwtValue jwtValue;

    JwtService jwtUtil;

    IUserService userService;

    SessionActiveQueryFactory sessionActiveQueryFactory;

    SessionActiveRepository sessionActiveRepository;

    UserRepository userRepository;



    @Override
    public SessionActiveEntity getByCode(UUID code) {

        return sessionActiveRepository.findByCode(code);
    }

    public SessionActiveEntity getByCodeAndType(UUID code, SessionActiveType type) {

        return sessionActiveRepository.findByCodeAndType(code, type);
    }

    @Override
    public SessionActiveEntity getByToken(String token) {

        return sessionActiveQueryFactory.findByToken(token).orElse(null);
    }

    @Transactional
    @Override
    public void deleteSessionCode(UUID sessionCode) {

        SessionActiveEntity confirmationToken = sessionActiveRepository.findByCode(sessionCode);

        if(confirmationToken != null)
            sessionActiveRepository.delete(confirmationToken);

    }

    @Transactional
    @Override
    public SessionActiveEntity saveSessionActive(UserEntity user, String jwtToken) {

        String tokenHash = jwtUtil.hashToHex(jwtToken);

        user.setLastLogin(LocalDateTime.now(ZoneId.systemDefault()));

        user = userRepository.save(user);

        SessionActiveEntity sessionActiveEntity = SessionActiveEntity.builder()
                .createAt(LocalDateTime.now(ZoneId.systemDefault()))
                .user(user)
                .code(UUID.randomUUID())
                .token(tokenHash)
                .type(SessionActiveType.REFRESH_TOKEN)
                .build();

        return sessionActiveRepository.save(sessionActiveEntity);
    }

    @Override
    public void verifyExpiration(SessionActiveEntity token) {

        if(token.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now()))
            throw new ErrorHolder(Error.BAD_REQUEST, "Refresh token was expired. Please make a new sign in request");

    }

    @Transactional
    @Override
    public void deleteAllTokenExpired(UserEntity user) {
        try{
            List<SessionActiveEntity> confirmationTokenList = sessionActiveRepository.findAllByUserAndType(user, SessionActiveType.REFRESH_TOKEN);

            for(SessionActiveEntity confirmationToken : confirmationTokenList){
                if(confirmationToken.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now())){

                    sessionActiveRepository.delete(confirmationToken);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("Don't delete token!");
        }

    }

    @Override
    public void deleteBySessionEntity(SessionActiveEntity sessionActiveEntity) {

        if(sessionActiveEntity == null) return;

        sessionActiveRepository.delete(sessionActiveEntity);
    }

    public List<SessionActiveEntity> getSessionActiveList(UUID userId, SessionActiveType sessionActiveType){

        UserEntity user = userService.getUserById(userId);

        return sessionActiveRepository.findAllByUserAndType(user, sessionActiveType);
    }

}
