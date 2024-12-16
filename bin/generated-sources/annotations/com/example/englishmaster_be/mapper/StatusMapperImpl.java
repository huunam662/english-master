package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.status.dto.request.StatusRequest;
import com.example.englishmaster_be.domain.status.dto.response.StatusResponse;
import com.example.englishmaster_be.domain.type.dto.response.TypeResponse;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.type.TypeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class StatusMapperImpl implements StatusMapper {

    @Override
    public void flowToStatusEntity(StatusRequest statusRequest, StatusEntity statusEntity) {
        if ( statusRequest == null ) {
            return;
        }

        statusEntity.setStatusName( statusRequest.getStatusName() );
        statusEntity.setFlag( statusRequest.getFlag() );
    }

    @Override
    public StatusResponse toStatusResponse(StatusEntity statusEntity) {
        if ( statusEntity == null ) {
            return null;
        }

        StatusResponse.StatusResponseBuilder statusResponse = StatusResponse.builder();

        statusResponse.statusId( statusEntity.getStatusId() );
        statusResponse.statusName( statusEntity.getStatusName() );
        statusResponse.flag( statusEntity.getFlag() );
        statusResponse.type( typeEntityToTypeResponse( statusEntity.getType() ) );

        return statusResponse.build();
    }

    @Override
    public List<StatusResponse> toStatusResponseList(List<StatusEntity> statusEntityList) {
        if ( statusEntityList == null ) {
            return null;
        }

        List<StatusResponse> list = new ArrayList<StatusResponse>( statusEntityList.size() );
        for ( StatusEntity statusEntity : statusEntityList ) {
            list.add( toStatusResponse( statusEntity ) );
        }

        return list;
    }

    protected TypeResponse typeEntityToTypeResponse(TypeEntity typeEntity) {
        if ( typeEntity == null ) {
            return null;
        }

        TypeResponse.TypeResponseBuilder typeResponse = TypeResponse.builder();

        typeResponse.typeId( typeEntity.getTypeId() );
        typeResponse.typeName( typeEntity.getTypeName() );
        typeResponse.nameSlug( typeEntity.getNameSlug() );

        return typeResponse.build();
    }
}
