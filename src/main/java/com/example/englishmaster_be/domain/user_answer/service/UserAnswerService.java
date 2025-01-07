package com.example.englishmaster_be.domain.user_answer.service;

import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingRequest;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingQuestionRequest;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest1;
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
    public void createUserAnswer(List<UserAnswerRequest> userAnswerRequests) {
        UserEntity user = userService.currentUser();



        for(UserAnswerRequest request : userAnswerRequests) {
            QuestionEntity question = questionService.getQuestionById(request.getQuestionId());

            if( request.getType()==QuestionTypeEnum.Multiple_Choice || request.getType()==QuestionTypeEnum.T_F_Not_Given){
                if( request.getAnswer().size() > question.getNumberChoice() ) {
                    throw new BadRequestException("The number of choices must be less than or equal to " + question.getNumberChoice());
                }
                else{
                    UserAnswerEntity userAns = UserAnswerEntity.builder()
                            .user(user)
                            .question(question)
                            .answers(new ArrayList<>())
                            .build();

                    for(int i = 0; i< request.getAnswer().size(); i++){
                        AnswerEntity answerEntity = answerRepository.findByQuestionAndAnswerContent(question, request.getAnswer().get(i));

                        if(Objects.isNull(answerEntity)){
                            throw new BadRequestException("The answer is not exist");
                        }
                        userAns.getAnswers().add(answerEntity);

                    }
                    userAnswerRepository.save(userAns);
                }
            }
            else if( request.getType()==QuestionTypeEnum.Fill_In_Blank || request.getType()==QuestionTypeEnum.Label){

                for(AnswerBlankRequest answerBlankRequest: request.getAnswersBlank()){
                    UserBlankAnswerEntity answer = new UserBlankAnswerEntity();
                    answer.setUser(user);
                    answer.setQuestion(question);
                    answer.setAnswer(answerBlankRequest.getContent());
                    answer.setPosition(answerBlankRequest.getPosition());
                    userBlankAnswerRepository.save(answer);
                }


            } else if ( request.getType()==QuestionTypeEnum.Matching) {

                for(AnswerMatchingRequest answerMatchingRequest: request.getAnswersMatching()){
                    UserAnswerMatchingEntity userAnswerMatching = UserAnswerMatchingEntity.builder()
                            .question(question)
                            .user(user)
                            .contentLeft(answerMatchingRequest.getContentLeft())
                            .contentRight(answerMatchingRequest.getContentRight())
                            .build();

                    userAnswerMatchingRepository.save(userAnswerMatching);
                }

            }
        }


    }


    @Transactional
    @Override
    public UserAnswerEntity saveUserAnswer(UserAnswerRequest1 userAnswerRequest1){

        UserEntity user = userService.currentUser();

        QuestionEntity question = questionService.getQuestionById(userAnswerRequest1.getQuestionId());

        if(userAnswerRequest1.getAnswer().size() > question.getNumberChoice()){
            throw new BadRequestException("The number of choices must be less than or equal to " + question.getNumberChoice());
        }


        UserAnswerEntity userAns = UserAnswerEntity.builder()
                .user(user)
                .question(question)
                .answers(new ArrayList<>())
                .build();

        for(int i = 0; i< userAnswerRequest1.getAnswer().size(); i++){
            AnswerEntity answerEntity = answerRepository.findByQuestionAndAnswerContent(question, userAnswerRequest1.getAnswer().get(i));

            if(Objects.isNull(answerEntity)){
                throw new BadRequestException("The answer is not exist");
            }
            userAns.getAnswers().add(answerEntity);

        }

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

        UserEntity user = userService.getUserById(userId);

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

        UserEntity user = userService.getUserById(userId);

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

        UserEntity user = userService.getUserById(userId);

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
            scoreAnswerResponse.setScoreAnswer(scoreAnswerBlank(questionId));
        }
        else if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Multiple_Choice)){
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
