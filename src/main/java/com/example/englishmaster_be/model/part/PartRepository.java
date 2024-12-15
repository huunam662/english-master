package com.example.englishmaster_be.model.part;

import com.example.englishmaster_be.model.topic.TopicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PartRepository extends JpaRepository<PartEntity, UUID>, JpaSpecificationExecutor<PartEntity> {

    Optional<PartEntity> findByPartId(UUID partID);

    Page<PartEntity> findByTopics(TopicEntity topic, Pageable pageable);

    @Query("SELECT p FROM PartEntity p WHERE LOWER(p.partName) = LOWER(:partName)")
    Optional<PartEntity> findByPartName(@Param("partName") String partName);

    @Query("SELECT EXISTS(SELECT p FROM PartEntity p WHERE p != :part AND LOWER(p.partName) = LOWER(:partName))")
    boolean isExistedPartNameWithDiff(@Param("part") PartEntity part, @Param("partName") String partName);

}
