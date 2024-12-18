package com.example.englishmaster_be.model.otp;

import com.example.englishmaster_be.common.constant.OtpStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {

    @Modifying
    @Query("DELETE FROM OtpEntity o WHERE o.email = :email AND o.status = :status")
    void deleteByEmailAndStatus(@Param("email") String email, @Param("status") OtpStatusEnum status);

    @Modifying
    void deleteByEmail(String email);

    Optional<OtpEntity> findByOtp(String otp);

    Optional<OtpEntity> findByEmailAndOtp(String email, String otp);
}
