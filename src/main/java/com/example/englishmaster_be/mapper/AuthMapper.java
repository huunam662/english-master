package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "refreshToken", expression = "java(sessionActive.getCode())")
    @Mapping(target = "accessToken", expression = "java(jwtToken)")
    UserAuthResponse toUserAuthResponse(SessionActiveEntity sessionActive, String jwtToken);

}
