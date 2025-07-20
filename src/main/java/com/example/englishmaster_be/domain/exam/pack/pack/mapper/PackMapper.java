package com.example.englishmaster_be.domain.exam.pack.pack.mapper;

import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackFullRes;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackPageRes;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackRes;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.mapper.PackTypeMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.Collection;
import java.util.List;

@Mapper(imports = {PackTypeMapper.class}, builder = @Builder(disableBuilder = true))
public interface PackMapper {

    PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

    PackRes toPackResponse(PackEntity packEntity);

    List<PackRes> toPackResponseList(Collection<PackEntity> packEntityList);

    @Mapping(target = "packType", expression = "java(PackTypeMapper.INSTANCE.toPackTypeResponse(pack.getPackType()))")
    PackFullRes toPackPackTypeResponse(PackEntity pack);

    List<PackFullRes> toPackPackTypeResponseList(Collection<PackEntity> packEntityList);

    PackPageRes toPackPageRes(IPackPageView packPageView);

    List<PackPageRes> toPackPageResList(Collection<IPackPageView> packPageViews);
}
