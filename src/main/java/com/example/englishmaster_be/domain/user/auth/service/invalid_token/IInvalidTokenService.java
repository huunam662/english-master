package com.example.englishmaster_be.domain.user.auth.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IInvalidTokenService {

    boolean inValidToken(String token);

    void saveInvalidToken(String token, UUID userId, InvalidTokenType typeInvalid);

    void saveInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, UserEntity user, InvalidTokenType typeInvalid);

    void sessionActiveToInvalidToken(String tokenHash, UUID userId, InvalidTokenType typeInvalid);

}
