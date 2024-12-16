package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordResponse;
import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-16T12:56:24+0700",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
public class FlashCardWordMapperImpl implements FlashCardWordMapper {

    @Override
    public FlashCardWordResponse toFlashCardWordResponse(FlashCardWordEntity flashCardWord) {
        if ( flashCardWord == null ) {
            return null;
        }

        FlashCardWordResponse.FlashCardWordResponseBuilder flashCardWordResponse = FlashCardWordResponse.builder();

        flashCardWordResponse.flashCardId( flashCardWordFlashCardFlashCardId( flashCardWord ) );
        flashCardWordResponse.wordId( flashCardWord.getWordId() );
        flashCardWordResponse.word( flashCardWord.getWord() );
        flashCardWordResponse.image( flashCardWord.getImage() );
        flashCardWordResponse.type( flashCardWord.getType() );
        flashCardWordResponse.spelling( flashCardWord.getSpelling() );
        flashCardWordResponse.example( flashCardWord.getExample() );
        flashCardWordResponse.note( flashCardWord.getNote() );
        flashCardWordResponse.define( flashCardWord.getDefine() );
        flashCardWordResponse.createAt( flashCardWord.getCreateAt() );
        flashCardWordResponse.updateAt( flashCardWord.getUpdateAt() );
        flashCardWordResponse.userCreate( userEntityToUserBasicResponse( flashCardWord.getUserCreate() ) );
        flashCardWordResponse.userUpdate( userEntityToUserBasicResponse( flashCardWord.getUserUpdate() ) );

        return flashCardWordResponse.build();
    }

    @Override
    public FlashCardWordEntity toFlashCardWord(FlashCardWordRequest flashCardWordRequest) {
        if ( flashCardWordRequest == null ) {
            return null;
        }

        FlashCardWordEntity.FlashCardWordEntityBuilder flashCardWordEntity = FlashCardWordEntity.builder();

        flashCardWordEntity.word( flashCardWordRequest.getWord() );
        flashCardWordEntity.type( flashCardWordRequest.getType() );
        flashCardWordEntity.spelling( flashCardWordRequest.getSpelling() );
        flashCardWordEntity.example( flashCardWordRequest.getExample() );
        flashCardWordEntity.note( flashCardWordRequest.getNote() );
        flashCardWordEntity.define( flashCardWordRequest.getDefine() );

        return flashCardWordEntity.build();
    }

    @Override
    public List<FlashCardWordResponse> toFlashCardWordResponseList(List<FlashCardWordEntity> flashCardWords) {
        if ( flashCardWords == null ) {
            return null;
        }

        List<FlashCardWordResponse> list = new ArrayList<FlashCardWordResponse>( flashCardWords.size() );
        for ( FlashCardWordEntity flashCardWordEntity : flashCardWords ) {
            list.add( toFlashCardWordResponse( flashCardWordEntity ) );
        }

        return list;
    }

    @Override
    public void flowToFlashCardWordEntity(FlashCardWordRequest flashCardWordRequest, FlashCardWordEntity flashCardWord) {
        if ( flashCardWordRequest == null ) {
            return;
        }

        flashCardWord.setWord( flashCardWordRequest.getWord() );
        flashCardWord.setType( flashCardWordRequest.getType() );
        flashCardWord.setSpelling( flashCardWordRequest.getSpelling() );
        flashCardWord.setExample( flashCardWordRequest.getExample() );
        flashCardWord.setNote( flashCardWordRequest.getNote() );
        flashCardWord.setDefine( flashCardWordRequest.getDefine() );
    }

    private UUID flashCardWordFlashCardFlashCardId(FlashCardWordEntity flashCardWordEntity) {
        FlashCardEntity flashCard = flashCardWordEntity.getFlashCard();
        if ( flashCard == null ) {
            return null;
        }
        return flashCard.getFlashCardId();
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
