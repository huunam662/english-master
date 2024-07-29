package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StatusRepository extends JpaRepository<Status, UUID> {
}
