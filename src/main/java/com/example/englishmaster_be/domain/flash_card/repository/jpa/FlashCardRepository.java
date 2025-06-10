package com.example.englishmaster_be.domain.flash_card.repository.jpa;

import com.example.englishmaster_be.domain.flash_card.dto.projection.IFlashCardField1Projection;
import com.example.englishmaster_be.domain.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.awt.print.Pageable;
import java.util.*;

public interface FlashCardRepository extends JpaRepository<FlashCardEntity, UUID>, QuerydslPredicateExecutor<FlashCardEntity>{

    @Query("SELECT fc FROM FlashCardEntity fc WHERE fc.flashCardId = :flashCardId")
    Optional<FlashCardEntity> findByFlashCardId(@Param("flashCardId") UUID flashCardID);

    @Query("SELECT fc FROM FlashCardEntity fc WHERE fc.userCreate = :userCreate")
    List<FlashCardEntity> findByUserCreate(@Param("userCreate") UserEntity user, Sort sort);

    @Query(value = """
        SELECT fc.id as flashCardId, 
               fc.user_id as ownerId,
               fc.image as flashCardImage
        FROM flash_card fc
        WHERE fc.id = :flashCardId
    """, nativeQuery = true)
    IFlashCardField1Projection findInToProjection1dByFlashCardId(@Param("flashCardId") UUID flashCardId);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM flash_card
        WHERE id = :flashCardId
    """, nativeQuery = true)
    void deleteByFlashCardId(@Param("flashCardId") UUID flashCardId);

    @Query("""
        SELECT fc FROM FlashCardEntity fc
        INNER JOIN FETCH fc.flashCardOwner owner
        WHERE fc.flashCardId = :flashCardId
        AND owner.userId = :userId
    """)
    Optional<FlashCardEntity> findJoinOwner(
            @Param("flashCardId") UUID flashCardId,
            @Param("userId") UUID userId
    );


}
