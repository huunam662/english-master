package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.domain.content.dto.response.ContentBasicResponse;
import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class ContentMapperImpl implements ContentMapper {

    @Override
    public ContentResponse toContentResponse(ContentEntity content) {
        if ( content == null ) {
            return null;
        }

        ContentResponse.ContentResponseBuilder contentResponse = ContentResponse.builder();

        contentResponse.questionId( contentQuestionQuestionId( content ) );
        contentResponse.contentId( content.getContentId() );
        contentResponse.topicId( content.getTopicId() );
        contentResponse.contentType( content.getContentType() );
        contentResponse.contentData( content.getContentData() );
        contentResponse.code( content.getCode() );
        contentResponse.userCreate( userEntityToUserBasicResponse( content.getUserCreate() ) );
        contentResponse.userUpdate( userEntityToUserBasicResponse( content.getUserUpdate() ) );

        return contentResponse.build();
    }

    @Override
    public List<ContentResponse> toContentResponseList(List<ContentEntity> content) {
        if ( content == null ) {
            return null;
        }

        List<ContentResponse> list = new ArrayList<ContentResponse>( content.size() );
        for ( ContentEntity contentEntity : content ) {
            list.add( toContentResponse( contentEntity ) );
        }

        return list;
    }

    @Override
    public ContentBasicResponse toContentBasicResponse(ContentEntity content) {
        if ( content == null ) {
            return null;
        }

        ContentBasicResponse.ContentBasicResponseBuilder contentBasicResponse = ContentBasicResponse.builder();

        contentBasicResponse.contentType( content.getContentType() );
        contentBasicResponse.contentData( content.getContentData() );

        return contentBasicResponse.build();
    }

    @Override
    public List<ContentBasicResponse> toContentBasicResponseList(List<ContentEntity> contentEntityList) {
        if ( contentEntityList == null ) {
            return null;
        }

        List<ContentBasicResponse> list = new ArrayList<ContentBasicResponse>( contentEntityList.size() );
        for ( ContentEntity contentEntity : contentEntityList ) {
            list.add( toContentBasicResponse( contentEntity ) );
        }

        return list;
    }

    @Override
    public void flowToContentEntity(ContentRequest contentRequest, ContentEntity content) {
        if ( contentRequest == null ) {
            return;
        }

        content.setTopicId( contentRequest.getTopicId() );
        content.setCode( contentRequest.getCode() );
    }

    private UUID contentQuestionQuestionId(ContentEntity contentEntity) {
        QuestionEntity question = contentEntity.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getQuestionId();
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
