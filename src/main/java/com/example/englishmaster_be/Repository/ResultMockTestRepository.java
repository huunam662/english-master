package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.ResultMockTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ResultMockTestRepository extends JpaRepository<ResultMockTest, UUID>, QuerydslPredicateExecutor<ResultMockTest> {

}
