package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContentRepository extends JpaRepository<Content, UUID> {
    Optional<Content> findByContentId(UUID contentUId);
}
