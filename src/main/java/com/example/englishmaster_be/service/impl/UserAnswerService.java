package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.model.request.Answer.AnswerBlankRequest;
import com.example.englishmaster_be.model.request.Answer.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.model.request.Answer.UserAnswerRequest;
import com.example.englishmaster_be.common.constaint.QuestionTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.model.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.model.response.ScoreAnswerResponse;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.IAnswerService;
import com.example.englishmaster_be.service.IQuestionService;
import com.example.englishmaster_be.service.IUserService;
import com.example.englishmaster_be.entity.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAnswerService {

    AnswerBlankRepository answerBlankRepository;

    UserBlankAnswerRepository userBlankAnswerRepository;

    UserAnswerMatchingRepository userAnswerMatchingRepository;

    AnswerMatchingRepository answerMatchingRepository;

    UserAnswerRepository userAnswerRepository;

    AnswerRepository answerRepository;

    IUserService userService;

    IQuestionService questionService;

    IAnswerService answerService;


    @Transactional
    public UserAnswerEntity saveUserAnswer(UserAnswerRequest userAnswerRequest){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(userAnswerRequest.getQuestionId());

        AnswerEntity answer = answerService.getAnswerById(userAnswerRequest.getAnswerId());

        UserAnswerEntity userAns = userAnswerRepository.findByUserAndQuestion(user, question);

        if(Objects.isNull(userAns)){

            userAns = UserAnswerEntity.builder()
                    .user(user)
                    .question(question)
                    .answers(Collections.singletonList(answer))
                    .numberChoice(1)
                    .build();

            return userAnswerRepository.save(userAns);
        }

        int numberChoice = userAns.getNumberChoice();

        if(question.getNumberChoice() < numberChoice + 1)
            throw new BadRequestException("The number of choices must be less than or equal to " + question.getNumberChoice());

        if(userAns.getAnswers() == null)
            userAns.setAnswers(new ArrayList<>());

        userAns.getAnswers().add(answer);
        userAns.setNumberChoice(numberChoice + 1);

        return userAnswerRepository.save(userAns);
    }


    @Transactional
    public UserBlankAnswerEntity createUserBlankAnswer(AnswerBlankRequest request) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(request.getQuestionId());

        UserBlankAnswerEntity answer = new UserBlankAnswerEntity();
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setAnswer(request.getContent());
        answer.setPosition(request.getPosition());

        return userBlankAnswerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(UUID questionId){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(questionId);

        if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Fill_In_Blank)){
            List<UserBlankAnswerEntity> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
            for (UserBlankAnswerEntity answer : answers) {
                userBlankAnswerRepository.deleteById(answer.getId());
            }
        }
        else if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Multiple_Choice) || Objects.equals(question.getQuestionType(), QuestionTypeEnum.T_F_Not_Given)){
            UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
            userAnswerRepository.deleteById(userAnswer.getId());
        }
    }

    public boolean checkCorrectAnswerBlank(UUID questionId, UUID userId) {

        UserEntity user = userService.findUserById(userId);

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<UserBlankAnswerEntity> answers = userBlankAnswerRepository.getByUserAndQuestion(user,question);

        List<AnswerBlankEntity> answerBlanks = answerBlankRepository.findByQuestion(question);
        Map<Integer, String> answerMap=answerBlanks.stream()
                .collect(Collectors.toMap(AnswerBlankEntity::getPosition, AnswerBlankEntity::getAnswer));

        for (UserBlankAnswerEntity userBlankAnswer:answers){

            int pos=userBlankAnswer.getPosition();
            String answer=userBlankAnswer.getAnswer();

            if(!answer.equalsIgnoreCase(answerMap.get(pos)))
                return false;

        }

        return true;
    }

    public int scoreAnswerBlank(UUID questionId) {

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<UserBlankAnswerEntity> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);

        List<AnswerBlankEntity> answerBlanks= answerBlankRepository.findByQuestion(question);
        Map<Integer, String> answerMap=answerBlanks.stream()
                .collect(Collectors.toMap(AnswerBlankEntity::getPosition, AnswerBlankEntity::getAnswer));

        int score = 0;

        for (UserBlankAnswerEntity userBlankAnswer:answers){
            int pos=userBlankAnswer.getPosition();
            String answer=userBlankAnswer.getAnswer();

            if(answer.equalsIgnoreCase(answerMap.get(pos)))
                score += question.getQuestionScore() / answerBlanks.size();

        }

        return score;
    }

    public boolean checkCorrectAnswerMultipleChoice(UUID questionId, UUID userId){

        UserEntity user = userService.findUserById(userId);

        QuestionEntity question = questionService.getQuestionById(questionId);

        UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
        for (AnswerEntity answer : userAnswer.getAnswers())
            if(!answer.getCorrectAnswer())
                return false;


        return true;
    }

    public int scoreAnswerMultipleChoice(UUID questionId){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<AnswerEntity> answers = answerRepository.findByQuestion(question);

        int answerCount=answers.stream()
                .filter(AnswerEntity::getCorrectAnswer)
                .toList().size();

        int score = 0;

        UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
        for (AnswerEntity answer:userAnswer.getAnswers()){
            if(!answer.getCorrectAnswer())
                return 0;

            score += question.getQuestionScore() / answerCount;
        }

        return score;
    }

    public ScoreAnswerResponse scoreAnswerMatching(UUID questionId, UUID userId){

        UserEntity user = userService.findUserById(userId);

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<UserAnswerMatchingEntity> userAnswerMatchings = userAnswerMatchingRepository.findAllByUserAndQuestion(user, question);

        List<AnswerMatchingEntity> answerMatchings = answerMatchingRepository.findAllByQuestion(question);

        ScoreAnswerResponse scoreAnswerResponse = ScoreAnswerResponse.builder()
                .answers(AnswerMatchingMapper.INSTANCE.toAnswerMatchingBasicResponseList(answerMatchings))
                .build();

        for(UserAnswerMatchingEntity matching : userAnswerMatchings){

            scoreAnswerResponse.getAnswers().stream()
                    .map(AnswerMatchingBasicResponse::getContentRight)
                    .filter(contentRightItem -> contentRightItem.equalsIgnoreCase(matching.getContentRight()))
                    .findFirst().ifPresent(contentRight -> {
                        int scoreAnswer = scoreAnswerResponse.getScoreAnswer();
                        scoreAnswerResponse.setScoreAnswer(scoreAnswer + question.getQuestionScore() / answerMatchings.size());
                    });

        }

        return scoreAnswerResponse;
    }

    public int scoreAnswerMatching(UUID questionId){

        UserEntity user = userService.currentUser();

        if (Objects.isNull(user))
            throw new BadRequestException("UserEntity not found");

        QuestionEntity question = questionService.getQuestionById(questionId);

        int score=0;
        List<UserAnswerMatchingEntity> userAnswerMatchings= userAnswerMatchingRepository.findAllByUserAndQuestion(user,question);


        List<AnswerMatchingEntity> answerMatchings= answerMatchingRepository.findAllByQuestion(question);
        Map<String,String> map=new HashMap<>();

        for(AnswerMatchingEntity matching:answerMatchings){
            map.put(matching.getContentLeft(),matching.getContentRight());
        }

        for(UserAnswerMatchingEntity matching:userAnswerMatchings){
            String contentRight= map.get(matching.getContentLeft());
            if(contentRight.equalsIgnoreCase(matching.getContentRight())){
                score+=question.getQuestionScore()/answerMatchings.size();
            }
        }

        return score;

    }


    public ScoreAnswerResponse scoreAnswer(UUID questionId){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(questionId);

        ScoreAnswerResponse scoreAnswerResponse = new ScoreAnswerResponse();

        if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Fill_In_Blank) || Objects.equals(question.getQuestionType(), QuestionTypeEnum.Label)){
//            if(!checkCorrectAnswerBlank(questionId)){
//                score.put("scoreAnswer",0);
//            }
//            else{
//                List<UserBlankAnswerEntity> answers= userBlankAnswerRepository.getByUserAndQuestion(user,question);
//                List<AnswerBlankEntity> answerBlanks= answerBlankRepository.findByQuestion(question);
//                score.put("scoreAnswer",(question.getQuestionScore()/answerBlanks.size())* answers.size());
//            }
            scoreAnswerResponse.setScoreAnswer(scoreAnswerBlank(questionId));
            System.out.println(" diem "+scoreAnswerResponse.getScoreAnswer());
        }
        else if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Multiple_Choice)){
//            if (checkCorrectAnswerMultipleChoice(questionId)){
//                UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
//                int n=question.getAnswers().size();
//                score.put("scoreAnswer",question.getQuestionScore()/n*userAnswer.getAnswers().size());
//            }
//            else{
//                score.put("scoreAnswer",0);
//            }
            scoreAnswerResponse.setScoreAnswer(scoreAnswerMultipleChoice(questionId));
        }
        else if (Objects.equals(question.getQuestionType(), QuestionTypeEnum.T_F_Not_Given))
        {
            UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
            if(userAnswer.getAnswers().get(0).getCorrectAnswer())
                scoreAnswerResponse.setScoreAnswer(userAnswer.getQuestion().getQuestionScore());

        }
        else if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Matching))
            scoreAnswerResponse.setScoreAnswer(scoreAnswerMatching(questionId));

        return scoreAnswerResponse;
    }


    public UserAnswerMatchingEntity createUserMatchingAnswer(AnswerMatchingQuestionRequest request) {

        UserEntity user=userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(request.getQuestionId());

        UserAnswerMatchingEntity userAnswerMatching = UserAnswerMatchingEntity.builder()
                .question(question)
                .user(user)
                .contentLeft(request.getContentLeft())
                .contentRight(request.getContentRight())
                .build();

        return userAnswerMatchingRepository.save(userAnswerMatching);
    }


}
