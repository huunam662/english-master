package com.example.englishmaster_be.domain.flash_card_word.repository;

import com.example.englishmaster_be.domain.flash_card.dto.projection.IFlashCardField2Projection;
import com.example.englishmaster_be.domain.flash_card_word.model.FlashCardWordEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashCardWordRepository extends JpaRepository<FlashCardWordEntity, UUID> {

    @Query("SELECT f FROM FlashCardWordEntity f WHERE LOWER(f.word) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FlashCardWordEntity> findFlashCartWordByQuery(Pageable pageable, @Param("query") String query);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE FROM flash_card_word
        WHERE flash_card_id = :flashCardId
    """, nativeQuery = true)
    void deleteAllByFlashCardId(@Param("flashCardId") UUID flashCardId);

    @Query(value = """
        SELECT fcw.flash_card_id as flashCardId, COALESCE(COUNT(fcw.id), 0) as countOfWords
        FROM flash_card_word fcw
        WHERE fcw.flash_card_id IN :flashCardIds
        GROUP BY fcw.flash_card_id
    """, nativeQuery = true)
    List<IFlashCardField2Projection> countWordsInFlashCardIds(@Param("flashCardIds") List<UUID> flashCardIds);

    @Query(value = """
        SELECT fcw.flash_card_id as flashCardId, COALESCE(COUNT(fcw.id), 0) as countOfWords
        FROM flash_card_word fcw
        WHERE fcw.flash_card_id = :flashCardId
        GROUP BY fcw.flash_card_id
    """, nativeQuery = true)
    IFlashCardField2Projection countWordsOfFlashCardId(@Param("flashCardId") UUID flashCardId);

}

