package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartRepository extends JpaRepository<Part, UUID> {
    Optional<Part> findByPartId(UUID partID);

    Page<Part> findByTopics(Topic topic, Pageable pageable);
}
