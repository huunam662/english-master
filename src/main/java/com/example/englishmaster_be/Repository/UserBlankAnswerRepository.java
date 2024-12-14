package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.entity.QuestionEntity;
import com.example.englishmaster_be.entity.UserBlankAnswerEntity;
import com.example.englishmaster_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface UserBlankAnswerRepository extends JpaRepository<UserBlankAnswerEntity, UUID> {

    List<UserBlankAnswerEntity> getByUserAndQuestion(UserEntity user, QuestionEntity question);
    UserBlankAnswerEntity getByUserAndQuestionAndPosition(UserEntity user, QuestionEntity question, int position);
}
