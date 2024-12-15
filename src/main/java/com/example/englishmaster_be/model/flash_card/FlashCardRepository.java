package com.example.englishmaster_be.model.flash_card;

import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface FlashCardRepository extends JpaRepository<FlashCardEntity, UUID> {
    Optional<FlashCardEntity> findByFlashCardId(UUID flashCardID);

    List<FlashCardEntity> findByUser(UserEntity user, Sort sort);
}
