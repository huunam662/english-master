package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.SaveAnswerDTO;
import com.example.englishmaster_be.DTO.Answer.UpdateAnswerDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.AnswerResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IAnswerService;
import com.example.englishmaster_be.Service.IQuestionService;
import com.example.englishmaster_be.Service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.englishmaster_be.Exception.Error;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerServiceImpl implements IAnswerService {

    AnswerRepository answerRepository;

    DetailMockTestRepository detailMockTestRepository;

    IUserService userService;

    IQuestionService questionService;


    @Transactional
    @Override
    public Answer saveAnswer(SaveAnswerDTO createAnswerDTO) {

        User user = userService.currentUser();

        Question question = questionService.getQuestionById(createAnswerDTO.getQuestionId());

        boolean check = false;

        for (Answer answerCheck : question.getAnswers()) {
            if(createAnswerDTO instanceof UpdateAnswerDTO updateAnswerDTO) {
                if (
                        answerCheck.isCorrectAnswer()
                        && updateAnswerDTO.isCorrectAnswer()
                        && !answerCheck.getAnswerId().equals(updateAnswerDTO.getAnswerId())
                ) {
                    check = true;
                    break;
                }
            }
            else if (answerCheck.isCorrectAnswer() && createAnswerDTO.isCorrectAnswer()){
                check = true;
                break;
            }
        }

        if (check) throw new BadRequestException("Had correct Answer");

        Answer answer;

        if(createAnswerDTO instanceof UpdateAnswerDTO updateAnswerDTO){
            answer = findAnswerToId(updateAnswerDTO.getAnswerId());
            answer.setAnswerContent(updateAnswerDTO.getContentAnswer());
            answer.setCorrectAnswer(updateAnswerDTO.isCorrectAnswer());
            answer.setExplainDetails(updateAnswerDTO.getExplainDetails());
            answer.setQuestion(question);
            answer.setUpdateAt(LocalDateTime.now());
            answer.setUserUpdate(user);
        }
        else{
            answer = new Answer(createAnswerDTO);
            answer.setQuestion(question);
            answer.setUserCreate(user);
            answer.setUserUpdate(user);
        }

        answerRepository.save(answer);

        return answer;

    }

    @Override
    public boolean existQuestion(Answer answer, Question question) {
        return answer.getQuestion().equals(question);
    }

    @Override
    public Answer findAnswerToId(UUID answerID) {

        return answerRepository.findByAnswerId(answerID)
                .orElseThrow(() -> new CustomException(Error.ANSWER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void deleteAnswer(UUID answerId) {
        try {

            Answer answer = findAnswerToId(answerId);

            answerRepository.delete(answer);

        } catch (BadRequestException e) {
            throw new BadRequestException("Delete Answer fail: " + e.getMessage());
        }
    }

    @Override
    public Answer correctAnswer(Question question) {
        return answerRepository.findByQuestionAndCorrectAnswer(question, true).orElseThrow(() -> new CustomException(Error.ANSWER_BY_CORRECT_QUESTION_NOT_FOUND));
    }

    @Override
    public Answer choiceAnswer(Question question, MockTest mockTest) {
        for (DetailMockTest detailMockTest : detailMockTestRepository.findAllByMockTest(mockTest)) {
            if (detailMockTest.getAnswer().getQuestion().equals(question)) {
                return detailMockTest.getAnswer();
            }
        }
        return null;
    }

    @Override
    public List<AnswerResponse> getListAnswerByQuestionId(UUID questionId) {

        Question question = questionService.getQuestionById(questionId);

        List<Answer> answerList = question.getAnswers();

        return answerList.stream().map(AnswerResponse::new).toList();
    }
}
