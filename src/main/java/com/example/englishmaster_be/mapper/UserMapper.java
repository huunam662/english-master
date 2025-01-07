package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.user.dto.request.UserChangeProfileRequest;
import com.example.englishmaster_be.domain.auth.dto.request.UserRegisterRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserProfileResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserResponse;
import com.example.englishmaster_be.model.user.UserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserRegisterRequest userRegisterDTO);

    UserResponse toUserResponse(UserEntity user);

    List<UserResponse> toUserResponseList(List<UserEntity> userEntityList);

    @Mapping(target = "avatar", ignore = true)
    void flowToUserEntity(UserChangeProfileRequest changeProfileRequest, @MappingTarget UserEntity userEntity);

    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "user", expression = "java(toUserResponse(userEntity))")
    UserProfileResponse toInformationUserResponse(UserEntity userEntity);

}

