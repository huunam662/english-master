package com.example.englishmaster_be.repository;

import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserAnswerEntity;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserAnswerRepository extends JpaRepository<UserAnswerEntity, UUID> {

    UserAnswerEntity findByUserAndQuestion(UserEntity user, QuestionEntity question);
}
