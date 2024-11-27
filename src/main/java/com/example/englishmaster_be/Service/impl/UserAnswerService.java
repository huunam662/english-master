package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAnswerService {
    private final AnswerBlankRepository answerBlankRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserBlankAnswerRepository userBlankAnswerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final AnswerRepository answerRepository;

    public boolean checkCorrectAnswerBlank(UUID questionId, UUID userId) {
        User user = userRepository.findByUserId(userId);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );


        List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);

        List<AnswerBlank> answerBlanks= answerBlankRepository.findByQuestion(question);
        Map<Integer, String> answerMap=answerBlanks.stream()
                .collect(Collectors.toMap(AnswerBlank::getPosition,AnswerBlank::getAnswer));

        for (UserBlankAnswer userBlankAnswer:answers){
            int pos=userBlankAnswer.getPosition();
            String answer=userBlankAnswer.getAnswer();

            if(!answer.equalsIgnoreCase(answerMap.get(pos))){
                System.out.println(answer+" "+ answerMap.get(pos));
                return false;

            }
        }

        return true;
    }

    public boolean checkCorrectAnswerMultipleChoice(UUID questionId, UUID userId){
        User user = userRepository.findByUserId(userId);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        List<Answer> answers=answerRepository.findByQuestion(question);

        UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);

        Set<UUID> correctAnswerIds=answers.stream()
                .filter(Answer::isCorrectAnswer)
                .map(Answer::getAnswerId)
                .collect(Collectors.toSet());

        Set<UUID> userAnswerIds=userAnswer.getAnswers().stream()
                .map(Answer::getAnswerId)
                .collect(Collectors.toSet());

        return correctAnswerIds.equals(userAnswerIds);

    }
}
