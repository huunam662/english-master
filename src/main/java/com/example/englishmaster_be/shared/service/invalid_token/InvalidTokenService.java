package com.example.englishmaster_be.shared.service.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenEntity;
import com.example.englishmaster_be.model.invalid_token.InvalidTokenRepository;
import com.example.englishmaster_be.shared.service.session_active.ISessionActiveService;
import com.example.englishmaster_be.value.JwtValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidTokenService implements IInvalidTokenService {

    JwtValue jwtValue;

    JwtService jwtUtil;

    InvalidTokenRepository invalidTokenRepository;

    ISessionActiveService sessionActiveService;


    @Override
    public boolean inValidToken(String token) {

        String tokenHash = jwtUtil.hashToHex(token);

        Optional<InvalidTokenEntity> tokenExpire = invalidTokenRepository.findById(tokenHash);

        return tokenExpire.isPresent();
    }

    @Transactional
    @Override
    public void saveInvalidToken(SessionActiveEntity sessionActive, InvalidTokenType typeInvalid) {

        if(sessionActive == null) return;

        LocalDateTime expireTime = new Date(
                sessionActive.getCreateAt().toInstant(ZoneOffset.UTC).toEpochMilli() + jwtValue.getJwtExpiration()
        ).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        InvalidTokenEntity invalidToken = InvalidTokenEntity.builder()
                .expireTime(expireTime)
                .createAt(LocalDateTime.now(ZoneId.systemDefault()))
                .token(sessionActive.getToken())
                .user(sessionActive.getUser())
                .type(typeInvalid)
                .build();

        invalidTokenRepository.save(invalidToken);

    }

    @Transactional
    @Override
    public void saveInvalidToken(String token, UUID userId, InvalidTokenType typeInvalid) {

        if(token == null) return;

        if(userId == null) return;

        String tokenHash = jwtUtil.hashToHex(token);

        LocalDateTime expiredTimeToken = jwtUtil.getTokenExpireFromJWT(token);

        invalidTokenRepository.insertInvalidToken(
                tokenHash, expiredTimeToken, LocalDateTime.now(), typeInvalid, userId
        );

        sessionActiveService.deleteByToken(tokenHash);
    }

    @Transactional
    @Override
    public void saveInvalidTokenList(List<SessionActiveEntity> sessionActiveEntityList, InvalidTokenType typeInvalid) {

        sessionActiveEntityList.forEach(sessionActiveEntity -> saveInvalidToken(sessionActiveEntity, typeInvalid));
    }

    @Override
    public void saveInvalidToken(String token, SessionActiveType type) {

    }
}
