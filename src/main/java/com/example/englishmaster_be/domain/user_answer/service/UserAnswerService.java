package com.example.englishmaster_be.domain.user_answer.service;

import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerScoreResponse;
import com.example.englishmaster_be.model.answer_blank.AnswerBlankEntity;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.answer_blank.AnswerBlankRepository;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingRepository;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user_answer.UserAnswerEntity;
import com.example.englishmaster_be.model.user_answer.UserAnswerRepository;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingEntity;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingRepository;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerEntity;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerRepository;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.user.service.IUserService;
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
public class UserAnswerService implements IUserAnswerService {

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
    @Override
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
    @Override
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
    @Override
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

    @Override
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

    @Override
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

    @Override
    public boolean checkCorrectAnswerMultipleChoice(UUID questionId, UUID userId){

        UserEntity user = userService.findUserById(userId);

        QuestionEntity question = questionService.getQuestionById(questionId);

        UserAnswerEntity userAnswer=userAnswerRepository.findByUserAndQuestion(user,question);
        for (AnswerEntity answer : userAnswer.getAnswers())
            if(!answer.getCorrectAnswer())
                return false;


        return true;
    }

    @Override
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

    @Override
    public UserAnswerScoreResponse scoreAnswerMatching(UUID questionId, UUID userId){

        UserEntity user = userService.findUserById(userId);

        QuestionEntity question = questionService.getQuestionById(questionId);

        List<UserAnswerMatchingEntity> userAnswerMatchings = userAnswerMatchingRepository.findAllByUserAndQuestion(user, question);

        List<AnswerMatchingEntity> answerMatchings = answerMatchingRepository.findAllByQuestion(question);

        UserAnswerScoreResponse scoreAnswerResponse = UserAnswerScoreResponse.builder()
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

    @Override
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

    @Override
    public UserAnswerScoreResponse scoreAnswer(UUID questionId){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(questionId);

        UserAnswerScoreResponse scoreAnswerResponse = new UserAnswerScoreResponse();

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

    @Override
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
