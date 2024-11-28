package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.DTO.Answer.CreateAnswerDTO;
import com.example.englishmaster_be.DTO.Answer.UpdateAnswerDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IAnswerService;
import com.example.englishmaster_be.Service.IQuestionService;
import com.example.englishmaster_be.Service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.englishmaster_be.Exception.Error;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerServiceImpl implements IAnswerService {

    AnswerRepository answerRepository;

    DetailMockTestRepository detailMockTestRepository;

    IUserService userService;

    IQuestionService questionService;

    IAnswerService answerService;

    @Transactional
    @Override
    public Answer saveAnswer(CreateAnswerDTO createAnswerDTO) {

        try {

            User user = userService.currentUser();

            Question question = questionService.findQuestionById(createAnswerDTO.getQuestionId());

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

            if (!check){
                MessageResponseHolder.setMessage("Had correct Answer");
                return null;
            }

            Answer answer;

            if(createAnswerDTO instanceof UpdateAnswerDTO updateAnswerDTO){
                answer = answerService.findAnswerToId(updateAnswerDTO.getAnswerId());
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

            MessageResponseHolder.setMessage("Save answer successfully");

            return answer;

        } catch (BadRequestException e) {

            throw new BadRequestException("Save answer fail: " + e.getMessage());
        }
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

    @Override
    public void deleteAnswer(UUID answerId) {
        try {

            Answer answer = answerService.findAnswerToId(answerId);

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

}
