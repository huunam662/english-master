package com.example.englishmaster_be.Controller;


import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.PackMapper;
import com.example.englishmaster_be.Model.Request.Pack.PackRequest;
import com.example.englishmaster_be.Model.Response.PackResponse;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.PackEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pack")
@RestController
@RequestMapping("/pack")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackController {

    IPackService packService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create pack successfully")
    public PackResponse createPack(@RequestBody PackRequest packRequest) {

        PackEntity pack = packService.createPack(packRequest);

        return PackMapper.INSTANCE.toPackResponse(pack);
    }

    @GetMapping(value = "/listPack")
    @MessageResponse("Show list pack successfully")
    public List<PackResponse> getListPack(){

        List<PackEntity> packEntityList = packService.getListPack();

        return PackMapper.INSTANCE.toPackResponseList(packEntityList);
    }
}
