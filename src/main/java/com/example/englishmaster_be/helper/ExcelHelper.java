package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.common.constant.excel.ExcelHeaderContentConstant;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionContentResponse;
import com.example.englishmaster_be.exception.template.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();

        String fileName = file.getOriginalFilename();

        return contentType == null ||
                (!contentType.equals("application/vnd.ms-excel") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
                (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")));
    }


    public static ExcelQuestionContentResponse collectContentPart1234567With(Sheet sheet, Integer iRowAudioOrQuestionContentPath, Integer iRowImagePath, Integer iRowTotalScore, int part) {

        if(!List.of(1, 2, 3, 4, 5, 6, 7).contains(part))
            throw new BadRequestException("Part must be in numbers 1, 2, 3, 4, 5, 6, 7 !");

        String audioPathOrQuestionContent = null;
        String imagePath = null;
        int totalScore = 0;

        if(iRowAudioOrQuestionContentPath != null){

            Row audioOrQuestionContentRow = sheet.getRow(iRowAudioOrQuestionContentPath);

            if(List.of(1, 2, 3, 4).contains(part)){
                if(audioOrQuestionContentRow == null || !getStringCellValue(audioOrQuestionContentRow, 0).equalsIgnoreCase(ExcelHeaderContentConstant.Audio.getHeaderName()))
                    throw new BadRequestException("'Audio' tag is required in Sheet " + part + ", You can fill is blank content audio !");
            }
            else{
                if(audioOrQuestionContentRow == null || !getStringCellValue(audioOrQuestionContentRow, 0).equalsIgnoreCase(ExcelHeaderContentConstant.Question_Content.getHeaderName()))
                    throw new BadRequestException("'Question Content' tag is required in Sheet " + part + ", You can fill is blank question content !");
            }

            audioPathOrQuestionContent = getStringCellValue(audioOrQuestionContentRow, 1);
        }

        if(iRowImagePath != null){

            Row imageRow = sheet.getRow(iRowImagePath);

            if(imageRow == null || !getStringCellValue(imageRow, 0).equalsIgnoreCase(ExcelHeaderContentConstant.Image.getHeaderName()))
                throw new BadRequestException("'Image' tag is required in Sheet " + part + ", You can fill is blank content image !");

            imagePath = getStringCellValue(imageRow, 1);
        }

        if(iRowTotalScore != null){

            Row scoreRow = sheet.getRow(iRowTotalScore);

            if(scoreRow == null || !getStringCellValue(scoreRow, 0).equalsIgnoreCase(ExcelHeaderContentConstant.Score.getHeaderName()))
                throw new BadRequestException("'Score' tag is required in Sheet " + part + ", You can fill is blank content score !");

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

    public static void checkHeaderTabQuestionPart1234567With(Sheet sheet, int iRowHeaderTable, int part){

        if(!List.of(1, 2, 3, 4, 5, 6, 7).contains(part))
            throw new BadRequestException("Part must be in numbers 1, 2, 3, 4, 5, 6, 7 !");

        Row rowHeaderTable = sheet.getRow(iRowHeaderTable);

        int jColSTT_CellOnHeader = 0;

        checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColSTT_CellOnHeader, part, ExcelHeaderContentConstant.STT);

        if(List.of(3, 4, 5, 6, 7).contains(part)){

            int jColQuestionContent_CellOnHeader = 1;

            if(List.of(3, 4, 5).contains(part))
                checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColQuestionContent_CellOnHeader, part, ExcelHeaderContentConstant.Question_Content);
            else
                checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColQuestionContent_CellOnHeader, part, ExcelHeaderContentConstant.Question_Content_Child);

            int jColA_CellOnHeader = 2;

            checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColA_CellOnHeader, part, ExcelHeaderContentConstant.A);

            int jColB_CellOnHeader = 3;

            checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColB_CellOnHeader, part, ExcelHeaderContentConstant.B);

            int jColC_CellOnHeader = 4;

            checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColC_CellOnHeader, part, ExcelHeaderContentConstant.C);

            int jColD_CellOnHeader = 5;

            checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColD_CellOnHeader, part, ExcelHeaderContentConstant.D);

        }

        int jColResultHeaderTable = 6;

        if(List.of(1, 2).contains(part))
            jColResultHeaderTable = 1;

        checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColResultHeaderTable, part, ExcelHeaderContentConstant.Result);

        int jColScoreHeaderTable = 7;

        if(List.of(1, 2).contains(part))
            jColScoreHeaderTable = 2;

        checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColScoreHeaderTable, part, ExcelHeaderContentConstant.Score);

        if(part == 1){

            int jColImageHeaderTable = 3;

            checkCellHeaderTable(rowHeaderTable, iRowHeaderTable, jColImageHeaderTable, part, ExcelHeaderContentConstant.Image);
        }
    }

    private static void checkCellHeaderTable(Row rowHeaderTable, int iRowHeaderTable, int jColHeaderTable, int part, ExcelHeaderContentConstant cellHeaderValue){

        Cell cellOnHeaderTable = rowHeaderTable.getCell(jColHeaderTable);

        if(cellOnHeaderTable == null || !cellOnHeaderTable.getStringCellValue().equalsIgnoreCase(cellHeaderValue.getHeaderName()))
            throw new BadRequestException(
                    String.format(
                            "'%s' cell %d in header table row at row %d is required in Sheet %d",
                            cellHeaderValue.getHeaderName(),
                            jColHeaderTable,
                            iRowHeaderTable,
                            part
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
