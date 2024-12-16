package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.type.dto.request.TypeRequest;
import com.example.englishmaster_be.domain.type.dto.response.TypeResponse;
import com.example.englishmaster_be.model.type.TypeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class TypeMapperImpl implements TypeMapper {

    @Override
    public TypeResponse toTypeResponse(TypeEntity typeEntity) {
        if ( typeEntity == null ) {
            return null;
        }

        TypeResponse.TypeResponseBuilder typeResponse = TypeResponse.builder();

        typeResponse.typeId( typeEntity.getTypeId() );
        typeResponse.typeName( typeEntity.getTypeName() );
        typeResponse.nameSlug( typeEntity.getNameSlug() );

        return typeResponse.build();
    }

    @Override
    public TypeEntity toTypeEntity(TypeRequest typeRequest) {
        if ( typeRequest == null ) {
            return null;
        }

        TypeEntity.TypeEntityBuilder typeEntity = TypeEntity.builder();

        typeEntity.typeId( typeRequest.getTypeId() );
        typeEntity.typeName( typeRequest.getTypeName() );
        typeEntity.nameSlug( typeRequest.getNameSlug() );

        return typeEntity.build();
    }

    @Override
    public List<TypeResponse> toTypeResponseList(List<TypeEntity> typeEntityList) {
        if ( typeEntityList == null ) {
            return null;
        }

        List<TypeResponse> list = new ArrayList<TypeResponse>( typeEntityList.size() );
        for ( TypeEntity typeEntity : typeEntityList ) {
            list.add( toTypeResponse( typeEntity ) );
        }

        return list;
    }

    @Override
    public void flowToTypeEntity(TypeRequest typeRequest, TypeEntity typeEntity) {
        if ( typeRequest == null ) {
            return;
        }

        typeEntity.setTypeName( typeRequest.getTypeName() );
        typeEntity.setNameSlug( typeRequest.getNameSlug() );
    }
}
