package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.FlashCard.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flashCard")
public class FlashCardController {

    @Autowired
    private IUserService IUserService;

    @Autowired
    private IFileStorageService IFileStorageService;

    @Autowired
    private IFlashCardService IFlashCardService;

    @GetMapping(value = "/{flashCardId:.+}/listFlashCardWord")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getWordToFlashCard(@PathVariable UUID flashCardId){
        ResponseModel responseModel = new ResponseModel();

        try {
            FlashCard flashCard = IFlashCardService.findFlashCardToId(flashCardId);
            JSONObject flashCardWordObject = new JSONObject();

            List<FlashCardWordResponse> flashCardWordResponseList = new ArrayList<>();

            for(FlashCardWord flashCardWord : flashCard.getFlashCardWords()){
                FlashCardWordResponse flashCardWordResponse = new FlashCardWordResponse(flashCardWord);
                flashCardWordResponseList.add(flashCardWordResponse);
            }

            flashCardWordObject.put("flashCardTitle", flashCard.getFlashCardTitle());
            flashCardWordObject.put("flashCardDescription", flashCard.getFlashCardDescription());
            flashCardWordObject.put("flashCardWord", flashCardWordResponseList);
            responseModel.setMessage("Show list flashcard word successfully");
            responseModel.setResponseData(flashCardWordObject);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Show list flashcard word fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
    }


    @GetMapping(value = "/listFlashCardUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> listFlashCardUser(){
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();

            List<FlashCard> flashCardList = IFlashCardService.findFlashCardToUser(user);

            List<FlashCardResponse> flashCardResponseList = new ArrayList<>();

            for(FlashCard flashCard : flashCardList){
                FlashCardResponse flashCardResponse = new FlashCardResponse(flashCard);
                flashCardResponseList.add(flashCardResponse);
            }

            responseModel.setMessage("Show list flashcard successfully");
            responseModel.setResponseData(flashCardResponseList);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Show list flashcard fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
    }


    @PostMapping(value = "/addFlashCardUser", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> addFlashCardUser(@ModelAttribute CreateFlashCardTO createFlashCardTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();

            String filename = IFileStorageService.nameFile(createFlashCardTO.getFlashCardImage());

            FlashCard flashCard = new FlashCard();
            flashCard.setFlashCardTitle(createFlashCardTO.getFlashCardTitle());
            flashCard.setFlashCardDescription(createFlashCardTO.getFlashCardDescription());
            flashCard.setFlashCardImage(filename);
            flashCard.setUser(user);

            flashCard.setUserUpdate(user);
            flashCard.setUserCreate(user);

            IFlashCardService.saveFlashCard(flashCard);
            IFileStorageService.save(createFlashCardTO.getFlashCardImage(), filename);

            FlashCardResponse flashCardResponse = new FlashCardResponse(flashCard);

            responseModel.setMessage("Create flashcard successfully");
            responseModel.setResponseData(flashCardResponse);
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Create flashcard fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
    }
}
