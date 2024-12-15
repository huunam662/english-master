package com.example.englishmaster_be.model.detail_mock_test;

import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetailMockTestRepository extends JpaRepository<DetailMockTestEntity, UUID> {
    Page<DetailMockTestEntity> findAllByMockTest(MockTestEntity mockTest, Pageable pageable);

    List<DetailMockTestEntity> findAllByMockTest(MockTestEntity mockTest);
}
