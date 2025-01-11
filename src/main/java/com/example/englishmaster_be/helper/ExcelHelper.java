package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.common.constant.excel.ExcelQuestionConstant;
import com.example.englishmaster_be.common.constant.excel.ExcelTopicConstant;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.exception.template.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelHelper {

    public static boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();

        String fileName = file.getOriginalFilename();

        return contentType == null ||
                (!contentType.equals("application/vnd.ms-excel") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
                (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")));
    }


    public static ExcelQuestionContentResponse collectQuestionContentPart1234567With(Sheet sheet, Integer iRowAudioOrQuestionContentPath, Integer iRowImagePath, Integer iRowTotalScore, int part) {

        if(!List.of(1, 2, 3, 4, 5, 6, 7).contains(part))
            throw new BadRequestException("Part must be in numbers 1, 2, 3, 4, 5, 6, 7 !");

        String audioPathOrQuestionContent = null;
        String imagePath = null;
        int totalScore = 0;

        if(iRowAudioOrQuestionContentPath != null){

            Row audioOrQuestionContentRow = sheet.getRow(iRowAudioOrQuestionContentPath);

            if(List.of(1, 2, 3, 4).contains(part)){
                if(audioOrQuestionContentRow == null || !getStringCellValue(audioOrQuestionContentRow, 0).equalsIgnoreCase(ExcelQuestionConstant.Audio.getHeaderName()))
                    throw new BadRequestException(String.format("'%s' tag is required in Sheet %s, You can fill is blank content audio !", ExcelQuestionConstant.Audio.getHeaderName(), sheet.getSheetName()));
            }
            else{
                if(audioOrQuestionContentRow == null || !getStringCellValue(audioOrQuestionContentRow, 0).equalsIgnoreCase(ExcelQuestionConstant.Question_Content.getHeaderName()))
                    throw new BadRequestException(String.format("'%s' tag is required in Sheet %s, You can fill is blank question content !", ExcelQuestionConstant.Question_Content.getHeaderName(), sheet.getSheetName()));
            }

            audioPathOrQuestionContent = getStringCellValue(audioOrQuestionContentRow, 1);
        }

        if(iRowImagePath != null){

            Row imageRow = sheet.getRow(iRowImagePath);

            if(imageRow == null || !getStringCellValue(imageRow, 0).equalsIgnoreCase(ExcelQuestionConstant.Image.getHeaderName()))
                throw new BadRequestException(String.format("'%s' tag is required in Sheet %s, You can fill is blank content image !", ExcelQuestionConstant.Image.getHeaderName(), sheet.getSheetName()));

            imagePath = getStringCellValue(imageRow, 1);
        }

        if(iRowTotalScore != null){

            Row scoreRow = sheet.getRow(iRowTotalScore);

            if(scoreRow == null || !getStringCellValue(scoreRow, 0).equalsIgnoreCase(ExcelQuestionConstant.Score.getHeaderName()))
                throw new BadRequestException(String.format("'%s' tag is required in Sheet %s, You can fill is blank content score !", ExcelQuestionConstant.Score.getHeaderName(), sheet.getSheetName()));

            Cell cellTotalScore = scoreRow.getCell(1);

            totalScore = cellTotalScore != null && cellTotalScore.getCellType().equals(CellType.NUMERIC)
                    ? (int) cellTotalScore.getNumericCellValue() : 0;
        }

        ExcelQuestionContentResponse excelQuestionContentResponse = ExcelQuestionContentResponse.builder()
                .imagePath(imagePath)
                .totalScore(totalScore)
                .build();

        if(List.of(1, 2, 3, 4).contains(part))
            excelQuestionContentResponse.setAudioPath(audioPathOrQuestionContent);
        else if(List.of(6, 7).contains(part))
            excelQuestionContentResponse.setQuestionContent(audioPathOrQuestionContent);

        return excelQuestionContentResponse;
    }

    public static ExcelTopicContentResponse collectTopicContentWith(Sheet sheet){

        int jColHeader = 0;

        int jColBody = 1;

        int iRowExamName = 0;
        checkCellTopicHeader(sheet, iRowExamName, jColHeader, ExcelTopicConstant.Name);

        String topicName = getCellValueAsString(sheet, iRowExamName, jColBody);

        int iRowImagePath = 1;
        checkCellTopicHeader(sheet, iRowImagePath, jColHeader, ExcelTopicConstant.Image_Path);

        String imagePath = getCellValueAsString(sheet, iRowImagePath, jColBody);

        int iRowDescription = 2;
        checkCellTopicHeader(sheet, iRowDescription, jColHeader, ExcelTopicConstant.Description);

        String description = getCellValueAsString(sheet, iRowDescription, jColBody);

        int iRowExamType = 3;
        checkCellTopicHeader(sheet, iRowExamType, jColHeader, ExcelTopicConstant.Type);

        String topicType = getCellValueAsString(sheet, iRowExamType, jColBody);

        int iRowWorkTime = 4;
        checkCellTopicHeader(sheet, iRowWorkTime, jColHeader, ExcelTopicConstant.Work_Time);

        String workTimeString = getCellValueAsString(sheet, iRowWorkTime, jColBody);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");

        LocalDateTime localDateTime = LocalDateTime.parse(workTimeString, dateTimeFormatter);

        Time workTime = Time.valueOf(localDateTime.toLocalTime());

        int iRowNumberQuestion = 5;
        checkCellTopicHeader(sheet, iRowNumberQuestion, jColHeader, ExcelTopicConstant.Number_Question);

        int numberQuestion = (int) sheet.getRow(iRowNumberQuestion).getCell(jColBody).getNumericCellValue();

        int iRowPackName = 6;
        checkCellTopicHeader(sheet, iRowPackName, jColHeader, ExcelTopicConstant.Exam_Set);

        String packName = getCellValueAsString(sheet, iRowPackName, jColBody);

        int iRowPart = 7;
        checkCellTopicHeader(sheet, iRowPart, jColHeader, ExcelTopicConstant.Part);

        String part = getCellValueAsString(sheet, iRowPart, jColBody).replaceAll("\n", "");

        List<String> partsList = Arrays.stream(part.split(",")).map(String::trim).toList();

        List<String> partNamesList = new ArrayList<>();
        List<String> partTypesList = new ArrayList<>();

        partsList.forEach(partItem -> {
            String[] partItemSplit = partItem.split(":");

            partNamesList.add(partItemSplit[0].trim());
            partTypesList.add(partItemSplit[1].trim());
        });


        return ExcelTopicContentResponse.builder()
                .topicName(topicName)
                .topicImage(imagePath)
                .topicDescription(description)
                .topicType(topicType)
                .workTime(workTime)
                .numberQuestion(numberQuestion)
                .packName(packName)
                .partNamesList(partNamesList)
                .partTypesList(partTypesList)
                .build();

    }

    public static void checkCellTopicHeader(Sheet sheet, int iRowHeader, int jColOfRow, ExcelTopicConstant excelTopicConstant){

        Row rowInSheet = sheet.getRow(iRowHeader);

        if(rowInSheet == null)
            throw new BadRequestException(String.format("Row %d at Sheet %s does not exist", iRowHeader, sheet.getSheetName()));

        Cell cellAtCol = rowInSheet.getCell(jColOfRow);

        if(cellAtCol == null || !getStringCellValue(rowInSheet, jColOfRow).equalsIgnoreCase(excelTopicConstant.getHeaderName()))
            throw new BadRequestException(
                    String.format(
                            "'%s' of cell %d header at row %d is required in Sheet %s",
                            excelTopicConstant.getHeaderName(),
                            jColOfRow,
                            iRowHeader,
                            sheet.getSheetName()
                    )
            );

    }

    public static void checkHeaderTableQuestionPart1234567With(Sheet sheet, int iRowHeaderTable, int part){

        if(!List.of(1, 2, 3, 4, 5, 6, 7).contains(part))
            throw new BadRequestException("Part must be in numbers 1, 2, 3, 4, 5, 6, 7 !");

        Row rowHeaderTable = sheet.getRow(iRowHeaderTable);

        if(rowHeaderTable == null)
            throw new BadRequestException(String.format("Row %d at Sheet %s does not exist", iRowHeaderTable, sheet.getSheetName()));

        int jColSTT_CellOnHeader = 0;

        checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColSTT_CellOnHeader, ExcelQuestionConstant.STT);

        if(List.of(3, 4, 5, 6, 7).contains(part)){

            int jColQuestionContent_CellOnHeader = 1;

            if(List.of(3, 4, 5).contains(part))
                checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColQuestionContent_CellOnHeader, ExcelQuestionConstant.Question_Content);
            else
                checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColQuestionContent_CellOnHeader, ExcelQuestionConstant.Question_Content_Child);

            int jColA_CellOnHeader = 2;

            checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColA_CellOnHeader, ExcelQuestionConstant.A);

            int jColB_CellOnHeader = 3;

            checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColB_CellOnHeader, ExcelQuestionConstant.B);

            int jColC_CellOnHeader = 4;

            checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColC_CellOnHeader, ExcelQuestionConstant.C);

            int jColD_CellOnHeader = 5;

            checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColD_CellOnHeader, ExcelQuestionConstant.D);

        }

        int jColResultHeaderTable = 6;

        if(List.of(1, 2).contains(part))
            jColResultHeaderTable = 1;

        checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColResultHeaderTable, ExcelQuestionConstant.Result);

        int jColScoreHeaderTable = 7;

        if(List.of(1, 2).contains(part))
            jColScoreHeaderTable = 2;

        checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColScoreHeaderTable, ExcelQuestionConstant.Score);

        if(part == 1){

            int jColImageHeaderTable = 3;

            checkCellQuestionHeaderTable(sheet, rowHeaderTable, iRowHeaderTable, jColImageHeaderTable, ExcelQuestionConstant.Image);
        }
    }

    private static void checkCellQuestionHeaderTable(Sheet sheet, Row rowHeaderTable, int iRowHeaderTable, int jColHeaderTable, ExcelQuestionConstant cellHeaderValue){

        Cell cellOnHeaderTable = rowHeaderTable.getCell(jColHeaderTable);

        if(cellOnHeaderTable == null || !getStringCellValue(rowHeaderTable, jColHeaderTable).equalsIgnoreCase(cellHeaderValue.getHeaderName()))
            throw new BadRequestException(
                    String.format(
                            "'%s' of cell %d in header table row %d is required in Sheet %s",
                            cellHeaderValue.getHeaderName(),
                            jColHeaderTable,
                            iRowHeaderTable,
                            sheet.getSheetName()
                    )
            );
    }


    public static String getStringCellValue(Row row, int cellIndex) {

        Cell cell = row.getCell(cellIndex);

        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell))
                    yield cell.getDateCellValue().toString();
                else yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }

    public static int getNumericCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null) ? (int) cell.getNumericCellValue() : 0;
    }

    public static List<AnswerBasicRequest> processAnswers(String optionA, String optionB, String optionC, String optionD, String result) {
        List<AnswerBasicRequest> listAnswerDTO = new ArrayList<>();
        listAnswerDTO.add(createAnswerDTO(optionA, result));
        listAnswerDTO.add(createAnswerDTO(optionB, result));
        listAnswerDTO.add(createAnswerDTO(optionC, result));
        listAnswerDTO.add(createAnswerDTO(optionD, result));
        return listAnswerDTO;
    }

    public static AnswerBasicRequest createAnswerDTO(String option, String result) {
        AnswerBasicRequest answerDTO = new AnswerBasicRequest();
        answerDTO.setAnswerContent(option);
        answerDTO.setCorrectAnswer(option != null && option.equalsIgnoreCase(result));
        return answerDTO;
    }


    public static String getStringCellValue(Cell cell) {

        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
    }

    public static double getNumericCellValue(Cell cell) {

        if (cell == null) return 0;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    public static String getCellValueAsString(Sheet sheet, int rowIndex, int cellIndex) {

        Row row = sheet.getRow(rowIndex);

        if (row == null) return "";

        Cell cell = row.getCell(cellIndex);

        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    public static int getIntCellValue(Sheet sheet, int rowIndex, int cellIndex) {

        String value = getCellValueAsString(sheet, rowIndex, cellIndex);

        try {
            return (int) Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format at row " + (rowIndex + 1) + ", column " + (cellIndex + 1));
        }
    }
}
