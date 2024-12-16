package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.domain.auth.dto.request.UserRegisterRequest;
import com.example.englishmaster_be.domain.user.dto.request.UserChangeProfileRequest;
import com.example.englishmaster_be.domain.user.dto.response.UserProfileResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserResponse;
import com.example.englishmaster_be.model.role.RoleEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toUserEntity(UserRegisterRequest userRegisterDTO) {
        if ( userRegisterDTO == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.email( userRegisterDTO.getEmail() );
        userEntity.password( userRegisterDTO.getPassword() );
        userEntity.name( userRegisterDTO.getName() );

        return userEntity.build();
    }

    @Override
    public UserResponse toUserResponse(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userId( user.getUserId() );
        userResponse.name( user.getName() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.address( user.getAddress() );
        userResponse.avatar( user.getAvatar() );

        return userResponse.build();
    }

    @Override
    public List<UserResponse> toUserResponseList(List<UserEntity> userEntityList) {
        if ( userEntityList == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( userEntityList.size() );
        for ( UserEntity userEntity : userEntityList ) {
            list.add( toUserResponse( userEntity ) );
        }

        return list;
    }

    @Override
    public void flowToUserEntity(UserChangeProfileRequest changeProfileRequest, UserEntity userEntity) {
        if ( changeProfileRequest == null ) {
            return;
        }

        userEntity.setName( changeProfileRequest.getName() );
        userEntity.setPhone( changeProfileRequest.getPhone() );
        userEntity.setAddress( changeProfileRequest.getAddress() );
    }

    @Override
    public UserProfileResponse toInformationUserResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserProfileResponse.UserProfileResponseBuilder userProfileResponse = UserProfileResponse.builder();

        RoleEnum roleName = userEntityRoleRoleName( userEntity );
        if ( roleName != null ) {
            userProfileResponse.role( roleName.name() );
        }

        userProfileResponse.user( toUserResponse(userEntity) );

        return userProfileResponse.build();
    }

    private RoleEnum userEntityRoleRoleName(UserEntity userEntity) {
        RoleEntity role = userEntity.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getRoleName();
    }
}
