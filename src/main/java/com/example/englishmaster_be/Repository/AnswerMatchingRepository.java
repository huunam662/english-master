package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.AnswerMatching;
import com.example.englishmaster_be.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerMatchingRepository extends JpaRepository<AnswerMatching, UUID> {
    List<AnswerMatching> findAllByQuestion(Question question);
}
