package com.example.englishmaster_be.domain.flash_card.repository;

import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface FlashCardRepository extends JpaRepository<FlashCardEntity, UUID> {

    @Query("SELECT fc FROM FlashCardEntity fc WHERE fc.flashCardId = :flashCardId")
    Optional<FlashCardEntity> findByFlashCardId(@Param("flashCardId") UUID flashCardID);

    @Query("SELECT fc FROM FlashCardEntity fc WHERE fc.userCreate = :userCreate")
    List<FlashCardEntity> findByUserCreate(@Param("userCreate") UserEntity user, Sort sort);
}
