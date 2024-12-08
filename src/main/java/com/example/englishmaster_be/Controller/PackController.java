package com.example.englishmaster_be.Controller;


import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Pack.PackDTO;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pack")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackController {

    IPackService packService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create pack successfully")
    public PackResponse createPack(@RequestParam PackDTO packDTO) {

        return packService.createPack(packDTO);
    }

    @GetMapping(value = "/listPack")
    @MessageResponse("Show list pack successfully")
    public List<PackResponse> getListPack(){

        return packService.getListPack();
    }
}
