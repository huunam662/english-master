package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DetailMockTestRepository extends JpaRepository<DetailMockTest, UUID> {
    Page<DetailMockTest> findAllByMockTest(MockTest mockTest, Pageable pageable);
}
