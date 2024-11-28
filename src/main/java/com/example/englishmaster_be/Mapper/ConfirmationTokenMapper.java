package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.confirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConfirmationTokenMapper {
    ConfirmationToken toConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO);
    ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}

