package com.example.englishmaster_be.shared.service.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;


public interface ISessionActiveService {

    SessionActiveEntity getByCode(UUID code);

    void deleteSessionCode(UUID sessionCode);

    SessionActiveEntity saveSessionActive(UserDetails userDetails);

    void verifyExpiration(SessionActiveEntity token);

    void deleteAllTokenExpired(UserEntity user);

    List<SessionActiveEntity> getSessionActiveList(UUID userId, SessionActiveTypeEnum sessionActiveType);

}
