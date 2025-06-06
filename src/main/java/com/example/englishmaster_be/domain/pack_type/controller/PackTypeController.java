package com.example.englishmaster_be.domain.pack_type.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.pack_type.dto.request.CreatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.PackTypeFilterRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.UpdatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeKeyResponse;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.domain.pack_type.service.IPackTypeService;
import com.example.englishmaster_be.domain.pack.mapper.PackMapper;
import com.example.englishmaster_be.domain.pack_type.mapper.PackTypeMapper;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pack Type")
@RestController
@RequestMapping("/pack-type")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackTypeController {

    IPackTypeService packTypeService;

    IPackService packService;

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

    @GetMapping("/list")
    @DefaultMessage("Load pack type list successful.")
    public List<PackTypeResponse> getAllPackTypes() {

        List<PackTypeEntity> packTypes = packTypeService.getAllPackTypes();

        return PackTypeMapper.INSTANCE.toPackTypeResponseList(packTypes);
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

    @GetMapping("/{packTypeId}/list-pack-topic")
    @DefaultMessage("Load pack topic by pack type successful.")
    @Operation(
            summary = "Get all pack by pack type id.",
            description = "Get all pack by pack type id."
    )
    public List<PackResponse> listPackTopic(@PathVariable("packTypeId") UUID packTypeId) {

        List<PackEntity> packlist = packService.getListPackByPackTypeId(packTypeId);

        return PackMapper.INSTANCE.toPackResponseList(packlist);
    }
}
