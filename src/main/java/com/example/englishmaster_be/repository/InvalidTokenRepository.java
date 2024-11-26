package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
}
