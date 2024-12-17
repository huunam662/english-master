package com.example.englishmaster_be.model.session_active;

import com.example.englishmaster_be.common.constant.SessionActiveTypeEnum;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface SessionActiveRepository extends JpaRepository<SessionActiveEntity, UUID> {

    SessionActiveEntity findBySessionId(UUID sessionId);

    SessionActiveEntity findByCode(UUID code);

    SessionActiveEntity findByCodeAndType(UUID code, SessionActiveTypeEnum type);

    SessionActiveEntity findByUserAndType(UserEntity user, SessionActiveTypeEnum type);

    List<SessionActiveEntity> findAllByUserAndType(UserEntity user, SessionActiveTypeEnum type);

    void deleteByUserAndType(UserEntity user, SessionActiveTypeEnum type);

    @Query("SELECT s FROM SessionActiveEntity s WHERE s.user.userId = :userId")
    Iterable<? extends SessionActiveEntity> findByUserId(@Param("userId") UUID userId);
}
