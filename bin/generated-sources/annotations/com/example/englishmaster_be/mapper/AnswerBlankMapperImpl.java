package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer_blank.dto.response.AnswerBlankResponse;
import com.example.englishmaster_be.model.answer_blank.AnswerBlankEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class AnswerBlankMapperImpl implements AnswerBlankMapper {

    @Override
    public AnswerBlankResponse toAnswerBlankResponseWithExcludeQuestion(AnswerBlankEntity answerBlank) {
        if ( answerBlank == null ) {
            return null;
        }

        AnswerBlankResponse.AnswerBlankResponseBuilder answerBlankResponse = AnswerBlankResponse.builder();

        answerBlankResponse.id( answerBlank.getId() );
        answerBlankResponse.position( answerBlank.getPosition() );
        answerBlankResponse.answer( answerBlank.getAnswer() );

        return answerBlankResponse.build();
    }

    @Override
    public AnswerBlankResponse toAnswerBlankResponse(AnswerBlankEntity answerBlank) {
        if ( answerBlank == null ) {
            return null;
        }

        AnswerBlankResponse.AnswerBlankResponseBuilder answerBlankResponse = AnswerBlankResponse.builder();

        answerBlankResponse.id( answerBlank.getId() );
        answerBlankResponse.position( answerBlank.getPosition() );
        answerBlankResponse.answer( answerBlank.getAnswer() );

        answerBlankResponse.question( QuestionMapper.INSTANCE.toQuestionResponse(answerBlank.getQuestion()) );

        return answerBlankResponse.build();
    }
}
