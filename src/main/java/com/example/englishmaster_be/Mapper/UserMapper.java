package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.Response.UserResponse;
import com.example.englishmaster_be.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRegisterDTO userRegisterDTO);
    @Mapping(target = "userId", source = "userId",
            defaultExpression = "java(java.util.UUID.randomUUID())")
    UserResponse toDto(User user);
}
