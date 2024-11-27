package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.Response.ResponseNotFoundException;
import com.example.englishmaster_be.Model.AnswerBlank;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Repository.AnswerBlankRepository;
import com.example.englishmaster_be.Repository.QuestionRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerBlankService {
    private final AnswerBlankRepository repository;
    private final QuestionRepository questionRepository;

    public Object getAnswerWithQuestionBlank(UUID questionId){
        Question question=questionRepository.findByQuestionId(questionId)
                .orElseThrow(
                        ()-> new ResponseNotFoundException("Not found question with"+ questionId)
                );

        List<AnswerBlank> questionBlanks=repository.findByQuestion(question);

        JSONArray jsonArray=new JSONArray();
        for (AnswerBlank questionBlank:questionBlanks){
            Map<String,Object> map=new HashMap<>();
            map.put("position",questionBlank.getPosition());
            map.put("answer",questionBlank.getAnswer());
            jsonArray.add(map);
        }
        return jsonArray;

    }
}
