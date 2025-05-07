package com.example.englishmaster_be.domain.flash_card.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.mapper.FlashCardMapper;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import com.example.englishmaster_be.model.flash_card.FlashCardRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardService implements IFlashCardService {


    FlashCardRepository flashCardRepository;

    IUserService userService;

    IUploadService uploadService;


    @Override
    public FlashCardEntity getFlashCardById(UUID flashCardId) {
        return flashCardRepository.findByFlashCardId(flashCardId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "FlashCardEntity not found with ID: " + flashCardId)
                );
    }

    @Override
    public List<FlashCardEntity> getFlashCardByUser(UserEntity user) {
        return flashCardRepository.findByUser(user, Sort.by(Sort.Order.desc("updateAt")));
    }

    @Override
    public List<FlashCardEntity> getListFlashCardByCurrentUser() {

        UserEntity user = userService.currentUser();

        return getFlashCardByUser(user);
    }

    @Transactional
    @Override
    @SneakyThrows
    public void delete(UUID flashCardId) {

        FlashCardEntity flashCard = getFlashCardById(flashCardId);

        if(flashCard.getFlashCardImage() != null && !flashCard.getFlashCardImage().isEmpty())
            uploadService.delete(
                    FileDeleteRequest.builder()
                            .filepath(flashCard.getFlashCardImage())
                            .build()
            );

        flashCardRepository.delete(flashCard);
    }

    @Transactional
    @Override
    @SneakyThrows
    public FlashCardEntity saveFlashCard(FlashCardRequest flashCardRequest) {

        UserEntity user = userService.currentUser();

        FlashCardEntity flashCard;

        if(flashCardRequest.getFlashCardId() != null)
            flashCard = getFlashCardById(flashCardRequest.getFlashCardId());

        else flashCard = FlashCardEntity.builder()
                .userCreate(user)
                .build();

        FlashCardMapper.INSTANCE.flowToFlashCardEntity(flashCardRequest, flashCard);

        if(flashCardRequest.getFlashCardImage() != null && !flashCardRequest.getFlashCardImage().isEmpty()){

            if(flashCard.getFlashCardImage() != null && !flashCard.getFlashCardImage().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(flashCard.getFlashCardImage())
                                .build()
                );

            flashCard.setFlashCardImage(flashCardRequest.getFlashCardImage());
        }

        return flashCardRepository.save(flashCard);
    }

}
