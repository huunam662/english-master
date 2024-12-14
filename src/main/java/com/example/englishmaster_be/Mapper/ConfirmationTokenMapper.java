package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.Model.Request.ConfirmationToken.ConfirmationTokenRequest;
import com.example.englishmaster_be.entity.ConfirmationTokenEntity;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface ConfirmationTokenMapper {

    ConfirmationTokenMapper INSTANCE = Mappers.getMapper(ConfirmationTokenMapper.class);

    ConfirmationTokenEntity toConfirmationTokenEntity(ConfirmationTokenRequest confirmationTokenRequest);

    ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationTokenEntity confirmationToken);
}

