package com.example.englishmaster_be.domain.excel.util;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerBasicRequest;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    public static boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();

        String fileName = file.getOriginalFilename();

        return contentType == null ||
                (!contentType.equals("application/vnd.ms-excel") &&
                        !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) ||
                (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")));
    }

    public static void checkPartInScope(int partNumber) {

        List<Integer> partsScope = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        if (!partsScope.contains(partNumber))
            throw new ErrorHolder(Error.BAD_REQUEST, String.format("Part number must one value in scope [%s]", String.join(", ", partsScope.stream().map(String::valueOf).toList())));
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
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
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
