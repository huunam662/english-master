package com.example.englishmaster_be.model.invalid_token;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.UUID;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {

    @Modifying
    @Query(value = """
        INSERT INTO invalid_token(token, expire_time, create_at, type, user_id)
        VALUES (:token, :expireTime, :createAt, :type, :userId)
    """, nativeQuery = true)
    void insertInvalidToken(
            @Param("token") String token,
            @Param("expireTime") LocalDateTime expireTime,
            @Param("createAt") LocalDateTime createAt,
            @Param("type") String type,
            @Param("userId") UUID userId
    );


    @Query(value = """
        SELECT EXISTS(SELECT t.token FROM invalid_token t WHERE t.token = :token)
    """, nativeQuery = true)
    boolean isValidToken(@Param("token") String token);

}
