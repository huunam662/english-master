package com.example.englishmaster_be.model.mock_test_detail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MockTestDetailRepository extends JpaRepository<MockTestDetailEntity, UUID> {

//    Page<MockTestDetailEntity> findAllByMockTest(MockTestEntity mockTest, Pageable pageable);
//
//    List<MockTestDetailEntity> findAllByMockTest(MockTestEntity mockTest);
}
