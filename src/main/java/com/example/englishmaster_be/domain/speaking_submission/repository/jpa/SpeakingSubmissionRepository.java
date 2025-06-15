package com.example.englishmaster_be.domain.speaking_submission.repository.jpa;

import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpeakingSubmissionRepository extends JpaRepository<SpeakingSubmissionEntity, UUID> {

}
