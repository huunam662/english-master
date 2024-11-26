package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetailMockTestRepository extends JpaRepository<DetailMockTest, UUID> {
    Page<DetailMockTest> findAllByMockTest(MockTest mockTest, Pageable pageable);

    List<DetailMockTest> findAllByMockTest(MockTest mockTest);
}
