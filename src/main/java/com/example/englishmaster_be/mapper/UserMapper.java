package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.dto.UserRegisterDTO;
import com.example.englishmaster_be.model.response.UserResponse;
import com.example.englishmaster_be.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRegisterDTO userRegisterDTO);
    UserResponse toDto(User user);
}

