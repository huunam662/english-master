package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardDTO;
import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.DTO.FlashCard.UpdateFlashCardDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.FlashCard;
import com.example.englishmaster_be.Model.FlashCardWord;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Service.IFlashCardService;
import com.example.englishmaster_be.Service.IUserService;
import com.google.cloud.storage.Blob;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardServiceImpl implements IFlashCardService {


    FlashCardRepository flashCardRepository;

    IUserService userService;

    IFileStorageService fileStorageService;


    @Override
    public FlashCard getFlashCardToId(UUID flashCardId) {
        return flashCardRepository.findByFlashCardId(flashCardId)
                .orElseThrow(
                        () -> new IllegalArgumentException("FlashCard not found with ID: " + flashCardId)
                );
    }

    @Override
    public List<FlashCard> getFlashCardToUser(User user) {
        return flashCardRepository.findByUser(user, Sort.by(Sort.Order.desc("updateAt")));
    }

    @Override
    public FlashCardListWordsResponse getWordToFlashCard(UUID flashCardId) {

        FlashCard flashCard = getFlashCardToId(flashCardId);

        FlashCardListWordsResponse flashCardListWordsResponse = FlashCardListWordsResponse
                .builder()
                    .flashCardTitle(flashCard.getFlashCardTitle())
                    .flashCardDescription(flashCard.getFlashCardDescription())
                    .flashCardWord(new ArrayList<>())
                .build();

        flashCard.getFlashCardWords().forEach(word -> {

            FlashCardWordResponse flashCardWordResponse = new FlashCardWordResponse(word);

            flashCardListWordsResponse.getFlashCardWord().add(flashCardWordResponse);
        });

        return flashCardListWordsResponse;
    }

    @Override
    public List<FlashCardResponse> getListFlashCardUser() {

        User user = userService.currentUser();

        List<FlashCard> flashCardList = getFlashCardToUser(user);

        List<FlashCardResponse> flashCardResponseList = new ArrayList<>();

        flashCardList.forEach(flashCard -> {

            FlashCardResponse flashCardResponse = new FlashCardResponse(flashCard);

            flashCardResponseList.add(flashCardResponse);
        });

        return flashCardResponseList;
    }

    @Transactional
    @Override
    public void delete(UUID flashCardId) {

        FlashCard flashCard = getFlashCardToId(flashCardId);

        if(flashCard.getFlashCardImage() != null && !flashCard.getFlashCardImage().isEmpty())
            fileStorageService.delete(flashCard.getFlashCardImage());

        flashCardRepository.delete(flashCard);
    }

    @Transactional
    @Override
    public FlashCardResponse saveFlashCard(CreateFlashCardDTO createFlashCardDTO) {

        User user = userService.currentUser();

        FlashCard flashCard;

        if(createFlashCardDTO instanceof UpdateFlashCardDTO updateFlashCardDTO){

            flashCard = getFlashCardToId(updateFlashCardDTO.getFlashCardId());
            flashCard.setFlashCardTitle(updateFlashCardDTO.getFlashCardTitle());
            flashCard.setFlashCardDescription(updateFlashCardDTO.getFlashCardDescription());
            flashCard.setUser(user);
            flashCard.setUserUpdate(user);
        }
        else flashCard = FlashCard.builder()
                .flashCardTitle(createFlashCardDTO.getFlashCardTitle())
                .flashCardDescription(createFlashCardDTO.getFlashCardDescription())
                .user(user)
                .userCreate(user)
                .userUpdate(user)
                .build();

        if(createFlashCardDTO.getFlashCardImage() != null){

            Blob blobResponse = fileStorageService.save(createFlashCardDTO.getFlashCardImage());

            flashCard.setFlashCardImage(blobResponse.getName());
        }

        flashCard = flashCardRepository.save(flashCard);

        return new FlashCardResponse(flashCard);
    }

}
