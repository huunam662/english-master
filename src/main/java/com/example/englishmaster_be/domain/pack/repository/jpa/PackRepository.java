package com.example.englishmaster_be.domain.pack.repository.jpa;

import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
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

    @Query("""
        SELECT DISTINCT p.packId as packId, p.packTypeId as packTypeId
        FROM PackEntity p
        WHERE LOWER(p.packName) = LOWER(:packName)
    """)
    IPackKeyProjection findPackIdByName(@Param("packName") String packName);

}
