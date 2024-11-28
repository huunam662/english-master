package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import com.example.englishmaster_be.Service.*;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.UUID;

@RestController
@RequestMapping("/api/flashCardWord")
public class FlashCardWordController {
    @Autowired
    private IUserService IUserService;

    @Autowired
    private IFlashCardWordService IFlashCardWordService;
    @Autowired
    private IFileStorageService IFileStorageService;

    @DeleteMapping(value = "/{flashCardWordId:.+}/removeWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> removeWord(@PathVariable UUID flashCardWordId){
        ResponseModel responseModel = new ResponseModel();

        try {
            FlashCardWord flashCardWord = IFlashCardWordService.findWordToID(flashCardWordId);

            IFlashCardWordService.delete(flashCardWord);
            responseModel.setMessage("Delete flashcard word successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete flashcard word fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }

    @PutMapping(value = "/{flashCardWordId:.+}/updateWord", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateWord(@PathVariable UUID flashCardWordId, @ModelAttribute CreateFlashCardWordDTO createFlashCardWordDTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();
            String filename = null;

            MultipartFile image = createFlashCardWordDTO.getImage();

            if (image != null && !image.isEmpty()) {
                filename = IFileStorageService.nameFile(image);}

            FlashCardWord flashCardWord = IFlashCardWordService.findWordToID(flashCardWordId);

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

            if(filename != null){
//                IFileStorageService.delete(flashCardWord.getImage());
                flashCardWord.setImage(filename);

            }

            flashCardWord.setUserUpdate(user);
            IFlashCardWordService.save(flashCardWord);
            if(filename != null){
                IFileStorageService.save(createFlashCardWordDTO.getImage(), filename);
            }
            FlashCardWordResponse flashCardWordResponse = new FlashCardWordResponse(flashCardWord);
            responseModel.setMessage("Update flashcard word successfully");
            responseModel.setResponseData(flashCardWordResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update flashcard word fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping("/searchByWord")
    public ResponseEntity<ResponseModel> searchFlashCardByWord(@RequestParam(value = "query") String query) {
        ResponseModel responseModel = new ResponseModel();
        try {
            JSONArray responseArray = new JSONArray();

            for (FlashCardWord flashCardWord : IFlashCardWordService.searchByFlashCardWord(query)) {
                responseArray.add(flashCardWord.getWord());
            }

            responseModel.setMessage("Show list flashcard word successfully");
            responseModel.setResponseData(responseArray);


            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list flashcard word fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
}
