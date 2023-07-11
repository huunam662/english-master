package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MockTestRepository extends JpaRepository<MockTest, UUID> {
    Page<MockTest> findAll(Pageable pageable);

    Page<MockTest> findAllByUser(User user, Pageable pageable);
    MockTest findByMockTestId(UUID mockTestId);
}
