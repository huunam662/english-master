package com.example.englishmaster_be.domain.user.auth.service.session_active;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.domain.user.auth.repository.SessionActiveQueryFactory;
import com.example.englishmaster_be.domain.user.auth.repository.SessionActiveRepository;
import com.example.englishmaster_be.domain.user.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.value.JwtValue;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionActiveService implements ISessionActiveService {

    private final JwtValue jwtValue;
    private final JwtService jwtUtil;
    private final IUserService userService;
    private final SessionActiveQueryFactory sessionActiveQueryFactory;
    private final SessionActiveRepository sessionActiveRepository;

    @Lazy
    public SessionActiveService(JwtValue jwtValue, JwtService jwtUtil, IUserService userService, SessionActiveQueryFactory sessionActiveQueryFactory, SessionActiveRepository sessionActiveRepository) {
        this.jwtValue = jwtValue;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.sessionActiveQueryFactory = sessionActiveQueryFactory;
        this.sessionActiveRepository = sessionActiveRepository;
    }

    @Override
    public SessionActiveEntity getByCode(UUID code) {

        return sessionActiveRepository.findByCode(code);
    }

    public SessionActiveEntity getJoinUserByCodeAndType(UUID code, SessionActiveType type) {

        return sessionActiveRepository.findJoinUserByCodeAndType(code, type)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Session active Not Found")
                );
    }

    public SessionActiveEntity getJoinUserRoleByCodeAndType(UUID code, SessionActiveType type) {

        return sessionActiveRepository.findJoinUserRoleByCodeAndType(code, type)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Session active Not Found")
                );
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

        userService.updateLastLoginTime(user.getUserId(), LocalDateTime.now(ZoneId.systemDefault()));

        SessionActiveEntity sessionActive = new SessionActiveEntity();
        sessionActive.setUser(user);
        sessionActive.setToken(tokenHash);
        sessionActive.setType(SessionActiveType.REFRESH_TOKEN);
        sessionActive.setCode(UUID.randomUUID());

        return sessionActiveRepository.save(sessionActive);
    }

    @Transactional
    @Override
    public SessionActiveEntity saveForUserRegister(UserEntity user, SessionActiveType type) {

        SessionActiveEntity sessionActive = new SessionActiveEntity();
        sessionActive.setUser(user);
        sessionActive.setCode(UUID.randomUUID());
        sessionActive.setType(type);

        return sessionActiveRepository.save(sessionActive);
    }

    @Transactional
    @Override
    public void saveSessionActive(UUID userId, String jwtToken) {

        sessionActiveRepository.insertSessionActive(
                UUID.randomUUID(),
                UUID.randomUUID(),
                SessionActiveType.REFRESH_TOKEN.name(),
                jwtUtil.hashToHex(jwtToken),
                LocalDateTime.now(),
                userId
        );
    }

    @Override
    public boolean isExpirationToken(SessionActiveEntity token) {

        return token.getCreateAt().plusSeconds(jwtValue.getJwtRefreshExpirationMs()/1000).isBefore(LocalDateTime.now());
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

    public List<SessionActiveEntity> getSessionActiveList(UserEntity user, SessionActiveType sessionActiveType){

        return sessionActiveRepository.findAllByUserAndType(user, sessionActiveType);
    }

    @Transactional
    @Override
    public void deleteByUserIdAndType(UUID userId, SessionActiveType type) {

        sessionActiveRepository.deleteByUserIdAndType(userId, type.name());
    }

    @Transactional
    @Override
    public void deleteByToken(String token) {

        String tokenHash = jwtUtil.hashToHex(token);

        sessionActiveRepository.deleteByToken(tokenHash);
    }

    @Transactional
    @Override
    public void deleteByCode(UUID code) {

        sessionActiveRepository.deleteByCode(code);
    }

    @Transactional
    @Override
    public void deleteAll(List<SessionActiveEntity> sessionActiveEntityList) {

        Set<UUID> ids = sessionActiveEntityList.stream().map(
                SessionActiveEntity::getSessionId
        ).collect(Collectors.toSet());

        sessionActiveRepository.deleteAllByIds(ids);
    }
}
