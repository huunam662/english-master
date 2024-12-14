package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.PackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PackRepository extends JpaRepository<PackEntity, UUID> {
    Optional<PackEntity> findByPackId(UUID packId);

    Optional<PackEntity> findByPackName(String topicPackName);
}
