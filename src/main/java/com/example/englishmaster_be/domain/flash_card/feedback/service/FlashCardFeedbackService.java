package com.example.englishmaster_be.domain.flash_card.feedback.service;

import com.example.englishmaster_be.advice.exception.template.ApplicationException;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.req.FlashCardFeedbackReq;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.feedback.repository.FlashCardFeedbackRepository;
import com.example.englishmaster_be.domain.flash_card.feedback.repository.FlashCardFeedbackSpecRepository;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.service.IFlashCardService;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import org.apache.http.client.HttpResponseException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
public class FlashCardFeedbackService implements IFlashCardFeedbackService{

    private final FlashCardFeedbackRepository flashCardFeedbackRepository;
    private final FlashCardFeedbackSpecRepository flashCardFeedbackSpecRepository;
    private final IFlashCardService flashCardService;
    private final IUserService userService;

    @Lazy
    public FlashCardFeedbackService(FlashCardFeedbackSpecRepository flashCardFeedbackSpecRepository, FlashCardFeedbackRepository flashCardFeedbackRepository, IFlashCardService flashCardService, IUserService userService) {
        this.flashCardFeedbackSpecRepository = flashCardFeedbackSpecRepository;
        this.flashCardFeedbackRepository = flashCardFeedbackRepository;
        this.flashCardService = flashCardService;
        this.userService = userService;
    }

    @Override
    public FlashCardFeedbackEntity getSingleFlashCardFeedback(UUID id) {
        return flashCardFeedbackRepository.findEntityById(id)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Flash card feedback not found.", new Exception("id")));
    }

    @Override
    public List<FlashCardFeedbackEntity> getListFlashCardFeedback() {
        return flashCardFeedbackRepository.findAllEntity();
    }

    @Transactional
    @Override
    public FlashCardFeedbackEntity createFlashCardFeedback(UUID flashCardId, FlashCardFeedbackReq body) {
        FlashCardEntity flashCard = flashCardService.getSingleFlashCardToId(flashCardId);
        FlashCardFeedbackEntity flashCardFeedback = new FlashCardFeedbackEntity();
        flashCardFeedback.setStar(body.getStar());
        flashCardFeedback.setContent(body.getContent());
        flashCardFeedback.setFlashCard(flashCard);
        return flashCardFeedbackRepository.save(flashCardFeedback);
    }

    @Transactional
    @Override
    public FlashCardFeedbackEntity editFlashCardFeedbackContent(UUID id, FlashCardFeedbackReq body) {
        UserEntity userEdit = userService.currentUser();
        FlashCardFeedbackEntity flashCardFeedback = getSingleFlashCardFeedback(id);
        if(!userEdit.getUserId().equals(flashCardFeedback.getUserFeedback().getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "You are not allowed to edit this flash card feedback.");
        flashCardFeedback.setContent(body.getContent());
        return flashCardFeedbackRepository.save(flashCardFeedback);
    }

    @Transactional
    @Override
    public void deleteSingleFlashCardFeedback(UUID id) {
        UserEntity userEdit = userService.currentUser();
        FlashCardFeedbackEntity flashCardFeedback = getSingleFlashCardFeedback(id);
        if(!userEdit.getRole().getRoleName().equals(Role.ADMIN)){
            if(!userEdit.getUserId().equals(flashCardFeedback.getUserFeedback().getUserId()))
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "You are not allowed to delete this flash card feedback.");
        }
        flashCardFeedbackRepository.delete(flashCardFeedback);
    }

    @Override
    public Page<FlashCardFeedbackEntity> getPageFlashCardFeedback(PageOptionsReq optionsReq) {
        return flashCardFeedbackSpecRepository.findPageFlashCardFeedback(optionsReq);
    }

    @Override
    public Page<FlashCardFeedbackEntity> getPageFlashCardFeedbackToFlashCardId(UUID flashCardId, PageOptionsReq optionsReq) {
        return flashCardFeedbackSpecRepository.findPageFlashCardFeedbackByFlashCardId(flashCardId, optionsReq);
    }
}
