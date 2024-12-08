package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Model.AnswerMatching;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.AnswerMatchingRepository;
import com.example.englishmaster_be.Service.IAnswerMatching;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerMatchingServiceImpl implements IAnswerMatching {
    private final AnswerMatchingRepository answerMatchingRepository;
    private final UserServiceImpl userService;
    private final QuestionServiceImpl questionService;

    @Override
    public AnswerMatching createAnswerMatching(AnswerMatchingRequest request) {
        User user=userService.currentUser();

        if(Objects.isNull(user)){
            throw new ResourceNotFoundException("User not found");
        }

        Question question= questionService.getQuestionById(request.getQuestionId());

        if(Objects.isNull(question)){
            throw new ResourceNotFoundException("Question not found");
        }

        AnswerMatching answerMatching=new AnswerMatching();
        answerMatching.setQuestion(question);
        answerMatching.setContentLeft(request.getContentLeft());
        answerMatching.setContentRight(request.getContentRight());
        return answerMatchingRepository.save(answerMatching);
    }

    @Override
    public Map<String,String> getListAnswerMatching(UUID questionId) {
        Question question=questionService.getQuestionById(questionId);

        if(Objects.isNull(question)){
            throw new ResourceNotFoundException("Question not found");
        }
        List<AnswerMatching> answerMatchings= answerMatchingRepository.findAllByQuestion(question);
        List<String> contentLeft= new ArrayList<>(answerMatchings.stream().map(AnswerMatching::getContentLeft).toList());
        List<String> contentRight= new ArrayList<>(answerMatchings.stream().map(AnswerMatching::getContentRight).toList());

        Collections.shuffle(contentLeft);
        Collections.shuffle(contentRight);
        Map<String,String> map=new HashMap<>();
        for(int i=0; i<contentLeft.size(); i++){
            map.put(contentLeft.get(i),contentRight.get(i));
        }
        return map;

    }
}
