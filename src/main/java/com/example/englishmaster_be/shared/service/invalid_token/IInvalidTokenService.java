package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;

import java.util.List;
import java.util.UUID;

public interface IInvalidTokenService {

    boolean inValidToken(String token);

    void saveInvalidToken(String token, UUID userId, InvalidTokenType typeInvalid);

    void saveInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, UserEntity user, InvalidTokenType typeInvalid);

    void sessionActiveToInvalidToken(String tokenHash, UUID userId, InvalidTokenType typeInvalid);

}
