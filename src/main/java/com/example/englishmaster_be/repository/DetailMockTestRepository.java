package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.DetailMockTestEntity;
import com.example.englishmaster_be.entity.MockTestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetailMockTestRepository extends JpaRepository<DetailMockTestEntity, UUID> {
    Page<DetailMockTestEntity> findAllByMockTest(MockTestEntity mockTest, Pageable pageable);

    List<DetailMockTestEntity> findAllByMockTest(MockTestEntity mockTest);
}
