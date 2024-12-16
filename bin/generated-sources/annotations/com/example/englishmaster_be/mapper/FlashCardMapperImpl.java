package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordListResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class FlashCardMapperImpl implements FlashCardMapper {

    @Override
    public FlashCardResponse toFlashCardResponse(FlashCardEntity flashCard) {
        if ( flashCard == null ) {
            return null;
        }

        FlashCardResponse.FlashCardResponseBuilder flashCardResponse = FlashCardResponse.builder();

        flashCardResponse.flashCardId( flashCard.getFlashCardId() );
        flashCardResponse.flashCardTitle( flashCard.getFlashCardTitle() );
        flashCardResponse.flashCardImage( flashCard.getFlashCardImage() );
        flashCardResponse.flashCardDescription( flashCard.getFlashCardDescription() );
        flashCardResponse.createAt( flashCard.getCreateAt() );
        flashCardResponse.updateAt( flashCard.getUpdateAt() );
        flashCardResponse.user( userEntityToUserBasicResponse( flashCard.getUser() ) );
        flashCardResponse.userCreate( userEntityToUserBasicResponse( flashCard.getUserCreate() ) );
        flashCardResponse.userUpdate( userEntityToUserBasicResponse( flashCard.getUserUpdate() ) );

        return flashCardResponse.build();
    }

    @Override
    public List<FlashCardResponse> toFlashCardResponseList(List<FlashCardEntity> flashCardList) {
        if ( flashCardList == null ) {
            return null;
        }

        List<FlashCardResponse> list = new ArrayList<FlashCardResponse>( flashCardList.size() );
        for ( FlashCardEntity flashCardEntity : flashCardList ) {
            list.add( toFlashCardResponse( flashCardEntity ) );
        }

        return list;
    }

    @Override
    public FlashCardWordListResponse toFlashCardListWordResponse(FlashCardEntity flashCard) {
        if ( flashCard == null ) {
            return null;
        }

        FlashCardWordListResponse.FlashCardWordListResponseBuilder flashCardWordListResponse = FlashCardWordListResponse.builder();

        flashCardWordListResponse.flashCardTitle( flashCard.getFlashCardTitle() );
        flashCardWordListResponse.flashCardDescription( flashCard.getFlashCardDescription() );

        flashCardWordListResponse.flashCardWords( FlashCardWordMapper.INSTANCE.toFlashCardWordResponseList(flashCard.getFlashCardWords()) );

        return flashCardWordListResponse.build();
    }

    @Override
    public void flowToFlashCardEntity(FlashCardRequest flashCardRequest, FlashCardEntity flashCard) {
        if ( flashCardRequest == null ) {
            return;
        }

        flashCard.setFlashCardTitle( flashCardRequest.getFlashCardTitle() );
        flashCard.setFlashCardDescription( flashCardRequest.getFlashCardDescription() );
    }

    protected UserBasicResponse userEntityToUserBasicResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserBasicResponse.UserBasicResponseBuilder userBasicResponse = UserBasicResponse.builder();

        userBasicResponse.userId( userEntity.getUserId() );
        userBasicResponse.name( userEntity.getName() );

        return userBasicResponse.build();
    }
}
