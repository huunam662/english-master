package com.example.englishmaster_be.model.pack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PackRepository extends JpaRepository<PackEntity, UUID> {

    Optional<PackEntity> findByPackId(UUID packId);

    @Query("SELECT p FROM PartEntity p WHERE LOWER(p.partName) = LOWER(:topicPackName)")
    Optional<PackEntity> findByPackName(@Param("topicPackName") String topicPackName);

    @Query("""
        SELECT p FROM PackEntity p
        WHERE p.packTypeId = :packTypeId
    """)
    List<PackEntity> getAllByPackTypeId(@Param("packTypeId") UUID packTypeId);
}
