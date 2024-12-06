package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.DTO.Answer.UserAnswerRequest;
import com.example.englishmaster_be.DTO.Type.QuestionType;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
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
    private final QuestionRepository questionRepository;
    private final UserBlankAnswerRepository userBlankAnswerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final AnswerRepository answerRepository;
    private final UserServiceImpl userService;
    private final UserAnswerMatchingRepository userAnswerMatchingRepository;
    private final AnswerMatchingRepository answerMatchingRepository;

    @Transactional
    public UserAnswer createUserAnswer(UserAnswerRequest request){
        User user = userService.currentUser();

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



        UserAnswer userAns=userAnswerRepository.findByUserAndQuestion(user,question);


        if(Objects.isNull(userAns)){
            userAns=new UserAnswer();
            userAns.setUser(user);
            userAns.setQuestion(question);
            userAns.setAnswers(List.of(answer));
            userAns.setNumberChoice(1);
            userAnswerRepository.save(userAns);
            return userAns;
        }

        int numberChoice=userAns.getNumberChoice();
        if(question.getNumberChoice()<numberChoice+1){
            throw new BadRequestException("The number of choices must be less than or equal to "+question.getNumberChoice());
        }

        List<Answer> answerList=userAns.getAnswers();
        answerList.add(answer);
        userAns.setAnswers(answerList);
        userAns.setNumberChoice(numberChoice+1);
        userAnswerRepository.save(userAns);

        return userAns;
    }



    public UserBlankAnswer createUserBlankAnswer(AnswerBlankRequest request) {
        User user = userService.currentUser();
        if(Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(request.getQuestionId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        UserBlankAnswer answer=new UserBlankAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setAnswer(request.getContent());
        answer.setPosition(request.getPosition());

        return userBlankAnswerRepository.save(answer);


    }




    @Transactional
    public void deleteAnswer(UUID questionId){
        User user = userService.currentUser();

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );
        if(Objects.equals(question.getQuestionType(),QuestionType.Fill_In_Blank)){
            List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
            for (UserBlankAnswer answer : answers) {
                userBlankAnswerRepository.deleteById(answer.getId());
            }
        }
        else if(Objects.equals(question.getQuestionType(),QuestionType.Multiple_Choice) || Objects.equals(question.getQuestionType(),QuestionType.T_F_Not_Given)){
            UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
            userAnswerRepository.deleteById(userAnswer.getId());
        }
    }
//    public boolean checkCorrectAnswerBlank(UUID questionId) {
//        User user = userService.currentUser();
//
//        if (Objects.isNull(user))
//            throw new ResourceNotFoundException("User not found");
//
//        Question question = questionRepository.findByQuestionId(questionId)
//                .orElseThrow(
//                        () -> new ResourceNotFoundException("Question not found")
//                );
//
//
//        List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
//
//        List<AnswerBlank> answerBlanks= answerBlankRepository.findByQuestion(question);
//        Map<Integer, String> answerMap=answerBlanks.stream()
//                .collect(Collectors.toMap(AnswerBlank::getPosition,AnswerBlank::getAnswer));
//
//        for (UserBlankAnswer userBlankAnswer:answers){
//            int pos=userBlankAnswer.getPosition();
//            String answer=userBlankAnswer.getAnswer();
//
//            if(!answer.equalsIgnoreCase(answerMap.get(pos))){
//                return false;
//
//            }
//        }
//
//        return true;
//    }

    public int scoreAnswerBlank(UUID questionId) {
        User user = userService.currentUser();

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );


        int score=0;

        List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);

        List<AnswerBlank> answerBlanks= answerBlankRepository.findByQuestion(question);
        Map<Integer, String> answerMap=answerBlanks.stream()
                .collect(Collectors.toMap(AnswerBlank::getPosition,AnswerBlank::getAnswer));

        for (UserBlankAnswer userBlankAnswer:answers){
            int pos=userBlankAnswer.getPosition();
            String answer=userBlankAnswer.getAnswer();

            if(answer.equalsIgnoreCase(answerMap.get(pos))){
                score+= question.getQuestionScore()/answerBlanks.size();

            }
        }

        return score;
    }


//    public boolean checkCorrectAnswerMultipleChoice(UUID questionId){
//        User user = userService.currentUser();
//
//        if (Objects.isNull(user))
//            throw new ResourceNotFoundException("User not found");
//
//        Question question = questionRepository.findByQuestionId(questionId)
//                .orElseThrow(
//                        () -> new ResourceNotFoundException("Question not found")
//                );
//
//
//        UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
//        for (Answer answer:userAnswer.getAnswers()){
//            if(!answer.isCorrectAnswer())
//                return false;
//        }
//
//        return true;
//
//
//
//    }

    public int scoreAnswerMultipleChoice(UUID questionId){
        User user = userService.currentUser();

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        int score=0;

        List<Answer> answers=answerRepository.findByQuestion(question);

        int answerCount=answers.stream()
                .filter(Answer::isCorrectAnswer)
                .toList().size();

        UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
        for (Answer answer:userAnswer.getAnswers()){
            if(!answer.isCorrectAnswer())
                return 0;
            score+=question.getQuestionScore()/answerCount;
        }

        return score;



    }

    public int scoreAnswerMatching(UUID questionId){

        User user = userService.currentUser();

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        int score=0;
        List<UserAnswerMatching> userAnswerMatchings= userAnswerMatchingRepository.findAllByUserAndQuestion(user,question);


        List<AnswerMatching> answerMatchings= answerMatchingRepository.findAllByQuestion(question);
        Map<String,String> map=new HashMap<>();

        for(AnswerMatching matching:answerMatchings){
            map.put(matching.getContentLeft(),matching.getContentRight());
        }

        for(UserAnswerMatching matching:userAnswerMatchings){
            String contentRight= map.get(matching.getContentLeft());
            if(contentRight.equalsIgnoreCase(matching.getContentRight())){
                score+=question.getQuestionScore()/answerMatchings.size();
            }
        }

        return score;

    }

    public Map<String,Integer> scoreAnswer(UUID questionId){
        User user = userService.currentUser();

        if (Objects.isNull(user))
            throw new ResourceNotFoundException("User not found");

        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        Map<String,Integer> score= new HashMap<>();
        if(Objects.equals(question.getQuestionType(), QuestionType.Fill_In_Blank)){
//            if(!checkCorrectAnswerBlank(questionId)){
//                score.put("scoreAnswer",0);
//            }
//            else{
//                List<UserBlankAnswer> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
//                List<AnswerBlank> answerBlanks= answerBlankRepository.findByQuestion(question);
//                score.put("scoreAnswer",(question.getQuestionScore()/answerBlanks.size())* answers.size());
//            }
            score.put("scoreAnswer",scoreAnswerBlank(questionId));
            return score;
        }
        else if(Objects.equals(question.getQuestionType(),QuestionType.Multiple_Choice)){
            score.put("scoreAnswer",scoreAnswerMultipleChoice(questionId));
//            if (checkCorrectAnswerMultipleChoice(questionId)){
//                UserAnswer userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
//                int n=question.getAnswers().size();
//                score.put("scoreAnswer",question.getQuestionScore()/n*userAnswer.getAnswers().size());
//            }
//            else{
//                score.put("scoreAnswer",0);
//            }
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
        else if(Objects.equals(question.getQuestionType(),QuestionType.Matching)){
            score.put("scoreAnswer", scoreAnswerMatching(questionId));
            return score;
        }
        score.put("scoreAnswer",0);
        return score;
    }


    public Map<String, String> createUserMatchingAnswer(AnswerMatchingRequest request) {
        User user=userService.currentUser();
        if(Objects.isNull(user)){
            throw new ResourceNotFoundException("User not found");
        }

        Question question = questionRepository.findByQuestionId(request.getQuestionId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Question not found")
                );

        Map<String,String> answerMap=new HashMap<>();

        UserAnswerMatching userAnswerMatching=new UserAnswerMatching();
        userAnswerMatching.setQuestion(question);
        userAnswerMatching.setUser(user);
        userAnswerMatching.setContentLeft(request.getContentLeft());
        userAnswerMatching.setContentRight(request.getContentRight());
        userAnswerMatchingRepository.save(userAnswerMatching);

        answerMap.put(userAnswerMatching.getContentLeft(),userAnswerMatching.getContentRight());
        return answerMap;

    }


}
