package com.example.englishmaster_be.controller;


import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pack")
public class PackController {

    @Autowired
    private IUserService IUserService;
    @Autowired
    private IPackService IPackService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createPack(@RequestParam String packName ){
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            User user = IUserService.currentUser();

            Pack pack = new Pack();
            pack.setPackName(packName);
            pack.setUserCreate(user);
            pack.setUserUpdate(user);

            boolean checkPack = IPackService.createPack(pack);

            PackResponse packResponse = new PackResponse(pack);

            if (checkPack){
                responseModel.setMessage("Create pack successfully");
                responseModel.setResponseData(packResponse);

            }
            else {
                exceptionResponseModel.setMessage("Create pack fail: The pack name is already exist");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            exceptionResponseModel.setMessage("Create pack fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }

    @GetMapping(value = "/listPack")
    public ResponseEntity<ResponseModel> getListPack(){
        ResponseModel responseModel = new ResponseModel();

        try {
            List<PackResponse> packResponseList = new ArrayList<>();

            for(Pack pack :IPackService.getAllPack()){
                PackResponse packResponse = new PackResponse(pack);
                packResponseList.add(packResponse);
            }
            responseModel.setMessage("Show list pack successfully");
            responseModel.setResponseData(packResponseList);


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list pack fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);}
    }
}
