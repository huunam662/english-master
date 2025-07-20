package com.example.englishmaster_be.domain.exam.pack.pack.controller;



import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.exam.pack.pack.mapper.PackMapper;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.pack.pack.service.IPackService;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackPageRes;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.req.PackReq;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.res.PackRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Pack")
@RestController
@RequestMapping("/pack")
public class PackController {

    private final IPackService packService;

    public PackController(IPackService packService) {
        this.packService = packService;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PackRes createPack(@RequestBody PackReq packRequest) {

        PackEntity pack = packService.createPack(packRequest);

        return PackMapper.INSTANCE.toPackResponse(pack);
    }

    @GetMapping(value = "/listPack")
    public List<PackRes> getListPack(){

        List<PackEntity> packEntityList = packService.getListPack();

        return PackMapper.INSTANCE.toPackResponseList(packEntityList);
    }

    @GetMapping("/page")
    public PageInfoRes<PackPageRes> getPagePack(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<IPackPageView> packPage = packService.getPagePack(optionsReq);
        List<PackPageRes> packPageResList = PackMapper.INSTANCE.toPackPageResList(packPage.getContent());
        Page<PackPageRes> packPageRes = new PageImpl<>(packPageResList, packPage.getPageable(), packPage.getTotalElements());
        return new PageInfoRes<>(packPageRes);
    }

}
