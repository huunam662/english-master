package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Answer findByAnswerId(UUID answerId);
}
