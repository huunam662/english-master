package com.example.englishmaster_be.helper;


public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String PART5 = "Part5";

//    public static boolean hasExcelFormat(MultipartFile file_storage) {
//
//        if (!TYPE.equals(file_storage.getContentType())) {
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
//                QuestionEntity QuestionEntity = new QuestionEntity();
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//                        case 1 -> {
//                            QuestionEntity.setQuestionContent(currentCell.getStringCellValue());
//
//                        }
//                        case 2 -> {
//                            AnswerEntity AnswerEntity = new AnswerEntity();
//                            AnswerEntity.setAnswerContent(currentCell.getStringCellValue());
//                            AnswerEntity.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("A"));
//
//                            AnswerEntity.setQuestion(QuestionEntity);
//                        }
//                        case 3 -> {
//                            AnswerEntity AnswerEntity = new AnswerEntity();
//                            AnswerEntity.setAnswerContent(currentCell.getStringCellValue());
//                            AnswerEntity.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("B"));
//
//                            AnswerEntity.setQuestion(QuestionEntity);
//                        }
//                        case 4 -> {
//                            AnswerEntity AnswerEntity = new AnswerEntity();
//                            AnswerEntity.setAnswerContent(currentCell.getStringCellValue());
//                            AnswerEntity.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("C"));
//
//                            AnswerEntity.setQuestion(QuestionEntity);
//                        }
//                        case 5 -> {
//                            AnswerEntity AnswerEntity = new AnswerEntity();
//                            AnswerEntity.setAnswerContent(currentCell.getStringCellValue());
//                            AnswerEntity.setCorrectAnswer( currentRow.getCell(6).getStringCellValue().equalsIgnoreCase("D"));
//
//                            AnswerEntity.setQuestion(QuestionEntity);
//                        }
//                        case 6 -> {
////                            QuestionEntity.setQuestionScore((int) currentCell.getNumericCellValue());
//                        }
//                        default -> {
//                        }
//                    }
//
//                    cellIdx++;
//                    }
////              System.out.println(QuestionEntity.getPart());
//            }
//            workbook.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException("fail to parse Excel file_storage: " + e.getMessage());
//        }
//    }
}
