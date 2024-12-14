package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.common.constaint.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.entity.ConfirmationTokenEntity;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, UUID> {
    ConfirmationTokenEntity findByUserConfirmTokenId(UUID confirmToken);

    ConfirmationTokenEntity findByCodeAndType(String code, ConfirmRegisterTypeEnum type);

    ConfirmationTokenEntity findByUserAndType(UserEntity user, String type);

    List<ConfirmationTokenEntity> findAllByUserAndType(UserEntity user, String type);

    @Query("SELECT ct FROM ConfirmationTokenEntity ct WHERE ct.user.userId = :userId")
    Iterable<? extends ConfirmationTokenEntity> findByUserId(@Param("userId") UUID userId);

}
