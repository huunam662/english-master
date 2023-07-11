package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("ConfirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {
    ConfirmationToken findByUserConfirmTokenId(UUID confirmToken);

    ConfirmationToken findByCodeAndType(String code, String type);

    ConfirmationToken findByUserAndType(User user, String type);

    List<ConfirmationToken> findAllByUserAndType(User user, String type);

}
