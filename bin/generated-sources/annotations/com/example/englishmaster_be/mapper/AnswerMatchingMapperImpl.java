package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingResponse;
import com.example.englishmaster_be.model.answer_matching.AnswerMatchingEntity;
import com.example.englishmaster_be.model.user_answer_matching.UserAnswerMatchingEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:23+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class AnswerMatchingMapperImpl implements AnswerMatchingMapper {

    @Override
    public AnswerMatchingResponse toAnswerMatchingResponse(AnswerMatchingEntity answerMatching) {
        if ( answerMatching == null ) {
            return null;
        }

        AnswerMatchingResponse.AnswerMatchingResponseBuilder answerMatchingResponse = AnswerMatchingResponse.builder();

        answerMatchingResponse.id( answerMatching.getId() );
        answerMatchingResponse.contentLeft( answerMatching.getContentLeft() );
        answerMatchingResponse.contentRight( answerMatching.getContentRight() );

        answerMatchingResponse.question( QuestionMapper.INSTANCE.toQuestionResponse(answerMatching.getQuestion()) );

        return answerMatchingResponse.build();
    }

    @Override
    public AnswerMatchingBasicResponse toAnswerMatchingBasicResponse(AnswerMatchingEntity answerMatchingEntity) {
        if ( answerMatchingEntity == null ) {
            return null;
        }

        AnswerMatchingBasicResponse.AnswerMatchingBasicResponseBuilder answerMatchingBasicResponse = AnswerMatchingBasicResponse.builder();

        answerMatchingBasicResponse.contentLeft( answerMatchingEntity.getContentLeft() );
        answerMatchingBasicResponse.contentRight( answerMatchingEntity.getContentRight() );

        return answerMatchingBasicResponse.build();
    }

    @Override
    public AnswerMatchingBasicResponse toAnswerMatchingBasicResponse(UserAnswerMatchingEntity userAnswerMatchingEntity) {
        if ( userAnswerMatchingEntity == null ) {
            return null;
        }

        AnswerMatchingBasicResponse.AnswerMatchingBasicResponseBuilder answerMatchingBasicResponse = AnswerMatchingBasicResponse.builder();

        answerMatchingBasicResponse.contentLeft( userAnswerMatchingEntity.getContentLeft() );
        answerMatchingBasicResponse.contentRight( userAnswerMatchingEntity.getContentRight() );

        return answerMatchingBasicResponse.build();
    }

    @Override
    public List<AnswerMatchingBasicResponse> toAnswerMatchingBasicResponseList(List<AnswerMatchingEntity> answerMatchingEntityList) {
        if ( answerMatchingEntityList == null ) {
            return null;
        }

        List<AnswerMatchingBasicResponse> list = new ArrayList<AnswerMatchingBasicResponse>( answerMatchingEntityList.size() );
        for ( AnswerMatchingEntity answerMatchingEntity : answerMatchingEntityList ) {
            list.add( toAnswerMatchingBasicResponse( answerMatchingEntity ) );
        }

        return list;
    }
}
