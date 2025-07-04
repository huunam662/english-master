package com.example.englishmaster_be.domain.flash_card.flash_card.repository;

import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Repository
public interface FlashCardRepository extends JpaRepository<FlashCardEntity, UUID>, JpaSpecificationExecutor<FlashCardEntity> {

    @Transactional
    @Modifying
    @Query("""
        UPDATE FlashCardEntity f SET f.publicShared = :publicShared WHERE f.id = :id
    """)
    void updatePublicSharedById(@Param("id") UUID id, @Param("publicShared") boolean publicShared);

}
