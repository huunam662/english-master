package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.dto.confirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.model.ConfirmationToken;
import com.example.englishmaster_be.model.response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ConfirmationTokenMapper {
    ConfirmationToken toConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO);
    ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}

