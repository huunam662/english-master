package com.example.englishmaster_be.model.mock_test_detail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MockTestDetailRepository extends JpaRepository<MockTestDetailEntity, UUID> {

//    Page<MockTestDetailEntity> findAllByMockTest(MockTestEntity mockTest, Pageable pageable);
//
//    List<MockTestDetailEntity> findAllByMockTest(MockTestEntity mockTest);

    @Query("""
        SELECT DISTINCT mtd FROM MockTestDetailEntity mtd
        INNER JOIN FETCH mtd.questionChild qc
        INNER JOIN FETCH qc.questionGroupParent qp
        INNER JOIN FETCH mtd.answerChoice ac
        INNER JOIN FETCH mtd.resultMockTest mtr
        INNER JOIN FETCH mtr.part p
        WHERE mtr.mockTestId = :mockTestId
    """)
    List<MockTestDetailEntity> findMockDetailJoinQuestionAnswerMockResultPartByMockId(@Param("mockTestId") UUID mockTestId);
}
