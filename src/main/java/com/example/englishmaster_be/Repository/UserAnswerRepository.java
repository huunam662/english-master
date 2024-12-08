package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


public interface UserAnswerRepository extends JpaRepository<UserAnswer, UUID> {

    UserAnswer findByUserAndQuestion(User user, Question question);
}
