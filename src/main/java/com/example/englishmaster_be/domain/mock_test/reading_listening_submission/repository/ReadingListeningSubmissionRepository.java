package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.repository;

import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReadingListeningSubmissionRepository extends JpaRepository<ReadingListeningSubmissionEntity, UUID> {


    @Query("""
        SELECT DISTINCT mtd FROM ReadingListeningSubmissionEntity mtd
        INNER JOIN FETCH mtd.answerChoice ac
        INNER JOIN FETCH ac.question qc
        INNER JOIN FETCH qc.part p
        LEFT JOIN FETCH qc.answers aqc
        WHERE mtd.mockTestId = :mockTestId
    """)
    List<ReadingListeningSubmissionEntity> findMockDetailJoinUpperLayerByMockId(@Param("mockTestId") UUID mockTestId);
}
