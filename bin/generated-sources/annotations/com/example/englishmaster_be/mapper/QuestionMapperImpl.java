package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:23+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionEntity toQuestionEntity(QuestionRequest questionDto) {
        if ( questionDto == null ) {
            return null;
        }

        QuestionEntity.QuestionEntityBuilder questionEntity = QuestionEntity.builder();

        questionEntity.questionId( questionDto.getQuestionId() );
        questionEntity.questionContent( questionDto.getQuestionContent() );
        questionEntity.questionScore( questionDto.getQuestionScore() );
        questionEntity.questionExplainEn( questionDto.getQuestionExplainEn() );
        questionEntity.questionExplainVn( questionDto.getQuestionExplainVn() );
        questionEntity.questionType( questionDto.getQuestionType() );
        questionEntity.numberChoice( questionDto.getNumberChoice() );
        questionEntity.hasHints( questionDto.isHasHints() );

        return questionEntity.build();
    }

    @Override
    public QuestionEntity toQuestionEntity(QuestionGroupRequest saveGroupQuestionDTO) {
        if ( saveGroupQuestionDTO == null ) {
            return null;
        }

        QuestionEntity.QuestionEntityBuilder questionEntity = QuestionEntity.builder();

        return questionEntity.build();
    }

    @Override
    public QuestionResponse toQuestionResponse(QuestionEntity question) {
        if ( question == null ) {
            return null;
        }

        QuestionResponse.QuestionResponseBuilder<?, ?> questionResponse = QuestionResponse.builder();

        questionResponse.partId( questionPartPartId( question ) );
        questionResponse.contentList( contentEntityListToContentResponseList( question.getContentCollection() ) );
        questionResponse.questionId( question.getQuestionId() );
        questionResponse.questionContent( question.getQuestionContent() );
        questionResponse.questionScore( question.getQuestionScore() );
        questionResponse.questionType( question.getQuestionType() );
        questionResponse.createAt( question.getCreateAt() );
        questionResponse.updateAt( question.getUpdateAt() );

        questionResponse.listAnswer( AnswerMapper.INSTANCE.toAnswerResponseList(question.getAnswers()) );
        questionResponse.questionGroupParent( toQuestionBasicResponse(question.getQuestionGroupParent()) );
        questionResponse.questionGroupChildren( toQuestionBasicResponseList(question.getQuestionGroupChildren()) );

        return questionResponse.build();
    }

    @Override
    public List<QuestionResponse> toQuestionGroupChildrenResponseList(List<QuestionEntity> questionGroupChildren) {
        if ( questionGroupChildren == null ) {
            return null;
        }

        List<QuestionResponse> list = new ArrayList<QuestionResponse>( questionGroupChildren.size() );
        for ( QuestionEntity questionEntity : questionGroupChildren ) {
            list.add( toQuestionResponse( questionEntity ) );
        }

        return list;
    }

    @Override
    public QuestionBasicResponse toQuestionBasicResponse(QuestionEntity question) {
        if ( question == null ) {
            return null;
        }

        QuestionBasicResponse.QuestionBasicResponseBuilder<?, ?> questionBasicResponse = QuestionBasicResponse.builder();

        questionBasicResponse.partId( questionPartPartId( question ) );
        questionBasicResponse.questionId( question.getQuestionId() );
        questionBasicResponse.questionContent( question.getQuestionContent() );
        questionBasicResponse.questionScore( question.getQuestionScore() );
        questionBasicResponse.questionType( question.getQuestionType() );
        questionBasicResponse.createAt( question.getCreateAt() );
        questionBasicResponse.updateAt( question.getUpdateAt() );

        return questionBasicResponse.build();
    }

    @Override
    public List<QuestionBasicResponse> toQuestionBasicResponseList(List<QuestionEntity> questionList) {
        if ( questionList == null ) {
            return null;
        }

        List<QuestionBasicResponse> list = new ArrayList<QuestionBasicResponse>( questionList.size() );
        for ( QuestionEntity questionEntity : questionList ) {
            list.add( toQuestionBasicResponse( questionEntity ) );
        }

        return list;
    }

    @Override
    public void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, QuestionEntity questionEntity) {
        if ( questionByExcelFileResponse == null ) {
            return;
        }

        questionEntity.setQuestionContent( questionByExcelFileResponse.getQuestionContent() );
        questionEntity.setQuestionScore( questionByExcelFileResponse.getQuestionScore() );
        questionEntity.setQuestionExplainEn( questionByExcelFileResponse.getQuestionExplainEn() );
        questionEntity.setQuestionExplainVn( questionByExcelFileResponse.getQuestionExplainVn() );
    }

    @Override
    public void flowToQuestionEntity(QuestionRequest questionRequest, QuestionEntity questionEntity) {
        if ( questionRequest == null ) {
            return;
        }

        questionEntity.setQuestionContent( questionRequest.getQuestionContent() );
        questionEntity.setQuestionScore( questionRequest.getQuestionScore() );
        questionEntity.setQuestionExplainEn( questionRequest.getQuestionExplainEn() );
        questionEntity.setQuestionExplainVn( questionRequest.getQuestionExplainVn() );
        questionEntity.setQuestionType( questionRequest.getQuestionType() );
        questionEntity.setNumberChoice( questionRequest.getNumberChoice() );
        questionEntity.setHasHints( questionRequest.isHasHints() );
    }

    private UUID questionPartPartId(QuestionEntity questionEntity) {
        PartEntity part = questionEntity.getPart();
        if ( part == null ) {
            return null;
        }
        return part.getPartId();
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

    protected ContentResponse contentEntityToContentResponse(ContentEntity contentEntity) {
        if ( contentEntity == null ) {
            return null;
        }

        ContentResponse.ContentResponseBuilder contentResponse = ContentResponse.builder();

        contentResponse.contentId( contentEntity.getContentId() );
        contentResponse.topicId( contentEntity.getTopicId() );
        contentResponse.contentType( contentEntity.getContentType() );
        contentResponse.contentData( contentEntity.getContentData() );
        contentResponse.code( contentEntity.getCode() );
        contentResponse.userCreate( userEntityToUserBasicResponse( contentEntity.getUserCreate() ) );
        contentResponse.userUpdate( userEntityToUserBasicResponse( contentEntity.getUserUpdate() ) );

        return contentResponse.build();
    }

    protected List<ContentResponse> contentEntityListToContentResponseList(List<ContentEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ContentResponse> list1 = new ArrayList<ContentResponse>( list.size() );
        for ( ContentEntity contentEntity : list ) {
            list1.add( contentEntityToContentResponse( contentEntity ) );
        }

        return list1;
    }
}
