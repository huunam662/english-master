package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.Response.UserResponse;
import com.example.englishmaster_be.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRegisterDTO userRegisterDTO);

    UserResponse toDto(User user);
}

