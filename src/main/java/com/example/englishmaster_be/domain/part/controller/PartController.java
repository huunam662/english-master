package com.example.englishmaster_be.domain.part.controller;


import com.example.englishmaster_be.domain.part.dto.request.CreatePartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.request.EditPartQuestionsAnswersRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartKeyResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartQuestionResponse;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    @PostMapping("/questions-answers/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create questions and answers for part.",
            description = "Create questions and answers for part."
    )
    public PartKeyResponse partQuestionsAnswersCreate(
            @RequestBody @Valid CreatePartQuestionsAnswersRequest request
    ){

        return partService.createPartAndQuestionsAnswers(request);
    }

    @PutMapping("/questions-answers/edit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Edit questions and answers for part.",
            description = "Edit questions and answers for part."
    )
    public PartKeyResponse partQuestionsAnswersEdit(
            @RequestBody @Valid EditPartQuestionsAnswersRequest request
    ){
        return partService.editPartAndQuestionsAnswers(request);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PartResponse createPart(
            @RequestBody PartRequest partRequest
    ) {

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public PartResponse updatePart(
            @PathVariable("partId") UUID partId,
            @RequestBody PartRequest partRequest
    ) {

        partRequest.setPartId(partId);

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @GetMapping(value = "/{partId:.+}/questions-answers")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get question answers of part by partId",
            description = "Get question answers of part by partId"
    )
    public PartQuestionResponse getPartQuestionsAnswers(
            @PathVariable("partId") UUID partId
    ) {

        PartEntity part = partService.getPartQuestionsAnswers(partId);

        return PartMapper.INSTANCE.toPartQuestionResponse(part);
    }

    @GetMapping(value = "/listPart")
    public List<PartResponse> getAllPart() {

        List<PartEntity> partEntityList = partService.getListPart();

        return PartMapper.INSTANCE.toPartResponseList(partEntityList);
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void deletePart(@PathVariable("partId") UUID partId) {

        partService.deletePart(partId);
    }

}
