package com.example.englishmaster_be.shared.session_active.service;

import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;


public interface ISessionActiveService {

    SessionActiveEntity getByCode(UUID code);

    SessionActiveEntity getByCodeAndType(UUID code, SessionActiveTypeEnum type);

    SessionActiveEntity getByToken(String token);

    void deleteSessionCode(UUID sessionCode);

    SessionActiveEntity saveSessionActive(UserEntity user, String jwtToken);

    void verifyExpiration(SessionActiveEntity token);

    void deleteAllTokenExpired(UserEntity user);

    void deleteBySessionEntity(SessionActiveEntity sessionActiveEntity);

    List<SessionActiveEntity> getSessionActiveList(UUID userId, SessionActiveTypeEnum sessionActiveType);

}
