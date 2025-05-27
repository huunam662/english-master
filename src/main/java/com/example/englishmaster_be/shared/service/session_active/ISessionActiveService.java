package com.example.englishmaster_be.shared.service.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.List;
import java.util.UUID;


public interface ISessionActiveService {


    SessionActiveEntity getByCode(UUID code);

    SessionActiveEntity getJoinUserByCodeAndType(UUID code, SessionActiveType type);

    SessionActiveEntity getJoinUserRoleByCodeAndType(UUID code, SessionActiveType type);

    SessionActiveEntity getByToken(String token);

    void deleteSessionCode(UUID sessionCode);

    SessionActiveEntity saveSessionActive(UserEntity user, String jwtToken);

    SessionActiveEntity saveForUser(UserEntity user, SessionActiveType type);

    void saveSessionActive(UUID userId, String jwtToken);

    boolean isExpirationToken(SessionActiveEntity token);

    void deleteAllTokenExpired(UserEntity user);

    void deleteBySessionEntity(SessionActiveEntity sessionActiveEntity);

    void deleteByUserIdAndType(UUID userId, SessionActiveType type);

    void deleteByToken(String token);

    void deleteByCode(UUID code);

    List<SessionActiveEntity> getSessionActiveList(UserEntity user, SessionActiveType sessionActiveType);

    void deleteAll(List<SessionActiveEntity> sessionActiveEntityList);
}
