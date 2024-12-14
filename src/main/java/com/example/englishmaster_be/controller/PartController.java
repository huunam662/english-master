package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.model.request.Part.PartRequest;
import com.example.englishmaster_be.model.request.UploadMultiFileRequest;
import com.example.englishmaster_be.model.request.UploadTextRequest;
import com.example.englishmaster_be.model.response.PartResponse;
import com.example.englishmaster_be.service.*;
import com.example.englishmaster_be.entity.PartEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@Tag(name = "Part")
@RestController
@RequestMapping("/part")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartController {

    IPartService partService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Create PartEntity successfully")
    public PartResponse createPart(
            @ModelAttribute PartRequest partRequest
    ) {

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Update PartEntity successfully")
    public PartResponse updatePart(
            @PathVariable UUID partId,
            @ModelAttribute PartRequest partRequest
    ) {

        partRequest.setPartId(partId);

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload file PartEntity successfully")
    public PartResponse uploadFilePart(
            @PathVariable UUID partId,
            @ModelAttribute UploadMultiFileRequest uploadMultiFileRequest
    ) {

        PartEntity part = partService.uploadFilePart(partId, uploadMultiFileRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/uploadText")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload file PartEntity successfully")
    public PartResponse uploadTextPart(@PathVariable UUID partId, @RequestBody UploadTextRequest uploadTextRequest) {

        PartEntity part = partService.uploadTextPart(partId, uploadTextRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @GetMapping(value = "/listPart")
    @DefaultMessage("Show PartEntity successfully")
    public List<PartResponse> getAllPart() {

        List<PartEntity> partEntityList = partService.getListPart();

        return PartMapper.INSTANCE.toPartResponseList(partEntityList);
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete PartEntity successfully")
    public void deletePart(@PathVariable UUID partId) {

        partService.deletePart(partId);
    }


    @GetMapping(value = "/{partId:.+}/content")
    @DefaultMessage("Show information PartEntity successfully")
    public PartResponse getPartToId(
            @PathVariable UUID partId
    ) {

        PartEntity partEntity = partService.getPartToId(partId);

        return PartMapper.INSTANCE.toPartResponse(partEntity);
    }
}
