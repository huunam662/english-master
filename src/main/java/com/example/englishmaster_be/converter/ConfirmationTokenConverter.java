package com.example.englishmaster_be.converter;

import com.example.englishmaster_be.domain.auth.dto.request.UserConfirmTokenRequest;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(builder = @Builder(disableBuilder = true))
public interface ConfirmationTokenConverter {

    ConfirmationTokenConverter INSTANCE = Mappers.getMapper(ConfirmationTokenConverter.class);

    SessionActiveEntity toConfirmationTokenEntity(UserConfirmTokenRequest confirmationTokenRequest);

    UserConfirmTokenResponse toConfirmationTokenResponse(SessionActiveEntity confirmationToken);
}

