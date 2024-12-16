package com.example.englishmaster_be.model.user_answer;

import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface UserAnswerRepository extends JpaRepository<UserAnswerEntity, UUID> {

    UserAnswerEntity findByUserAndQuestion(UserEntity user, QuestionEntity question);
}
