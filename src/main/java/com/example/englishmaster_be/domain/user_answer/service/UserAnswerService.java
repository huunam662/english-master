package com.example.englishmaster_be.domain.user_answer.service;

import com.example.englishmaster_be.domain.answer_blank.dto.response.AnswerBlankResponse;
import com.example.englishmaster_be.domain.answer_matching.dto.request.AnswerMatchingRequest;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingResponse;
import com.example.englishmaster_be.domain.user_answer.dto.request.UserAnswerRequest;
import com.example.englishmaster_be.domain.user_answer.dto.response.*;
import com.example.englishmaster_be.mapper.AnswerMatchingMapper;
import com.example.englishmaster_be.domain.answer_blank.dto.request.AnswerBlankRequest;
import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
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
    public UserAnswerResponse createUserAnswer(List<UserAnswerRequest> userAnswerRequests) {
        UserEntity user = userService.currentUser();

        UserAnswerResponse userAnswerResponse = new UserAnswerResponse();
        int score = 0;

        List<AnswerDetailsResponse> answerDetailsRespons =new ArrayList<>();

        for(UserAnswerRequest request : userAnswerRequests) {

            AnswerDetailsResponse answerDetailsResponse =new AnswerDetailsResponse();


            QuestionEntity question = questionService.getQuestionById(request.getQuestionId());

            answerDetailsResponse.setId(question.getQuestionId());
            answerDetailsResponse.setQuestionContent(question.getQuestionContent());
            answerDetailsResponse.setType(question.getQuestionType().toString());

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


                    UserAnswerMultipleChoiceResponse userAnswerMultipleChoiceResponse = new UserAnswerMultipleChoiceResponse();

                    boolean isCorrect = checkCorrectAnswerMultipleChoice(question.getQuestionId(),user.getUserId());
                    userAnswerMultipleChoiceResponse.setCorrect(isCorrect);
                    userAnswerMultipleChoiceResponse.setValues(
                            userAns.getAnswers().stream().map(AnswerEntity::getAnswerContent).collect(Collectors.toList())
                    );

                    if(isCorrect){
                        score += question.getQuestionScore();
                        userAnswerMultipleChoiceResponse.setExpected(userAns.getAnswers().stream().map(AnswerEntity::getAnswerContent).collect(Collectors.toList()));
                    }
                    else{
                        userAnswerMultipleChoiceResponse.setExpected(
                                answerRepository.findByQuestion(question)
                                .stream().map(AnswerEntity::getAnswerContent).collect(Collectors.toList())
                        );
                    }
                    answerDetailsResponse.setAnswers(userAnswerMultipleChoiceResponse);

                    answerDetailsRespons.add(answerDetailsResponse);

                }
            }
            else if( request.getType()==QuestionTypeEnum.Fill_In_Blank || request.getType()==QuestionTypeEnum.Label){
                UserAnswerBlankResponse userAnswerBlankResponse= new UserAnswerBlankResponse();
                List<AnswerBlankResponse> values=new ArrayList<>();
                List<AnswerBlankResponse> expected=new ArrayList<>();

                for(AnswerBlankRequest answerBlankRequest: request.getAnswersBlank()){
                    UserBlankAnswerEntity answer = new UserBlankAnswerEntity();
                    answer.setUser(user);
                    answer.setQuestion(question);
                    answer.setAnswer(answerBlankRequest.getContent());
                    answer.setPosition(answerBlankRequest.getPosition());
                    userBlankAnswerRepository.save(answer);

                    AnswerBlankResponse answerBlankResponse = new AnswerBlankResponse();
                    answerBlankResponse.setAnswer(answerBlankRequest.getContent());
                    answerBlankResponse.setPosition(answerBlankRequest.getPosition());

                    values.add(answerBlankResponse);

                }
                List<AnswerBlankEntity> answerBlankEntities=answerBlankRepository.findByQuestion(question);

                answerBlankEntities.forEach(answerEntity -> {
                    AnswerBlankResponse answerBlankResponse1 = new AnswerBlankResponse();
                    answerBlankResponse1.setAnswer(answerEntity.getAnswer());
                    answerBlankResponse1.setPosition(answerEntity.getPosition());
                    expected.add(answerBlankResponse1);
                });

                userAnswerBlankResponse.setValues(values);
                userAnswerBlankResponse.setExpected(expected);

                answerDetailsResponse.setAnswers(userAnswerBlankResponse);
                answerDetailsRespons.add(answerDetailsResponse);

                score += scoreAnswerBlank(question.getQuestionId());




            } else if ( request.getType()==QuestionTypeEnum.Words_Matching) {
                UserAnswerMatchingResponse userAnswerMatchingResponse = new UserAnswerMatchingResponse();
                List<AnswerMatchingResponse> values = new ArrayList<>();
                List<AnswerMatchingResponse> expected = new ArrayList<>();
                for(AnswerMatchingRequest answerMatchingRequest: request.getAnswersMatching()){
                    UserAnswerMatchingEntity userAnswerMatching = UserAnswerMatchingEntity.builder()
                            .question(question)
                            .user(user)
                            .contentLeft(answerMatchingRequest.getContentLeft())
                            .contentRight(answerMatchingRequest.getContentRight())
                            .build();

                    userAnswerMatchingRepository.save(userAnswerMatching);

                    AnswerMatchingResponse answerMatchingResponse = new AnswerMatchingResponse();
                    answerMatchingResponse.setContentLeft(answerMatchingRequest.getContentLeft());
                    answerMatchingResponse.setContentRight(answerMatchingRequest.getContentRight());
                    values.add(answerMatchingResponse);

                }

                List<AnswerMatchingEntity> answerMatchingEntities=answerMatchingRepository.findAllByQuestion(question);
                answerMatchingEntities.forEach(matching -> {
                    AnswerMatchingResponse answerMatchingResponse1 = new AnswerMatchingResponse();
                    if(matching.getContentLeft().isEmpty() || matching.getContentRight().isEmpty()){
                        answerMatchingResponse1.setContentLeft(matching.getContentLeft());
                        answerMatchingResponse1.setContentRight(matching.getContentRight());

                        expected.add(answerMatchingResponse1);
                    }
                });
                userAnswerMatchingResponse.setValues(values);
                userAnswerMatchingResponse.setExpected(expected);

                answerDetailsResponse.setAnswers(userAnswerMatchingResponse);

                answerDetailsRespons.add(answerDetailsResponse);

                score+= scoreAnswerMatching(question.getQuestionId());


            }
        }

        userAnswerResponse.setResult(answerDetailsRespons);

        userAnswerResponse.setScore(score);
        return userAnswerResponse;

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

        List<AnswerEntity> answerEntities=answerRepository.findByQuestion(question);
        if(userAnswer.getAnswers().size()<answerEntities.size()){
            return false;
        }

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

        List<AnswerMatchingEntity> answerMatchings = answerMatchingRepository.findAllByQuestion(question)
                .stream()
                .filter(matching->
                        !matching.getContentLeft().isEmpty() || !matching.getContentRight().isEmpty()
                )
                .collect(Collectors.toList());

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
        else if(Objects.equals(question.getQuestionType(), QuestionTypeEnum.Words_Matching))
            scoreAnswerResponse.setScoreAnswer(scoreAnswerMatching(questionId));

        return scoreAnswerResponse;
    }



}
