package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.auth.dto.request.UserConfirmTokenRequest;
import com.example.englishmaster_be.domain.auth.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class ConfirmationTokenMapperImpl implements ConfirmationTokenMapper {

    @Override
    public ConfirmationTokenEntity toConfirmationTokenEntity(UserConfirmTokenRequest confirmationTokenRequest) {
        if ( confirmationTokenRequest == null ) {
            return null;
        }

        ConfirmationTokenEntity.ConfirmationTokenEntityBuilder confirmationTokenEntity = ConfirmationTokenEntity.builder();

        confirmationTokenEntity.user( confirmationTokenRequest.getUser() );

        return confirmationTokenEntity.build();
    }

    @Override
    public UserConfirmTokenResponse toConfirmationTokenResponse(ConfirmationTokenEntity confirmationToken) {
        if ( confirmationToken == null ) {
            return null;
        }

        UserConfirmTokenResponse.UserConfirmTokenResponseBuilder userConfirmTokenResponse = UserConfirmTokenResponse.builder();

        userConfirmTokenResponse.userConfirmTokenId( confirmationToken.getUserConfirmTokenId() );
        if ( confirmationToken.getType() != null ) {
            userConfirmTokenResponse.type( confirmationToken.getType().name() );
        }
        userConfirmTokenResponse.code( confirmationToken.getCode() );
        userConfirmTokenResponse.user( confirmationToken.getUser() );
        userConfirmTokenResponse.createAt( confirmationToken.getCreateAt() );

        return userConfirmTokenResponse.build();
    }
}
