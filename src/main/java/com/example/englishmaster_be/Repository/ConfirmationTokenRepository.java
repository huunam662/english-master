package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("ConfirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
    ConfirmationToken findByUserConfirmTokenId(UUID confirmToken);

}
