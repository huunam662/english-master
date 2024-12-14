package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Model.Request.Part.PartRequest;
import com.example.englishmaster_be.Model.Request.UploadMultiFileRequest;
import com.example.englishmaster_be.Model.Request.UploadTextRequest;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.PackEntity;
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
    @MessageResponse("Create PartEntity successfully")
    public PartResponse createPart(
            @ModelAttribute PartRequest partRequest
    ) {

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update PartEntity successfully")
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
    @MessageResponse("Upload file PartEntity successfully")
    public PartResponse uploadFilePart(
            @PathVariable UUID partId,
            @ModelAttribute UploadMultiFileRequest uploadMultiFileRequest
    ) {

        PartEntity part = partService.uploadFilePart(partId, uploadMultiFileRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/uploadText")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Upload file PartEntity successfully")
    public PartResponse uploadTextPart(@PathVariable UUID partId, @RequestBody UploadTextRequest uploadTextRequest) {

        PartEntity part = partService.uploadTextPart(partId, uploadTextRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @GetMapping(value = "/listPart")
    @MessageResponse("Show PartEntity successfully")
    public List<PartResponse> getAllPart() {

        List<PartEntity> partEntityList = partService.getListPart();

        return PartMapper.INSTANCE.toPartResponseList(partEntityList);
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete PartEntity successfully")
    public void deletePart(@PathVariable UUID partId) {

        partService.deletePart(partId);
    }


    @GetMapping(value = "/{partId:.+}/content")
    @MessageResponse("Show information PartEntity successfully")
    public PartResponse getPartToId(
            @PathVariable UUID partId
    ) {

        PartEntity partEntity = partService.getPartToId(partId);

        return PartMapper.INSTANCE.toPartResponse(partEntity);
    }
}
