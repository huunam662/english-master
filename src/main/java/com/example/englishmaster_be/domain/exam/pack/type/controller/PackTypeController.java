package com.example.englishmaster_be.domain.exam.pack.type.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackRes;
import com.example.englishmaster_be.domain.exam.pack.pack.service.IPackService;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.CreatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.UpdatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeKeyRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypePageRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.view.IPackTypePageView;
import com.example.englishmaster_be.domain.exam.pack.type.service.IPackTypeService;
import com.example.englishmaster_be.domain.exam.pack.pack.mapper.PackMapper;
import com.example.englishmaster_be.domain.exam.pack.type.mapper.PackTypeMapper;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pack Type")
@RestController
@RequestMapping("/pack-type")
public class PackTypeController {

    private final IPackTypeService packTypeService;

    private final IPackService packService;

    public PackTypeController(IPackTypeService packTypeService, IPackService packService) {
        this.packTypeService = packTypeService;
        this.packService = packService;
    }

    @GetMapping("/{packTypeId:.+}")
    public PackTypeRes getPackTypeById(@PathVariable("packTypeId") UUID packTypeId) {

        PackTypeEntity packType = packTypeService.getPackTypeById(packTypeId);

        return PackTypeMapper.INSTANCE.toPackTypeResponse(packType);
    }

    @GetMapping("/page")
    public PageInfoRes<PackTypePageRes> getAllPackTypes(@ModelAttribute @Valid PageOptionsReq optionsReq) {
        Page<IPackTypePageView> packTypePage = packTypeService.getPagePackType(optionsReq);
        List<PackTypePageRes> packTypeResList = PackTypeMapper.INSTANCE.toPackTypePageResList(packTypePage.getContent());
        Page<PackTypePageRes> packTypePageRes = new PageImpl<>(packTypeResList, packTypePage.getPageable(), packTypePage.getTotalElements());
        return new PageInfoRes<>(packTypePageRes);
    }

    @GetMapping("/list")
    public List<PackTypeRes> getAllPackTypes() {

        List<PackTypeEntity> packTypes = packTypeService.getAllPackTypes();

        return PackTypeMapper.INSTANCE.toPackTypeResponseList(packTypes);
    }

    @PostMapping
    public PackTypeKeyRes createPackType(@Valid @RequestBody CreatePackTypeReq request){

        return packTypeService.createPackType(request);
    }

    @PatchMapping
    public PackTypeKeyRes updatePackType(@RequestBody @Valid UpdatePackTypeReq request){

        return packTypeService.updatePackType(request);
    }

    @DeleteMapping("/{packTypeId:.+}")
    public void deletePackType(@PathVariable("packTypeId") UUID packTypeId) {

        packTypeService.deletePackTypeById(packTypeId);
    }

    @GetMapping("/{packTypeId}/list-pack-topic")
    @Operation(
            summary = "Get all pack by pack type id.",
            description = "Get all pack by pack type id."
    )
    public List<PackRes> listPackTopic(@PathVariable("packTypeId") UUID packTypeId) {

        List<PackEntity> packlist = packService.getListPackByPackTypeId(packTypeId);

        return PackMapper.INSTANCE.toPackResponseList(packlist);
    }
}
