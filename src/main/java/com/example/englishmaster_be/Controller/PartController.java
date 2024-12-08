package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.DTO.Part.SavePartDTO;
import com.example.englishmaster_be.DTO.Part.UpdatePartDTO;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.Service.*;
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
@RequestMapping("/api/part")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartController {

    IPartService partService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create Part successfully")
    public PartResponse createPart(
            @RequestBody SavePartDTO savePartDTO
    ) {

        return partService.savePart(savePartDTO);
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update Part successfully")
    public PartResponse updatePart(
            @PathVariable UUID partId,
            @RequestBody UpdatePartDTO updatePartDTO
    ) {

        updatePartDTO.setPartId(partId);

        return partService.savePart(updatePartDTO);
    }

    @PutMapping(value = "/{partId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Upload file Part successfully")
    public PartResponse uploadFilePart(
            @PathVariable UUID partId,
            @ModelAttribute UploadMultiFileDTO uploadMultiFileDTO
    ) {

        return partService.uploadFilePart(partId, uploadMultiFileDTO);
    }

    @PutMapping(value = "/{partId:.+}/uploadText")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Upload file Part successfully")
    public PartResponse uploadTextPart(@PathVariable UUID partId, @RequestBody UploadTextDTO uploadTextDTO) {

        return partService.uploadTextPart(partId, uploadTextDTO);
    }

    @GetMapping(value = "/listPart")
    @MessageResponse("Show Part successfully")
    public List<PartResponse> getAllPart() {

        return partService.getListPart();
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete Part successfully")
    public void deletePart(@PathVariable UUID partId) {

        partService.deletePart(partId);
    }


    @GetMapping(value = "/{partId:.+}/content")
    @MessageResponse("Show information Part successfully")
    public PartResponse getPartToId(
            @PathVariable UUID partId
    ) {

        Part partEntity = partService.getPartToId(partId);

        return PartMapper.INSTANCE.partEntityToPartResponse(partEntity);
    }
}
