package com.example.englishmaster_be.domain.user.user.mapper;

import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthProfileRes;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.news.comment.dto.res.AuthorCommentRes;
import com.example.englishmaster_be.domain.user.user.dto.req.UserChangeProfileReq;
import com.example.englishmaster_be.domain.user.auth.dto.req.UserRegisterReq;
import com.example.englishmaster_be.domain.user.user.dto.res.UserPageRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserProfileRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import com.example.englishmaster_be.domain.user.user.dto.view.IUserPageView;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
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

    UserEntity toUserEntity(UserRegisterReq userRegisterDTO);

    UserRes toUserResponse(UserEntity user);

    List<UserRes> toUserResponseList(Collection<UserEntity> userEntityList);

    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "user", expression = "java(toUserResponse(userEntity))")
    UserProfileRes toInformationUserResponse(UserEntity userEntity);

    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "accessToken", source = "jwtToken")
    @Mapping(target = "userAuth", expression = "java(toUserAuthProfileResponse(user))")
    UserAuthRes toUserAuthResponse(UUID refreshToken, String jwtToken, UserEntity user);

    @Mapping(target = "role", expression = "java(user.getRole().getRoleName())")
    UserAuthProfileRes toUserAuthProfileResponse(UserEntity user);

    @Mapping(target = "avatar", ignore = true)
    void flowToUserEntity(UserChangeProfileReq changeProfileRequest, @MappingTarget UserEntity userEntity);

    AuthorCommentRes toAuthorCommentResponse(UserEntity user);

    UserPageRes toPageUserRes(IUserPageView pageUserView);

    List<UserPageRes> toPageUserResList(Collection<IUserPageView> pageUserViews);
}

