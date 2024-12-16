package com.example.englishmaster_be.domain.pack.contorller;



import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.mapper.PackMapper;
import com.example.englishmaster_be.domain.pack.dto.request.PackRequest;
import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.model.pack.PackEntity;
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
    @DefaultMessage("Create pack successfully")
    public PackResponse createPack(@RequestBody PackRequest packRequest) {

        PackEntity pack = packService.createPack(packRequest);

        return PackMapper.INSTANCE.toPackResponse(pack);
    }

    @GetMapping(value = "/listPack")
    @DefaultMessage("Show list pack successfully")
    public List<PackResponse> getListPack(){

        List<PackEntity> packEntityList = packService.getListPack();

        return PackMapper.INSTANCE.toPackResponseList(packEntityList);
    }
}
