package com.example.englishmaster_be.model.user_answer_matching;

import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAnswerMatchingRepository extends JpaRepository<UserAnswerMatchingEntity, UUID> {

    List<UserAnswerMatchingEntity> findAllByUserAndQuestion(UserEntity user, QuestionEntity question);
}
