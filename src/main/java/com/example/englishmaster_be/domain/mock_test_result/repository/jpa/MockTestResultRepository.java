package com.example.englishmaster_be.domain.mock_test_result.repository.jpa;

import com.example.englishmaster_be.domain.mock_test_result.model.MockTestResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface MockTestResultRepository extends JpaRepository<MockTestResultEntity, UUID>, QuerydslPredicateExecutor<MockTestResultEntity> {

    List<MockTestResultEntity> findByMockTest_MockTestId(UUID mockTestId);

    @Query("""
        SELECT mtr FROM MockTestResultEntity mtr
        INNER JOIN FETCH mtr.mockTest mt
        INNER JOIN FETCH mtr.part p
        WHERE mt.mockTestId = :mockTestId
        ORDER BY p.partName
    """)
    List<MockTestResultEntity> findResultMockTestJoinMockTestAndPart(@Param("mockTestId") UUID mockTestId);

    @Query("""
        SELECT mtr FROM MockTestResultEntity mtr
        INNER JOIN FETCH mtr.mockTest mt
        INNER JOIN FETCH mtr.part p
        LEFT JOIN FETCH mtr.mockTestDetails mtd
        LEFT JOIN FETCH mtd.questionChild qc
        LEFT JOIN FETCH qc.questionGroupParent qp
        LEFT JOIN FETCH mtd.answerChoice ac
        WHERE mt.mockTestId = :mockTestId
        ORDER BY p.partName ASC
    """)
    List<MockTestResultEntity> findResultJoinPartQuestionAnswer(@Param("mockTestId") UUID mockTestId);

}
