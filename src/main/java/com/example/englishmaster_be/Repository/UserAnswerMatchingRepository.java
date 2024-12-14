package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.entity.UserAnswerMatchingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAnswerMatchingRepository extends JpaRepository<UserAnswerMatchingEntity, UUID> {

    List<UserAnswerMatchingEntity> findAllByUserAndQuestion(UserEntity user, QuestionEntity question);
}
