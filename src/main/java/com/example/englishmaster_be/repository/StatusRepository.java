package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepository extends JpaRepository<Status, UUID> {
    @Query("SELECT s FROM Status s WHERE s.statusName LIKE :statusName")
    Optional<Status> findByStatusName(String statusName);
}
