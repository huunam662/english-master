package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.DTO.Type.QuestionType;
import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public void createUserAnswer(UserAnswerRequest request){
        User user = userRepository.findByUserId(request.getUserId());

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(request.getQuestionId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );
        Answer answer=answerRepository.findByAnswerId(request.getAnswerId())
                .orElseThrow(
                        ()-> new ResourceNotFoundException("Answer not found")
                );

        if(Objects.equals(question.getQuestionType(),QuestionType.Multiple_Choice) || Objects.equals(question.getQuestionType(),QuestionType.T_F_Not_Given)){
            UserAnswer userAns=userAnswerRepository.findByUserAndQuestion(user,question);

            if(Objects.isNull(userAns)){
                userAns.setUser(user);
                userAns.setQuestion(question);
                userAns.setAnswers(List.of(answer));
                userAnswerRepository.save(userAns);
                return;
            }

            List<Answer> answerList=userAns.getAnswers();
            answerList.add(answer);
            userAns.setAnswers(answerList);
            userAnswerRepository.save(userAns);
        }
        else if(Objects.equals(question.getQuestionType(),QuestionType.Fill_In_Blank)){
            UserBlankAnswer userBlankAnswer= new UserBlankAnswer();
            userBlankAnswer.setUser(user);
            userBlankAnswer.setPosition(request.getPosition());
            userBlankAnswer.setAnswer(request.getContent());
            userBlankAnswer.setQuestion(question);
            userBlankAnswerRepository.save(userBlankAnswer);
        }


    }

    @Transactional
    public void deleteAnswer(UUID questionId, UUID userId){
        User user = userRepository.findByUserId(userId);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );
        if(Objects.equals(question.getQuestionType(),QuestionType.Fill_In_Blank)){
            List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
            for (int i=0;i<answers.size();i++){
                userBlankAnswerRepository.deleteById(answers.get(i).getId());
            }
        }
        else if(Objects.equals(question.getQuestionType(),QuestionType.Multiple_Choice) || Objects.equals(question.getQuestionType(),QuestionType.T_F_Not_Given)){
            UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
            userAnswerRepository.deleteById(userAnswer.getId());
        }
    }
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


        UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
        for (Answer answer:userAnswer.getAnswers()){
            if(!answer.isCorrectAnswer())
                return false;
        }

        return true;



    }

    public Map<String,Integer> scoreAnswer(UUID questionId, UUID userId){
        User user = userRepository.findByUserId(userId);

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        Map<String,Integer> score= new HashMap<>();
        if(Objects.equals(question.getQuestionType(), QuestionType.Fill_In_Blank)){
            if(!checkCorrectAnswerBlank(questionId,userId)){
                score.put("scoreAnswer",0);
            }
            else{
                List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
                List<AnswerBlank> answerBlanks= answerBlankRepository.findByQuestion(question);
                score.put("scoreAnswer",(question.getQuestionScore()/answerBlanks.size())* answers.size());
            }
            return score;
        }
        else if(Objects.equals(question.getQuestionType(),QuestionType.Multiple_Choice)){
            if (checkCorrectAnswerMultipleChoice(questionId,userId)){
                UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
                int n=question.getAnswers().size();
                score.put("scoreAnswer",question.getQuestionScore()/n*userAnswer.getAnswers().size());
            }
            else{
                score.put("scoreAnswer",0);
            }
            return score;
        }
        else if (Objects.equals(question.getQuestionType(),QuestionType.T_F_Not_Given))
        {
            UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
            if(userAnswer.getAnswers().get(0).isCorrectAnswer()){
                score.put("scoreAnswer",userAnswer.getQuestion().getQuestionScore());
            }
            else {
                score.put("scoreAnswer",0);
            }
            return score;
        }
        score.put("scoreAnswer",0);
        return score;
    }

}
