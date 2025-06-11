package com.example.englishmaster_be.domain.excel.controller.exp;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.constant.ResourceTypeLoad;
import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.domain.excel.service.exp.IExcelExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.UUID;

@Tag(name = "Excel Export")
@RestController
@RequestMapping("/excel/export")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelExportController {

    IExcelExportService excelExportService;

    @GetMapping("/topic/type")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Export topic successful.")
    @Operation(
            summary = "Export to excel for topic.",
            description = "Export to excel for topic."
    )
    public void exportTopicByType(
            @RequestParam("exportType") TopicType topicType,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=meu-english_topic-"+topicType.getType()+"-data.zip");

        InputStream inputStream = excelExportService.exportTopicByType(topicType);
        inputStream.transferTo(response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/topic/all")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Export topic successful.")
    @Operation(
            summary = "Export to excel for topic.",
            description = "Export to excel for topic."
    )
    public void exportAllTopic(
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=meu-english_topic-all-data.zip");

        InputStream inputStream = excelExportService.exportAllTopic();
        inputStream.transferTo(response.getOutputStream());
        response.flushBuffer();
    }

    @GetMapping("/topic/{topicId:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Export topic successful.")
    @Operation(
            summary = "Export to excel for topic.",
            description = "Export to excel for topic."
    )
    public void exportTopicById(
            @PathVariable("topicId") UUID topicId,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=meu-english_topic-data.zip");

        InputStream inputStream = excelExportService.exportTopicById(topicId);
        inputStream.transferTo(response.getOutputStream());
        response.flushBuffer();
    }

}
