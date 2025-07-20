package com.example.englishmaster_be.domain.exam.pack.type.mapper;

import com.example.englishmaster_be.domain.exam.pack.type.dto.req.CreatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.UpdatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeKeyRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypePageRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.view.IPackTypePageView;
import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PackTypeMapper {

    PackTypeMapper INSTANCE = Mappers.getMapper(PackTypeMapper.class);

    PackTypeRes toPackTypeResponse(PackTypeEntity packTypeEntity);

    List<PackTypeRes> toPackTypeResponseList(Collection<PackTypeEntity> packTypeEntityList);

    PackTypeEntity toPackTypeEntity(CreatePackTypeReq createPackTypeRequest);

    void flowToPackTypeEntity(@MappingTarget PackTypeEntity packTypeEntity, CreatePackTypeReq createPackTypeRequest);

    void flowToPackTypeEntity(@MappingTarget PackTypeEntity packTypeEntity, UpdatePackTypeReq updatePackTypeRequest);

    PackTypeKeyRes toPackTypeKeyResponse(PackTypeEntity packType);

    PackTypePageRes toPackTypePageRes(IPackTypePageView packTypePageView);

    List<PackTypePageRes> toPackTypePageResList(Collection<IPackTypePageView> packTypePageViews);
}
