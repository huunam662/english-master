package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("ConfirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
    ConfirmationToken findByUserConfirmTokenId(UUID confirmToken);

    ConfirmationToken findByCodeAndType(String code, String type);

    ConfirmationToken findByUserAndType(User user, String type);

    List<ConfirmationToken> findAllByUserAndType(User user, String type);

    @Query("SELECT ct FROM ConfirmationToken ct WHERE ct.user.userId = :userId")
    Iterable<? extends ConfirmationToken> findByUserId(@Param("userId") UUID userId);
}
