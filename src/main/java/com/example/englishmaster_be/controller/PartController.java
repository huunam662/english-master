package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.dto.part.CreatePartDTO;
import com.example.englishmaster_be.dto.part.UpdatePartDTO;
import com.example.englishmaster_be.helper.GetExtension;
import com.example.englishmaster_be.dto.*;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.PartResponse;
import com.example.englishmaster_be.service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/part")
public class PartController {

    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IPartService IPartService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> createPart(@RequestBody CreatePartDTO createpartDTO) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            User user = IUserService.currentUser();

            Part part = new Part(createpartDTO.getPartName().toUpperCase(), createpartDTO.getPartDiscription(), createpartDTO.getPartType().toUpperCase());

            part.setUserCreate(user);
            part.setUserUpdate(user);

            boolean checkPart = IPartService.createPart(part);

            PartResponse partResponse = new PartResponse(part);

            if (checkPart) {
                responseModel.setMessage("Create part successfully");
                responseModel.setResponseData(partResponse);

            } else {
                exceptionResponseModel.setMessage("Create part fail: The part name is already exist");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            exceptionResponseModel.setMessage("Create part fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{partId:.+}/uploadfile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> uploadFilePart(@PathVariable UUID partId, @ModelAttribute UploadMultiFileDTO uploadMultiFileDTO) {
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();

            Part part = IPartService.getPartToId(partId);
            Arrays.asList(uploadMultiFileDTO.getContentData()).stream().forEach(file -> {
                String filename = IFileStorageService.nameFile(file);
                if (IPartService.checkFilePart(part)) {
                    boolean existed = IFileStorageService.delete(part.getContentData());
                }
                part.setContentType(GetExtension.typeFile(filename));
                part.setContentData(filename);

                part.setUserUpdate(user);
                part.setUpdateAt(LocalDateTime.now());
                IFileStorageService.save(file, filename);
            });

            IPartService.updatePart(part);

            PartResponse partResponse = new PartResponse(part);

            responseModel.setMessage("Upload file part successfully");
            responseModel.setResponseData(partResponse);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create part fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{partId:.+}/uploadText")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> uploadTextPart(@PathVariable UUID partId, @RequestBody UploadTextDTO uploadTextDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {

            Part part = IPartService.getPartToId(partId);
            part.setContentData(uploadTextDTO.getContentData());
            part.setContentType(uploadTextDTO.getContentType());

            User user = IUserService.currentUser();

            part.setUserUpdate(user);
            part.setUpdateAt(LocalDateTime.now());

            IPartService.updatePart(part);

            PartResponse partResponse = new PartResponse(part);

            responseModel.setMessage("Upload file part successfully");
            responseModel.setResponseData(partResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Upload file part fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/listPart")
    public ResponseEntity<ResponseModel> getAllPart() {
        ResponseModel responseModel = new ResponseModel();

        try {
            List<Part> partList = IPartService.getAllPart();

            List<PartResponse> partResponseList = new ArrayList<>();

            for (Part part : partList) {
                PartResponse partResponse = new PartResponse(part);
                partResponseList.add(partResponse);
            }

            responseModel.setMessage("Show part successfully");
            responseModel.setResponseData(partResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show all part: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> deletePart(@PathVariable UUID partId) {
        ResponseModel responseModel = new ResponseModel();

        try {

            Part part = IPartService.getPartToId(partId);

            if (part.getContentData() != null) {
                IFileStorageService.delete(part.getContentData());
            }

            IPartService.deletePart(part);

            responseModel.setMessage("Delete part successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Delete part: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> updatePart(@PathVariable UUID partId, @RequestBody UpdatePartDTO updatePartDTO) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            User user = IUserService.currentUser();

            Part part = IPartService.getPartToId(partId);

            String partNameOld = part.getPartName();

            part.setPartName(updatePartDTO.getPartName());
            part.setPartDescription(updatePartDTO.getPartDiscription());
            part.setPartType(updatePartDTO.getPartType());
            part.setUserUpdate(user);
            part.setUpdateAt(LocalDateTime.now());


            boolean checkPart = IPartService.checkPart(part);
            if (partNameOld.equals(updatePartDTO.getPartName())) {
                checkPart = true;
            }
            PartResponse partResponse = new PartResponse(part);

            if (checkPart) {
                IPartService.updatePart(part);

                responseModel.setMessage("Update part successfully");
                responseModel.setResponseData(partResponse);

            } else {
                exceptionResponseModel.setMessage("Create part fail: The part name is already exist");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            exceptionResponseModel.setMessage("Create part fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @GetMapping(value = "/{partId:.+}/content")
    public ResponseEntity<ResponseModel> getPartToId(@PathVariable UUID partId) {
        ResponseModel responseModel = new ResponseModel();

        try {
            Part part = IPartService.getPartToId(partId);
            JSONObject partResponse = new JSONObject();
            partResponse.put("partId", part.getPartId());
            partResponse.put("partName", part.getPartName());
            partResponse.put("partType", part.getPartType());
            partResponse.put("partDescription", part.getPartDescription());

            responseModel.setMessage("Show information part successfully");
            responseModel.setResponseData(partResponse);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show information part: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }
}
