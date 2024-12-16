package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerCorrectResponse;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class AnswerMapperImpl implements AnswerMapper {

    @Override
    public AnswerResponse toAnswerResponse(AnswerEntity answer) {
        if ( answer == null ) {
            return null;
        }

        AnswerResponse.AnswerResponseBuilder answerResponse = AnswerResponse.builder();

        answerResponse.questionId( answerQuestionQuestionId( answer ) );
        answerResponse.answerId( answer.getAnswerId() );
        answerResponse.answerContent( answer.getAnswerContent() );
        answerResponse.explainDetails( answer.getExplainDetails() );
        answerResponse.correctAnswer( answer.getCorrectAnswer() );
        answerResponse.createAt( answer.getCreateAt() );
        answerResponse.updateAt( answer.getUpdateAt() );

        return answerResponse.build();
    }

    @Override
    public List<AnswerResponse> toAnswerResponseList(List<AnswerEntity> answerList) {
        if ( answerList == null ) {
            return null;
        }

        List<AnswerResponse> list = new ArrayList<AnswerResponse>( answerList.size() );
        for ( AnswerEntity answerEntity : answerList ) {
            list.add( toAnswerResponse( answerEntity ) );
        }

        return list;
    }

    @Override
    public void flowToAnswerEntity(AnswerRequest answerRequest, AnswerEntity answer) {
        if ( answerRequest == null ) {
            return;
        }

        answer.setAnswerContent( answerRequest.getAnswerContent() );
        answer.setExplainDetails( answerRequest.getExplainDetails() );
        answer.setCorrectAnswer( answerRequest.getCorrectAnswer() );
    }

    @Override
    public void flowToAnswerEntity(AnswerBasicRequest answerBasicRequest, AnswerEntity answer) {
        if ( answerBasicRequest == null ) {
            return;
        }

        answer.setAnswerId( answerBasicRequest.getAnswerId() );
        answer.setAnswerContent( answerBasicRequest.getAnswerContent() );
        answer.setCorrectAnswer( answerBasicRequest.getCorrectAnswer() );
    }

    @Override
    public AnswerCorrectResponse toCheckCorrectAnswerResponse(AnswerEntity answer) {
        if ( answer == null ) {
            return null;
        }

        AnswerCorrectResponse.AnswerCorrectResponseBuilder answerCorrectResponse = AnswerCorrectResponse.builder();

        answerCorrectResponse.scoreAnswer( answerQuestionQuestionScore( answer ) );
        answerCorrectResponse.correctAnswer( answer.getCorrectAnswer() );

        return answerCorrectResponse.build();
    }

    private UUID answerQuestionQuestionId(AnswerEntity answerEntity) {
        QuestionEntity question = answerEntity.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getQuestionId();
    }

    private Integer answerQuestionQuestionScore(AnswerEntity answerEntity) {
        QuestionEntity question = answerEntity.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getQuestionScore();
    }
}
