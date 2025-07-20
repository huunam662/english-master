package com.example.englishmaster_be.domain.mock_test.speaking_submission.repository;

import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpeakingErrorRepository extends JpaRepository<SpeakingErrorEntity, UUID> {

    @Query("""
        SELECT se FROM SpeakingErrorEntity se
        WHERE se.speakingSubmissionId IN :speakingSubmissionIds
    """)
    List<SpeakingErrorEntity> findAllSpeakingErrorIn(@Param("speakingSubmissionIds") List<UUID> speakingSubmissionIds);

}
