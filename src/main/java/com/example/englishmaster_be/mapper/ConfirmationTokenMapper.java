package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.auth.dto.request.UserConfirmTokenRequest;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ConfirmationTokenMapper {

    ConfirmationTokenMapper INSTANCE = Mappers.getMapper(ConfirmationTokenMapper.class);

    ConfirmationTokenEntity toConfirmationTokenEntity(UserConfirmTokenRequest confirmationTokenRequest);

    UserConfirmTokenResponse toConfirmationTokenResponse(ConfirmationTokenEntity confirmationToken);
}

