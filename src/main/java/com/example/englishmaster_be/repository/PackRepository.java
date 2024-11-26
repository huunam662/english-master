package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PackRepository extends JpaRepository<Pack, UUID> {
    Optional<Pack> findByPackId(UUID packId);

    Optional<Pack> findByPackName(String topicPackName);
}
