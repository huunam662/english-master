package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.dto.feedback.CreateFeedbackDTO;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
@SuppressWarnings("unchecked")
public class FeedbackController {
    @Autowired
    private IFeedbackService IFeedbackService;
    @Autowired
    private IFileStorageService IFileStorageService;

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping(value = "/listFeedbackAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> listFeedbackOfAdmin(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
                                                         @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                         @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
                                                         @RequestParam(value = "search", required = false) String search,
                                                         @RequestParam(value = "enable", required = false) String isEnable){
        ResponseModel responseModel = new ResponseModel();
        try {
            JSONObject responseObject = new JSONObject();
            OrderSpecifier<?> orderSpecifier;


            if(Sort.Direction.DESC.equals(sortDirection)){
                orderSpecifier = QFeedback.feedback.updateAt.desc();
            }else {
                orderSpecifier = QFeedback.feedback.updateAt.asc();
            }

            JPAQuery<Feedback> query = queryFactory.selectFrom(QFeedback.feedback)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);

            if (search != null && !search.isEmpty()) {
                String likeExpression = "%" + search.toLowerCase().replace(" ", "%") + "%";

                query.where(QFeedback.feedback.content.lower().like(likeExpression));
            }
            if (isEnable != null){
                query.where(QFeedback.feedback.enable.eq(isEnable.equalsIgnoreCase("enable")));
            }

            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalRecords", totalRecords);
            responseObject.put("totalPages", totalPages);

            List<Feedback> feedbackList = query.fetch();

            List<FeedbackResponse> feedbackResponseList = new ArrayList<>();

            for (Feedback feedback : feedbackList) {
                FeedbackResponse feedbackResponse = new FeedbackResponse(feedback);
                feedbackResponseList.add(feedbackResponse);
            }

            responseObject.put("listFeedback", feedbackResponseList);
            responseModel.setMessage("List feedback successful");
            responseModel.setResponseData(responseObject);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List feedback fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/listFeedbackUser")
    public ResponseEntity<ResponseModel> listFeedbackOfUser(@RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size){
        ResponseModel responseModel = new ResponseModel();
        try {
            OrderSpecifier<?> orderSpecifier = QFeedback.feedback.updateAt.desc();

            JPAQuery<Feedback> query = queryFactory.selectFrom(QFeedback.feedback)
                    .orderBy(orderSpecifier)
                    .limit(size);

            query.where(QFeedback.feedback.enable.eq(true));

            List<Feedback> feedbackList = query.fetch();

            List<FeedbackResponse> feedbackResponseList = new ArrayList<>();

            for (Feedback feedback : feedbackList) {
                FeedbackResponse feedbackResponse = new FeedbackResponse(feedback);
                feedbackResponseList.add(feedbackResponse);
            }

            responseModel.setMessage("List feedback successful");
            responseModel.setResponseData(feedbackResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("List feedback fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping(value = "/createFeedback" , consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<ResponseModel> createFeedback(@ModelAttribute("contentFeedback") CreateFeedbackDTO createFeedbackDTO){
        ResponseModel responseModel = new ResponseModel();
        try {
            Feedback feedback = new Feedback();
            feedback.setName(createFeedbackDTO.getName());
            feedback.setDescription(createFeedbackDTO.getDescription());
            feedback.setContent(createFeedbackDTO.getContent());

            String fileNameImage = null;
            MultipartFile image = createFeedbackDTO.getAvatar();
            if (image != null && !image.isEmpty()) {
                fileNameImage = IFileStorageService.nameFile(image);
            }

            if(fileNameImage != null){
                feedback.setAvatar(fileNameImage);
            }
            IFeedbackService.save(feedback);

            if(fileNameImage != null){
                IFileStorageService.save(createFeedbackDTO.getAvatar(), fileNameImage);
            }
            FeedbackResponse feedbackResponse = new FeedbackResponse(feedback);
            responseModel.setMessage("Create feedback successful");
            responseModel.setResponseData(feedbackResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create feedback fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PatchMapping (value = "/{feedbackId:.+}/enableFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> enableFeedback(@PathVariable UUID feedbackId, @RequestParam boolean enable){
        ResponseModel responseModel = new ResponseModel();
        try {

            Feedback Feedback = IFeedbackService.findFeedbackById(feedbackId);
            Feedback.setEnable(enable);

            IFeedbackService.save(Feedback);
            if(enable){
                responseModel.setMessage("Enable Feedback successful");
            }else {
                responseModel.setMessage("Disable Feedback successful");
            }


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            if(enable){
                exceptionResponseModel.setMessage("Enable Feedback fail: " + e.getMessage());

            }else {
                exceptionResponseModel.setMessage("Disable Feedback fail: "+ e.getMessage());

            }
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PatchMapping(value = "/{feedbackId:.+}/updateFeedback", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updateFeedback(@PathVariable UUID feedbackId, @ModelAttribute("contentFeedback") CreateFeedbackDTO createFeedbackDTO){
        ResponseModel responseModel = new ResponseModel();
        try {

            Feedback feedback = IFeedbackService.findFeedbackById(feedbackId);

            feedback.setName(createFeedbackDTO.getName());
            feedback.setDescription(createFeedbackDTO.getDescription());
            feedback.setContent(createFeedbackDTO.getContent());

            String fileNameImage = null;
            if(createFeedbackDTO.getAvatar() != null){
                MultipartFile image = createFeedbackDTO.getAvatar();
                if (!image.isEmpty()) {
                    fileNameImage = IFileStorageService.nameFile(image);
                }

                if(feedback.getAvatar() != null){
                    IFileStorageService.delete(feedback.getAvatar());
                }

                if(fileNameImage != null){
                    feedback.setAvatar(fileNameImage);
                }
                if(fileNameImage != null){
                    IFileStorageService.save(createFeedbackDTO.getAvatar(), fileNameImage);
                }
            }

            IFeedbackService.save(feedback);

            FeedbackResponse feedbackResponse = new FeedbackResponse(feedback);
            responseModel.setMessage("Update Feedback successful");
            responseModel.setResponseData(feedbackResponse);
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Update Feedback fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{feedbackId:.+}/deleteFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deleteFeedback(@PathVariable UUID feedbackId){
        ResponseModel responseModel = new ResponseModel();
        try {
            Feedback feedback = IFeedbackService.findFeedbackById(feedbackId);
            if(feedback.getAvatar() != null){
                IFileStorageService.delete(feedback.getAvatar());
            }

            IFeedbackService.delete(feedback);

            responseModel.setMessage("Delete Feedback successful");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete Feedback fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
}
