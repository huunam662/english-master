package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class PartMapperImpl implements PartMapper {

    @Override
    public PartEntity toPartEntity(PartRequest partDto) {
        if ( partDto == null ) {
            return null;
        }

        PartEntity.PartEntityBuilder partEntity = PartEntity.builder();

        partEntity.partId( partDto.getPartId() );
        partEntity.partName( partDto.getPartName() );
        partEntity.partDescription( partDto.getPartDescription() );
        partEntity.partType( partDto.getPartType() );

        return partEntity.build();
    }

    @Override
    public PartResponse toPartResponse(PartEntity part) {
        if ( part == null ) {
            return null;
        }

        PartResponse.PartResponseBuilder partResponse = PartResponse.builder();

        partResponse.partId( part.getPartId() );
        partResponse.partName( part.getPartName() );
        partResponse.partDescription( part.getPartDescription() );
        partResponse.partType( part.getPartType() );
        partResponse.contentType( part.getContentType() );
        partResponse.contentData( part.getContentData() );
        partResponse.createAt( part.getCreateAt() );
        partResponse.updateAt( part.getUpdateAt() );
        partResponse.userCreate( userEntityToUserBasicResponse( part.getUserCreate() ) );
        partResponse.userUpdate( userEntityToUserBasicResponse( part.getUserUpdate() ) );

        partResponse.totalQuestion( part.getQuestions() != null ? part.getQuestions().size() : 0 );

        return partResponse.build();
    }

    @Override
    public List<PartResponse> toPartResponseList(List<PartEntity> partList) {
        if ( partList == null ) {
            return null;
        }

        List<PartResponse> list = new ArrayList<PartResponse>( partList.size() );
        for ( PartEntity partEntity : partList ) {
            list.add( toPartResponse( partEntity ) );
        }

        return list;
    }

    @Override
    public void flowToPartEntity(PartRequest partRequest, PartEntity partEntity) {
        if ( partRequest == null ) {
            return;
        }

        partEntity.setPartName( partRequest.getPartName() );
        partEntity.setPartDescription( partRequest.getPartDescription() );
        partEntity.setPartType( partRequest.getPartType() );
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
