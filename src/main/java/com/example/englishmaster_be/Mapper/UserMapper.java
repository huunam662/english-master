package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.User.ChangeProfileRequest;
import com.example.englishmaster_be.Model.Request.UserRegisterDTO;
import com.example.englishmaster_be.Model.Response.InformationUserResponse;
import com.example.englishmaster_be.Model.Response.UserResponse;
import com.example.englishmaster_be.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserRegisterDTO userRegisterDTO);

    UserResponse toUserResponse(UserEntity user);

    List<UserResponse> toUserResponseList(List<UserEntity> userEntityList);

    @Mapping(target = "avatar", ignore = true)
    void flowToUserEntity(ChangeProfileRequest changeProfileRequest, @MappingTarget UserEntity userEntity);

    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "user", expression = "java(toUserResponse(userEntity))")
    InformationUserResponse toInformationUserResponse(UserEntity userEntity);

}

