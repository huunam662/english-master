package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.FlashCardEntity;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface FlashCardRepository extends JpaRepository<FlashCardEntity, UUID> {
    Optional<FlashCardEntity> findByFlashCardId(UUID flashCardID);

    List<FlashCardEntity> findByUser(UserEntity user, Sort sort);
}
