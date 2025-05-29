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

    @Query("SELECT DISTINCT p FROM PartEntity p JOIN FETCH p.topics t WHERE t.topicId =:topicId ORDER BY p.partName ASC")
    Page<PartEntity> findByTopics(UUID topicId, Pageable pageable);

    @Query("SELECT p FROM PartEntity p WHERE LOWER(p.partName) = LOWER(:partName)")
    Optional<PartEntity> findByPartName(@Param("partName") String partName);

    @Query("SELECT EXISTS(SELECT p FROM PartEntity p WHERE p != :part AND LOWER(p.partName) = LOWER(:partName))")
    boolean isExistedPartNameWithDiff(@Param("part") PartEntity part, @Param("partName") String partName);

    @Query(value = """
        SELECT EXISTS(SELECT p.id FROM part p WHERE p.part_name = :partName AND p.part_type = :partType)
    """, nativeQuery = true)
    boolean isExistedByPartNameAndPartType(
            @Param("partName") String partName,
            @Param("partType") String partType
    );

    @Query(value = """
        SELECT EXISTS(SELECT p.id FROM part p WHERE p.id = :partId)
    """, nativeQuery = true)
    boolean isExistedByPartId(@Param("partId") UUID partId);
}
