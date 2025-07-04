package com.example.englishmaster_be.domain.user.mapper;

import com.example.englishmaster_be.domain.auth.dto.response.UserAuthProfileResponse;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.news.dto.response.AuthorCommentResponse;
import com.example.englishmaster_be.domain.user.dto.request.UserChangeProfileReq;
import com.example.englishmaster_be.domain.auth.dto.request.UserRegisterRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserProfileRes;
import com.example.englishmaster_be.domain.user.dto.response.UserRes;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Mapper(builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toUserEntity(UserRegisterRequest userRegisterDTO);

    UserRes toUserResponse(UserEntity user);

    List<UserRes> toUserResponseList(Collection<UserEntity> userEntityList);

    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "user", expression = "java(toUserResponse(userEntity))")
    UserProfileRes toInformationUserResponse(UserEntity userEntity);

    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "accessToken", source = "jwtToken")
    @Mapping(target = "userAuth", expression = "java(toUserAuthProfileResponse(user))")
    UserAuthResponse toUserAuthResponse(UUID refreshToken, String jwtToken, UserEntity user);

    @Mapping(target = "role", expression = "java(user.getRole().getRoleName())")
    UserAuthProfileResponse toUserAuthProfileResponse(UserEntity user);

    @Mapping(target = "avatar", ignore = true)
    void flowToUserEntity(UserChangeProfileReq changeProfileRequest, @MappingTarget UserEntity userEntity);

    AuthorCommentResponse toAuthorCommentResponse(UserEntity user);
}

