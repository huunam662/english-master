package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.model.pack.PackEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class PackMapperImpl implements PackMapper {

    @Override
    public PackResponse toPackResponse(PackEntity packEntity) {
        if ( packEntity == null ) {
            return null;
        }

        PackResponse.PackResponseBuilder packResponse = PackResponse.builder();

        packResponse.packId( packEntity.getPackId() );
        packResponse.packName( packEntity.getPackName() );
        packResponse.createAt( packEntity.getCreateAt() );
        packResponse.updateAt( packEntity.getUpdateAt() );

        return packResponse.build();
    }

    @Override
    public List<PackResponse> toPackResponseList(List<PackEntity> packEntityList) {
        if ( packEntityList == null ) {
            return null;
        }

        List<PackResponse> list = new ArrayList<PackResponse>( packEntityList.size() );
        for ( PackEntity packEntity : packEntityList ) {
            list.add( toPackResponse( packEntity ) );
        }

        return list;
    }
}
