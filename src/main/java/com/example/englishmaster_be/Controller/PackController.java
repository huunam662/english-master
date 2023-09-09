package com.example.englishmaster_be.Controller;


import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createPack(@RequestParam String packName ){
        ResponseModel responseModel = new ResponseModel();

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
                responseModel.setStatus("success");
            }
            else {
                responseModel.setMessage("Create pack fail: The pack name is already exist");
                responseModel.setStatus("fail");
            }

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Create pack fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
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
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Show list pack fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
    }
}
