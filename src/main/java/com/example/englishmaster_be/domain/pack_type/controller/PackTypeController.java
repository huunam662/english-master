package com.example.englishmaster_be.domain.pack_type.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.pack_type.dto.request.CreatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.PackTypeFilterRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.UpdatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeKeyResponse;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.domain.pack_type.service.IPackTypeService;
import com.example.englishmaster_be.mapper.PackTypeMapper;
import com.example.englishmaster_be.model.pack_type.PackTypeEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Pack Type")
@RestController
@RequestMapping("/pack-type")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackTypeController {

    IPackTypeService packTypeService;

    @GetMapping("/{packTypeId:.+}")
    @DefaultMessage("Load pack type successful.")
    public PackTypeResponse getPackTypeById(@PathVariable("packTypeId") UUID packTypeId) {

        PackTypeEntity packType = packTypeService.getPackTypeById(packTypeId);

        return PackTypeMapper.INSTANCE.toPackTypeResponse(packType);
    }

    @GetMapping
    @DefaultMessage("Load pack type list successful.")
    public FilterResponse<?> getAllPackTypes(@ModelAttribute @Valid PackTypeFilterRequest request) {

        return packTypeService.filterPackTypes(request);
    }

    @PostMapping
    @DefaultMessage("Save resource successful.")
    public PackTypeKeyResponse createPackType(@Valid @RequestBody CreatePackTypeRequest request){

        return packTypeService.createPackType(request);
    }

    @PatchMapping
    @DefaultMessage("Save resource successful.")
    public PackTypeKeyResponse updatePackType(@RequestBody @Valid UpdatePackTypeRequest request){

        return packTypeService.updatePackType(request);
    }

    @DeleteMapping("/{packTypeId:.+}")
    @DefaultMessage("Delete resource successful.")
    public void deletePackType(@PathVariable("packTypeId") UUID packTypeId) {

        packTypeService.deletePackTypeById(packTypeId);
    }
}
