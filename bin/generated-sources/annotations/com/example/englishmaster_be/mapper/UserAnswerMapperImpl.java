package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerResponse;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user_answer.UserAnswerEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class UserAnswerMapperImpl implements UserAnswerMapper {

    @Override
    public UserAnswerResponse toUserAnswerResponse(UserAnswerEntity userAnswerEntity) {
        if ( userAnswerEntity == null ) {
            return null;
        }

        UserAnswerResponse.UserAnswerResponseBuilder userAnswerResponse = UserAnswerResponse.builder();

        userAnswerResponse.id( userAnswerEntity.getId() );
        userAnswerResponse.content( userAnswerEntity.getContent() );
        userAnswerResponse.numberChoice( userAnswerEntity.getNumberChoice() );
        userAnswerResponse.user( userEntityToUserBasicResponse( userAnswerEntity.getUser() ) );

        userAnswerResponse.question( QuestionMapper.INSTANCE.toQuestionBasicResponse(userAnswerEntity.getQuestion()) );
        userAnswerResponse.answers( AnswerMapper.INSTANCE.toAnswerResponseList(userAnswerEntity.getAnswers()) );

        return userAnswerResponse.build();
    }

    protected UserBasicResponse userEntityToUserBasicResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserBasicResponse.UserBasicResponseBuilder userBasicResponse = UserBasicResponse.builder();

        userBasicResponse.userId( userEntity.getUserId() );
        userBasicResponse.name( userEntity.getName() );

        return userBasicResponse.build();
    }
}
