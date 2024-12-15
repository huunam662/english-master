package com.example.englishmaster_be.model.confirmation_token;

import com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenEntity, UUID> {
    ConfirmationTokenEntity findByUserConfirmTokenId(UUID confirmToken);

    ConfirmationTokenEntity findByCodeAndType(String code, ConfirmRegisterTypeEnum type);

    ConfirmationTokenEntity findByUserAndType(UserEntity user, ConfirmRegisterTypeEnum type);

    List<ConfirmationTokenEntity> findAllByUserAndType(UserEntity user, ConfirmRegisterTypeEnum type);

    void deleteByUserAndType(UserEntity user, ConfirmRegisterTypeEnum type);

    @Query("SELECT ct FROM ConfirmationTokenEntity ct WHERE ct.user.userId = :userId")
    Iterable<? extends ConfirmationTokenEntity> findByUserId(@Param("userId") UUID userId);
}
