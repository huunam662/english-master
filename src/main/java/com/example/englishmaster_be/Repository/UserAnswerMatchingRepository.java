package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Model.UserAnswerMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAnswerMatchingRepository extends JpaRepository<UserAnswerMatching, UUID> {

    List<UserAnswerMatching> findAllByUserAndQuestion(User user, Question question);
}
