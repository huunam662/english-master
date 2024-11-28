package com.example.englishmaster_be.Repository;

import com.example.englishmaster_be.Model.AnswerBlank;
import com.example.englishmaster_be.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerBlankRepository extends JpaRepository<AnswerBlank, UUID> {


    List<AnswerBlank> findByQuestion(Question question);



}
