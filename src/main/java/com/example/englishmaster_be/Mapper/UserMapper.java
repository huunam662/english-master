package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.UserRegisterDTO;
import com.example.englishmaster_be.Model.Response.UserResponse;
import com.example.englishmaster_be.Model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRegisterDTO userRegisterDTO);
    UserResponse toDto(User user);
}

