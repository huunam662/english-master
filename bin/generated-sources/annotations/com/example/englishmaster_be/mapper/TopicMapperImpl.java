package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
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
public class TopicMapperImpl implements TopicMapper {

    @Override
    public TopicRequest toTopicRequest(ExcelTopicResponse topicByExcelFileResponse) {
        if ( topicByExcelFileResponse == null ) {
            return null;
        }

        TopicRequest.TopicRequestBuilder<?, ?> topicRequest = TopicRequest.builder();

        topicRequest.topicId( topicByExcelFileResponse.getTopicId() );
        topicRequest.packId( topicByExcelFileResponse.getPackId() );
        topicRequest.topicName( topicByExcelFileResponse.getTopicName() );
        topicRequest.topicDescription( topicByExcelFileResponse.getTopicDescription() );
        topicRequest.topicType( topicByExcelFileResponse.getTopicType() );
        topicRequest.workTime( topicByExcelFileResponse.getWorkTime() );
        topicRequest.numberQuestion( topicByExcelFileResponse.getNumberQuestion() );
        List<UUID> list = topicByExcelFileResponse.getListPart();
        if ( list != null ) {
            topicRequest.listPart( new ArrayList<UUID>( list ) );
        }
        topicRequest.startTime( topicByExcelFileResponse.getStartTime() );
        topicRequest.endTime( topicByExcelFileResponse.getEndTime() );

        return topicRequest.build();
    }

    @Override
    public TopicResponse toTopicResponse(TopicEntity topicEntity) {
        if ( topicEntity == null ) {
            return null;
        }

        TopicResponse.TopicResponseBuilder topicResponse = TopicResponse.builder();

        topicResponse.packId( topicEntityPackPackId( topicEntity ) );
        topicResponse.statusId( topicEntityStatusStatusId( topicEntity ) );
        if ( topicEntity.getNumberQuestion() != null ) {
            topicResponse.numberQuestion( topicEntity.getNumberQuestion() );
        }
        else {
            topicResponse.numberQuestion( 0 );
        }
        topicResponse.topicId( topicEntity.getTopicId() );
        topicResponse.topicName( topicEntity.getTopicName() );
        topicResponse.topicImage( topicEntity.getTopicImage() );
        topicResponse.topicDescription( topicEntity.getTopicDescription() );
        topicResponse.topicType( topicEntity.getTopicType() );
        topicResponse.workTime( topicEntity.getWorkTime() );
        topicResponse.startTime( topicEntity.getStartTime() );
        topicResponse.endTime( topicEntity.getEndTime() );
        topicResponse.createAt( topicEntity.getCreateAt() );
        topicResponse.updateAt( topicEntity.getUpdateAt() );
        topicResponse.userCreate( userEntityToUserBasicResponse( topicEntity.getUserCreate() ) );
        topicResponse.userUpdate( userEntityToUserBasicResponse( topicEntity.getUserUpdate() ) );
        topicResponse.enable( topicEntity.getEnable() );

        topicResponse.parts( toListPartId(topicEntity.getParts()) );

        return topicResponse.build();
    }

    @Override
    public List<TopicResponse> toTopicResponseList(List<TopicEntity> topicEntityList) {
        if ( topicEntityList == null ) {
            return null;
        }

        List<TopicResponse> list = new ArrayList<TopicResponse>( topicEntityList.size() );
        for ( TopicEntity topicEntity : topicEntityList ) {
            list.add( toTopicResponse( topicEntity ) );
        }

        return list;
    }

    @Override
    public void flowToTopicEntity(TopicRequest topicRequest, TopicEntity topicEntity) {
        if ( topicRequest == null ) {
            return;
        }

        if ( topicRequest.getNumberQuestion() != null ) {
            topicEntity.setNumberQuestion( topicRequest.getNumberQuestion() );
        }
        else {
            topicEntity.setNumberQuestion( 0 );
        }
        topicEntity.setTopicName( topicRequest.getTopicName() );
        topicEntity.setTopicDescription( topicRequest.getTopicDescription() );
        topicEntity.setTopicType( topicRequest.getTopicType() );
        topicEntity.setWorkTime( topicRequest.getWorkTime() );
        topicEntity.setStartTime( topicRequest.getStartTime() );
        topicEntity.setEndTime( topicRequest.getEndTime() );
    }

    @Override
    public void flowToTopicEntity(ExcelTopicResponse topicByExcelFileResponse, TopicEntity topicEntity) {
        if ( topicByExcelFileResponse == null ) {
            return;
        }

        topicEntity.setTopicId( topicByExcelFileResponse.getTopicId() );
        topicEntity.setTopicName( topicByExcelFileResponse.getTopicName() );
        topicEntity.setTopicDescription( topicByExcelFileResponse.getTopicDescription() );
        topicEntity.setTopicType( topicByExcelFileResponse.getTopicType() );
        topicEntity.setWorkTime( topicByExcelFileResponse.getWorkTime() );
        topicEntity.setNumberQuestion( topicByExcelFileResponse.getNumberQuestion() );
        topicEntity.setStartTime( topicByExcelFileResponse.getStartTime() );
        topicEntity.setEndTime( topicByExcelFileResponse.getEndTime() );
    }

    private UUID topicEntityPackPackId(TopicEntity topicEntity) {
        PackEntity pack = topicEntity.getPack();
        if ( pack == null ) {
            return null;
        }
        return pack.getPackId();
    }

    private UUID topicEntityStatusStatusId(TopicEntity topicEntity) {
        StatusEntity status = topicEntity.getStatus();
        if ( status == null ) {
            return null;
        }
        return status.getStatusId();
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
