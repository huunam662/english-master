package com.example.englishmaster_be.Helper;


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
//                Question Question = new Question();
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//                        case 1 -> {
//                            Question.setQuestionContent(currentCell.getStringCellValue());
//
//                        }
//                        case 2 -> {
//                            Answer Answer = new Answer();
//                            Answer.setAnswerContent(currentCell.getStringCellValue());
//                            Answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("A"));
//
//                            Answer.setQuestion(Question);
//                        }
//                        case 3 -> {
//                            Answer Answer = new Answer();
//                            Answer.setAnswerContent(currentCell.getStringCellValue());
//                            Answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("B"));
//
//                            Answer.setQuestion(Question);
//                        }
//                        case 4 -> {
//                            Answer Answer = new Answer();
//                            Answer.setAnswerContent(currentCell.getStringCellValue());
//                            Answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("C"));
//
//                            Answer.setQuestion(Question);
//                        }
//                        case 5 -> {
//                            Answer Answer = new Answer();
//                            Answer.setAnswerContent(currentCell.getStringCellValue());
//                            Answer.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("D"));
//
//                            Answer.setQuestion(Question);
//                        }
//                        case 6 -> {
////                            Question.setQuestionScore((int) currentCell.getNumericCellValue());
//                        }
//                        default -> {
//                        }
//                    }
//
//                    cellIdx++;
//                    }
////              System.out.println(Question.getPart());
//            }
//            workbook.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
//        }
//    }
}
