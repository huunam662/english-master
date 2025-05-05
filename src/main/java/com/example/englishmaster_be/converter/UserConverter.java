package com.example.englishmaster_be.converter;

import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.user.dto.request.UserChangeProfileRequest;
import com.example.englishmaster_be.domain.auth.dto.request.UserRegisterRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserProfileResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserResponse;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(builder = @Builder(disableBuilder = true))
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserEntity toUserEntity(UserRegisterRequest userRegisterDTO);

    UserResponse toUserResponse(UserEntity user);

    List<UserResponse> toUserResponseList(List<UserEntity> userEntityList);

    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "user", expression = "java(toUserResponse(userEntity))")
    UserProfileResponse toInformationUserResponse(UserEntity userEntity);

    @Mapping(target = "refreshToken", expression = "java(sessionActiveEntity.getCode())")
    @Mapping(target = "accessToken", expression = "java(jwtToken)")
    @Mapping(target = "userAuth.role", expression = "java(sessionActiveEntity.getUser().getRole().getRoleName())")
    @Mapping(target = "userAuth.userId", expression = "java(sessionActiveEntity.getUser().getUserId())")
    @Mapping(target = "userAuth.name", expression = "java(sessionActiveEntity.getUser().getName())")
    @Mapping(target = "userAuth.email", expression = "java(sessionActiveEntity.getUser().getEmail())")
    @Mapping(target = "userAuth.avatar", expression = "java(sessionActiveEntity.getUser().getAvatar())")
    UserAuthResponse toUserAuthResponse(SessionActiveEntity sessionActiveEntity, String jwtToken);

    @Mapping(target = "avatar", ignore = true)
    void flowToUserEntity(UserChangeProfileRequest changeProfileRequest, @MappingTarget UserEntity userEntity);

}

