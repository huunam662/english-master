package com.example.englishmaster_be.domain.exam.part.controller;


<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/part/controller/PartController.java
import com.example.englishmaster_be.domain.exam.part.dto.req.EditPartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.req.PartReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartQuestionRes;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartRes;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.part.service.IPartService;
import com.example.englishmaster_be.domain.exam.part.dto.req.CreatePartQuestionsAnswersReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartKeyRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
=======
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
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/part/controller/PartController.java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "Part")
@RestController
@RequestMapping("/part")
public class PartController {

    private final IPartService partService;

    public PartController(IPartService partService) {
        this.partService = partService;
    }

    @PostMapping("/questions-answers/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create questions and answers for part.",
            description = "Create questions and answers for part."
    )
    public PartKeyRes partQuestionsAnswersCreate(
            @RequestBody @Valid CreatePartQuestionsAnswersReq request
    ){

        return partService.createPartAndQuestionsAnswers(request);
    }

    @PutMapping("/questions-answers/edit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Edit questions and answers for part.",
            description = "Edit questions and answers for part."
    )
    public PartKeyRes partQuestionsAnswersEdit(
            @RequestBody @Valid EditPartQuestionsAnswersReq request
    ){
        return partService.editPartAndQuestionsAnswers(request);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/part/controller/PartController.java
    public PartRes createPart(
            @RequestBody PartReq partRequest
=======
    public PartResponse createPart(
            @RequestBody PartRequest partRequest
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/part/controller/PartController.java
    ) {

        PartEntity part = partService.savePart(partRequest);

        return PartMapper.INSTANCE.toPartResponse(part);
    }

    @PutMapping(value = "/{partId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/part/controller/PartController.java
    public PartRes updatePart(
=======
    public PartResponse updatePart(
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/part/controller/PartController.java
            @PathVariable("partId") UUID partId,
            @RequestBody PartReq partRequest
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
    public PartQuestionRes getPartQuestionsAnswers(
            @PathVariable("partId") UUID partId
    ) {

        PartEntity part = partService.getPartQuestionsAnswers(partId);

        return PartMapper.INSTANCE.toPartQuestionResponse(part);
    }

    @GetMapping(value = "/listPart")
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/part/controller/PartController.java
    public List<PartRes> getAllPart() {
=======
    public List<PartResponse> getAllPart() {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/part/controller/PartController.java

        List<PartEntity> partEntityList = partService.getListPart();

        return PartMapper.INSTANCE.toPartResponseList(partEntityList);
    }

    @DeleteMapping(value = "/{partId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void deletePart(@PathVariable("partId") UUID partId) {

        partService.deletePart(partId);
    }

}
