package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerBlankResponse;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class UserBlankAnswerMapperImpl implements UserBlankAnswerMapper {

    @Override
    public UserAnswerBlankResponse toUserBlankAnswerResponse(UserBlankAnswerEntity userBlankAnswerEntity) {
        if ( userBlankAnswerEntity == null ) {
            return null;
        }

        UserAnswerBlankResponse.UserAnswerBlankResponseBuilder userAnswerBlankResponse = UserAnswerBlankResponse.builder();

        userAnswerBlankResponse.id( userBlankAnswerEntity.getId() );
        userAnswerBlankResponse.answer( userBlankAnswerEntity.getAnswer() );
        userAnswerBlankResponse.position( userBlankAnswerEntity.getPosition() );
        userAnswerBlankResponse.user( userEntityToUserBasicResponse( userBlankAnswerEntity.getUser() ) );

        userAnswerBlankResponse.question( QuestionMapper.INSTANCE.toQuestionBasicResponse(userBlankAnswerEntity.getQuestion()) );

        return userAnswerBlankResponse.build();
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
