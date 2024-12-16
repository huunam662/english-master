package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity;
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
public class DetailMockTestMapperImpl implements DetailMockTestMapper {

    @Override
    public MockTestDetailResponse toDetailMockTestResponse(DetailMockTestEntity detailMockTestEntity) {
        if ( detailMockTestEntity == null ) {
            return null;
        }

        MockTestDetailResponse.MockTestDetailResponseBuilder mockTestDetailResponse = MockTestDetailResponse.builder();

        mockTestDetailResponse.answerId( detailMockTestEntityAnswerAnswerId( detailMockTestEntity ) );
        mockTestDetailResponse.answerContent( detailMockTestEntityAnswerAnswerContent( detailMockTestEntity ) );
        Boolean correctAnswer = detailMockTestEntityAnswerCorrectAnswer( detailMockTestEntity );
        if ( correctAnswer != null ) {
            mockTestDetailResponse.correctAnswer( correctAnswer );
        }
        else {
            mockTestDetailResponse.correctAnswer( false );
        }
        Integer questionScore = detailMockTestEntityAnswerQuestionQuestionScore( detailMockTestEntity );
        if ( questionScore != null ) {
            mockTestDetailResponse.scoreAnswer( questionScore );
        }
        else {
            mockTestDetailResponse.scoreAnswer( 0 );
        }
        mockTestDetailResponse.detailMockTestId( detailMockTestEntity.getDetailMockTestId() );

        return mockTestDetailResponse.build();
    }

    @Override
    public List<MockTestDetailResponse> toDetailMockTestResponseList(List<DetailMockTestEntity> detailMockTestList) {
        if ( detailMockTestList == null ) {
            return null;
        }

        List<MockTestDetailResponse> list = new ArrayList<MockTestDetailResponse>( detailMockTestList.size() );
        for ( DetailMockTestEntity detailMockTestEntity : detailMockTestList ) {
            list.add( toDetailMockTestResponse( detailMockTestEntity ) );
        }

        return list;
    }

    private UUID detailMockTestEntityAnswerAnswerId(DetailMockTestEntity detailMockTestEntity) {
        AnswerEntity answer = detailMockTestEntity.getAnswer();
        if ( answer == null ) {
            return null;
        }
        return answer.getAnswerId();
    }

    private String detailMockTestEntityAnswerAnswerContent(DetailMockTestEntity detailMockTestEntity) {
        AnswerEntity answer = detailMockTestEntity.getAnswer();
        if ( answer == null ) {
            return null;
        }
        return answer.getAnswerContent();
    }

    private Boolean detailMockTestEntityAnswerCorrectAnswer(DetailMockTestEntity detailMockTestEntity) {
        AnswerEntity answer = detailMockTestEntity.getAnswer();
        if ( answer == null ) {
            return null;
        }
        return answer.getCorrectAnswer();
    }

    private Integer detailMockTestEntityAnswerQuestionQuestionScore(DetailMockTestEntity detailMockTestEntity) {
        AnswerEntity answer = detailMockTestEntity.getAnswer();
        if ( answer == null ) {
            return null;
        }
        QuestionEntity question = answer.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getQuestionScore();
    }
}
