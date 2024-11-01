package com.example.englishmaster_be.Mapper;

import com.example.englishmaster_be.DTO.ConfirmationToken.CreateConfirmationTokenDTO;
import com.example.englishmaster_be.Model.ConfirmationToken;
import com.example.englishmaster_be.Model.Response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConfirmationTokenMapper {
    ConfirmationToken toConfirmationToken(CreateConfirmationTokenDTO createConfirmationTokenDTO);
    @Mapping(target = "userConfirmTokenId", source = "userConfirmTokenId",
            defaultExpression = "java(java.util.UUID.randomUUID())")
    ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}
