package com.example.englishmaster_be.model.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SessionActiveRepository extends JpaRepository<SessionActiveEntity, UUID> {

    SessionActiveEntity findBySessionId(UUID sessionId);

    SessionActiveEntity findByCode(UUID code);

    @Query("""
        SELECT DISTINCT s
        FROM SessionActiveEntity s
        INNER JOIN FETCH s.user
        WHERE s.code = :code AND s.type = :type
    """)
    Optional<SessionActiveEntity> findByCodeAndType(@Param("code") UUID code, @Param("type") SessionActiveType type);

    SessionActiveEntity findByUserAndType(UserEntity user, SessionActiveType type);

    List<SessionActiveEntity> findAllByUserAndType(UserEntity user, SessionActiveType type);

    void deleteByUserAndType(UserEntity user, SessionActiveType type);

    @Query("SELECT s FROM SessionActiveEntity s WHERE s.user.userId = :userId")
    Iterable<? extends SessionActiveEntity> findByUserId(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM session_active
        WHERE user_id = :userId AND type = :type
    """, nativeQuery = true)
    void deleteByUserIdAndType(@Param("userId") UUID userId, @Param("type") String type);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM session_active WHERE token = :token", nativeQuery = true)
    void deleteByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM session_active WHERE code = :code", nativeQuery = true)
    void deleteByCode(@Param("code") UUID code);
}
