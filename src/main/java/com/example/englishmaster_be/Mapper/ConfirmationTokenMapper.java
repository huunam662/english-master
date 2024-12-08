package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.ConfirmationToken.SaveConfirmationTokenDTO;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface ConfirmationTokenMapper {

    ConfirmationTokenMapper INSTANCE = Mappers.getMapper(ConfirmationTokenMapper.class);

    ConfirmationToken toConfirmationToken(SaveConfirmationTokenDTO createConfirmationTokenDTO);

    ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}

