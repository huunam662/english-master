package com.example.englishmaster_be.domain.flash_card.word.repository;

import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlashCardWordRepository extends JpaRepository<FlashCardWordEntity, UUID>, JpaSpecificationExecutor<FlashCardWordEntity> {

    @Query("""
        SELECT fw FROM FlashCardWordEntity fw
        INNER JOIN FETCH fw.flashCard
        INNER JOIN FETCH fw.createBy
        LEFT JOIN FETCH fw.updateBy
        WHERE fw.id = :id
    """)
    Optional<FlashCardWordEntity> findEntityById(@Param("id") UUID id);

    @Query("""
        SELECT fw FROM FlashCardWordEntity fw
        INNER JOIN FETCH fw.flashCard
        INNER JOIN FETCH fw.createBy
        LEFT JOIN FETCH fw.updateBy
    """)
    List<FlashCardWordEntity> findAllEntity();

    @Query("""
        SELECT fw FROM FlashCardWordEntity fw
        INNER JOIN FETCH fw.flashCard fc
        INNER JOIN FETCH fw.createBy cb
        LEFT JOIN FETCH fw.updateBy
        WHERE fc.id = :flashCardId
    """)
    List<FlashCardWordEntity> findAllEntityByFlashCardId(@Param("flashCardId") UUID flashCardId);

    @Query("""
        SELECT fw FROM FlashCardWordEntity fw WHERE fw.id IN :ids
    """)
    List<FlashCardWordEntity> findAllEntityIdInIds(@Param("ids") List<UUID> ids);

}

