package com.example.englishmaster_be.domain.part.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.mapper.PartMapper;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.shared.upload_file.dto.request.UploadMultipleFileRequest;
import com.example.englishmaster_be.domain.part.dto.request.PartSaveContentRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @DefaultMessage("Create part successfully")
    public PartResponse createPart(
            @RequestBody PartRequest partRequest
    ) {

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }


    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Update part successfully")
    public PartResponse updatePart(
            @PathVariable UUID partId,
            @RequestBody PartRequest partRequest
    ) {

        partRequest.setPartId(partId);

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload file content part successfully")
    public PartResponse uploadFilePart(
            @PathVariable UUID partId,
            @RequestPart("contentData") MultipartFile contentData
    ) {

        PartEntity part = partService.uploadFilePart(partId, contentData);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/uploadText")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload file part content successfully")
    public PartResponse uploadTextPart(
            @PathVariable UUID partId,
            @RequestBody PartSaveContentRequest uploadTextRequest
    ) {

        PartEntity part = partService.uploadTextPart(partId, uploadTextRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @GetMapping(value = "/listPart")
    @DefaultMessage("Show part successfully")
    public List<PartResponse> getAllPart() {

        List<PartEntity> partEntityList = partService.getListPart();

        return PartMapper.INSTANCE.toPartResponseList(partEntityList);
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete part successfully")
    public void deletePart(@PathVariable UUID partId) {

        partService.deletePart(partId);
    }


    @GetMapping(value = "/{partId:.+}/content")
    @DefaultMessage("Show information part successfully")
    public PartResponse getPartToId(
            @PathVariable UUID partId
    ) {

        PartEntity partEntity = partService.getPartToId(partId);

        return PartMapper.INSTANCE.toPartResponse(partEntity);
    }
}
