package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardRequest;
import com.example.englishmaster_be.Mapper.FlashCardMapper;
import com.example.englishmaster_be.entity.FlashCardEntity;
import com.example.englishmaster_be.entity.UserEntity;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Service.IFlashCardService;
import com.example.englishmaster_be.Service.IUserService;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardServiceImpl implements IFlashCardService {


    FlashCardRepository flashCardRepository;

    IUserService userService;

    IFileStorageService fileStorageService;


    @Override
    public FlashCardEntity getFlashCardById(UUID flashCardId) {
        return flashCardRepository.findByFlashCardId(flashCardId)
                .orElseThrow(
                        () -> new IllegalArgumentException("FlashCardEntity not found with ID: " + flashCardId)
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
    public void delete(UUID flashCardId) {

        FlashCardEntity flashCard = getFlashCardById(flashCardId);

        if(flashCard.getFlashCardImage() != null && !flashCard.getFlashCardImage().isEmpty())
            fileStorageService.delete(flashCard.getFlashCardImage());

        flashCardRepository.delete(flashCard);
    }

    @Transactional
    @Override
    public FlashCardEntity saveFlashCard(FlashCardRequest flashCardRequest) {

        UserEntity user = userService.currentUser();

        FlashCardEntity flashCard;

        if(flashCardRequest.getFlashCardId() != null)
            flashCard = getFlashCardById(flashCardRequest.getFlashCardId());

        else flashCard = FlashCardEntity.builder()
                .userCreate(user)
                .createAt(LocalDateTime.now())
                .build();

        FlashCardMapper.INSTANCE.flowToFlashCardEntity(flashCardRequest, flashCard);

        if(flashCardRequest.getFlashCardImage() != null){

            Blob blobResponse = fileStorageService.save(flashCardRequest.getFlashCardImage());

            flashCard.setFlashCardImage(blobResponse.getName());
        }

        return flashCardRepository.save(flashCard);
    }

}
