package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PartRepository extends JpaRepository<Part, UUID>, JpaSpecificationExecutor<Part> {
    Optional<Part> findByPartId(UUID partID);

    Page<Part> findByTopics(Topic topic, Pageable pageable);

    Optional<Part> findByPartName(String partName);
}
