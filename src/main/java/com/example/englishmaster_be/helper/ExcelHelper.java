package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.service.IPackService;
import com.example.englishmaster_be.service.IPartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String PART5 = "Part5";

//    public static boolean hasExcelFormat(MultipartFile file) {
//
//        if (!TYPE.equals(file.getContentType())) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public static void excelToQuestionPart5(InputStream is){
//        try {

//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheet(PART5);
//
//            Iterator<Row> rows = sheet.iterator();
//
//            int rowNumber = 0;
//            while (rows.hasNext()) {
//                Row currentRow = rows.next();
//
//                if (rowNumber == 0) {
//                    rowNumber++;
//                    continue;
//                }
//
//
//                Iterator<Cell> cellsInRow = currentRow.iterator();
//
//                int cellIdx = 0;
//                Question question = new Question();
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//                        case 1 -> {
//                            question.setQuestionContent(currentCell.getStringCellValue());
//
//                        }
//                        case 2 -> {
//                            Answer answer = new Answer();
//                            answer.setAnswerContent(currentCell.getStringCellValue());
//                            answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("A"));
//
//                            answer.setQuestion(question);
//                        }
//                        case 3 -> {
//                            Answer answer = new Answer();
//                            answer.setAnswerContent(currentCell.getStringCellValue());
//                            answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("B"));
//
//                            answer.setQuestion(question);
//                        }
//                        case 4 -> {
//                            Answer answer = new Answer();
//                            answer.setAnswerContent(currentCell.getStringCellValue());
//                            answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("C"));
//
//                            answer.setQuestion(question);
//                        }
//                        case 5 -> {
//                            Answer answer = new Answer();
//                            answer.setAnswerContent(currentCell.getStringCellValue());
//                            answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("D"));
//
//                            answer.setQuestion(question);
//                        }
//                        case 6 -> {
////                            question.setQuestionScore((int) currentCell.getNumericCellValue());
//                        }
//                        default -> {
//                        }
//                    }
//
//                    cellIdx++;
//                    }
////              System.out.println(question.getPart());
//            }
//            workbook.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
//    }
}
