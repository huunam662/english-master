package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.ResultMockTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;


public interface ResultMockTestRepository extends JpaRepository<ResultMockTestEntity, UUID>, QuerydslPredicateExecutor<ResultMockTestEntity> {

    List<ResultMockTestEntity> findByMockTest_MockTestId(UUID mockTestId);

}
