package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.flashCard.CreateFlashCardDTO;
import com.example.englishmaster_be.DTO.flashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private IFlashCardWordService IFlashCardWordService;

    @GetMapping(value = "/{flashCardId:.+}/listFlashCardWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list flashcard word fail: " + e.getMessage());
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }


    @GetMapping(value = "/listFlashCardUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list flashcard fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }


    @PostMapping(value = "/addFlashCardUser", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addFlashCardUser(@ModelAttribute CreateFlashCardDTO createFlashCardDTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();

            String filename = IFileStorageService.nameFile(createFlashCardDTO.getFlashCardImage());

            FlashCard flashCard = new FlashCard();
            flashCard.setFlashCardTitle(createFlashCardDTO.getFlashCardTitle());
            flashCard.setFlashCardDescription(createFlashCardDTO.getFlashCardDescription());
            flashCard.setFlashCardImage(filename);
            flashCard.setUser(user);

            flashCard.setUserUpdate(user);
            flashCard.setUserCreate(user);

            IFlashCardService.saveFlashCard(flashCard);
            IFileStorageService.save(createFlashCardDTO.getFlashCardImage(), filename);

            FlashCardResponse flashCardResponse = new FlashCardResponse(flashCard);

            responseModel.setMessage("Create flashcard successfully");
            responseModel.setResponseData(flashCardResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create flashcard fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }

    @PostMapping(value = "/{flashCardId:.+}/addWordToFlashCard", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> addWordToFlashCard(@PathVariable UUID flashCardId, @ModelAttribute CreateFlashCardWordDTO createFlashCardWordDTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();
            String filename = null;

            MultipartFile image = createFlashCardWordDTO.getImage();

            if (image != null && !image.isEmpty()) {
                filename = IFileStorageService.nameFile(image);}

            FlashCard flashCard = IFlashCardService.findFlashCardToId(flashCardId);

            if(!flashCard.getUser().getUserId().equals(user.getUserId())){
                responseModel.setMessage("You don't add word to flash card ");

                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            FlashCardWord flashCardWord = new FlashCardWord();
            if(!createFlashCardWordDTO.getWord().isEmpty()){
                flashCardWord.setWord(createFlashCardWordDTO.getWord());
            }
            if(!createFlashCardWordDTO.getDefine().isEmpty()){
                flashCardWord.setDefine(createFlashCardWordDTO.getDefine());
            }
            if(!createFlashCardWordDTO.getType().isEmpty()){
                flashCardWord.setType(createFlashCardWordDTO.getType());
            }
            if(!createFlashCardWordDTO.getSpelling().isEmpty()){
                flashCardWord.setSpelling(createFlashCardWordDTO.getSpelling());
            }
            if(!createFlashCardWordDTO.getExample().isEmpty()){
                flashCardWord.setExample(createFlashCardWordDTO.getExample());
            }
            if(!createFlashCardWordDTO.getNote().isEmpty()){
                flashCardWord.setNote(createFlashCardWordDTO.getNote());
            }
            flashCardWord.setFlashCard(flashCard);
            if(filename != null){
                flashCardWord.setImage(filename);
            }

            flashCardWord.setUserUpdate(user);
            flashCardWord.setUserCreate(user);

            IFlashCardWordService.save(flashCardWord);
            if(filename != null){
                IFileStorageService.save(createFlashCardWordDTO.getImage(), filename);
            }


            FlashCardWordResponse flashCardWordResponse = new FlashCardWordResponse(flashCardWord);

            responseModel.setMessage("Create word for flashcard successfully");
            responseModel.setResponseData(flashCardWordResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create word for flashcard fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }

    @DeleteMapping(value = "/{flashCardId:.+}/removeFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> removeWord(@PathVariable UUID flashCardId){
        ResponseModel responseModel = new ResponseModel();

        try {
            FlashCard flashCard = IFlashCardService.findFlashCardToId(flashCardId);

            if(flashCard.getFlashCardImage() != null){
                IFileStorageService.delete(flashCard.getFlashCardImage());
            }

            IFlashCardService.delete(flashCard);
            responseModel.setMessage("Delete flashcard successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete flashcard fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }


    @PutMapping(value = "/{flashCardId:.+}/updateFlashCard", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateFlashCard(@PathVariable UUID flashCardId, @ModelAttribute CreateFlashCardDTO createFlashCardDTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            String filename = null;

            MultipartFile image = createFlashCardDTO.getFlashCardImage();

            if (image != null && !image.isEmpty()) {
                filename = IFileStorageService.nameFile(image);}

            FlashCard flashCard = IFlashCardService.findFlashCardToId(flashCardId);

            flashCard.setFlashCardTitle(createFlashCardDTO.getFlashCardTitle());
            flashCard.setFlashCardDescription(createFlashCardDTO.getFlashCardDescription());

            if(filename != null){
               IFileStorageService.delete(flashCard.getFlashCardImage());
                flashCard.setFlashCardImage(filename);
            }


            IFlashCardService.saveFlashCard(flashCard);
            if(filename != null){
                IFileStorageService.save(createFlashCardDTO.getFlashCardImage(), filename);
            }
            FlashCardResponse flashCardResponse = new FlashCardResponse(flashCard);
            responseModel.setMessage("Update flashcard successfully");
            responseModel.setResponseData(flashCardResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update flashcard fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }
}
