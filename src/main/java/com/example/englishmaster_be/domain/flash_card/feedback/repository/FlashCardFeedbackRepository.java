package com.example.englishmaster_be.domain.flash_card.feedback.repository;

import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashCardFeedbackRepository extends JpaRepository<FlashCardFeedbackEntity, UUID>, JpaSpecificationExecutor<FlashCardFeedbackEntity> {

    @Query("""
        SELECT f FROM FlashCardFeedbackEntity f
        INNER JOIN FETCH f.flashCard
        INNER JOIN FETCH f.userFeedback
        WHERE f.id = :id
    """)
    Optional<FlashCardFeedbackEntity> findEntityById(@Param("id") UUID id);

    @Query("""
        SELECT f FROM FlashCardFeedbackEntity f
        INNER JOIN FETCH f.flashCard
        INNER JOIN FETCH f.userFeedback
    """)
    List<FlashCardFeedbackEntity> findAllEntity();
}
