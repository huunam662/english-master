package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(target = "refreshToken", expression = "java(sessionActiveEntity.getCode())")
    @Mapping(target = "accessToken", expression = "java(jwtToken)")
    @Mapping(target = "userAuth.role", expression = "java(sessionActiveEntity.getUser().getRole().getRoleName())")
    @Mapping(target = "userAuth.userId", expression = "java(sessionActiveEntity.getUser().getUserId())")
    @Mapping(target = "userAuth.name", expression = "java(sessionActiveEntity.getUser().getName())")
    @Mapping(target = "userAuth.email", expression = "java(sessionActiveEntity.getUser().getEmail())")
    @Mapping(target = "userAuth.avatar", expression = "java(sessionActiveEntity.getUser().getAvatar())")
    UserAuthResponse toUserAuthResponse(SessionActiveEntity sessionActiveEntity, String jwtToken);

}
