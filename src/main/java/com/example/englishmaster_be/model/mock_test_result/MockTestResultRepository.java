package com.example.englishmaster_be.model.mock_test_result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;


public interface MockTestResultRepository extends JpaRepository<MockTestResultEntity, UUID>, QuerydslPredicateExecutor<MockTestResultEntity> {

    List<MockTestResultEntity> findByMockTest_MockTestId(UUID mockTestId);

}
