package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.FlashCard;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFlashCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlashCardServiceImpl implements IFlashCardService {

    @Autowired
    private FlashCardRepository flashCardRepository;
    @Override
    public FlashCard findFlashCardToId(UUID flashCardId) {
        return flashCardRepository.findByFlashCardId(flashCardId).orElseThrow(() -> new IllegalArgumentException("FlashCard not found with ID: " + flashCardId));
    }

    @Override
    public List<FlashCard> findFlashCardToUser(User user) {
        return flashCardRepository.findByUser(user, Sort.by(Sort.Order.desc("updateAt")));
    }

    @Override
    public void saveFlashCard(FlashCard flashCard) {
        flashCardRepository.save(flashCard);
    }


}
