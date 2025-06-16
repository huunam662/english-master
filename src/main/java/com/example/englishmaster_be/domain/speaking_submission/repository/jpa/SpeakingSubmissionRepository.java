package com.example.englishmaster_be.domain.speaking_submission.repository.jpa;

import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpeakingSubmissionRepository extends JpaRepository<SpeakingSubmissionEntity, UUID> {

    @Query("""
        SELECT sp FROM SpeakingSubmissionEntity sp
        INNER JOIN FETCH sp.question q
        INNER JOIN FETCH sp.mockTest mt
        INNER JOIN FETCH mt.topic t
        WHERE mt.mockTestId = :mockTestId
    """)
    List<SpeakingSubmissionEntity> findAllByMockTestId(@Param("mockTestId") UUID mockTestId);

}
