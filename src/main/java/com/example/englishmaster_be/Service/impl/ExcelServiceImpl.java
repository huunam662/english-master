package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.CreateQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.Repository.PackRepository;
import com.example.englishmaster_be.Repository.PartRepository;
import com.example.englishmaster_be.Service.IExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelServiceImpl implements IExcelService {

    @Autowired
    private PackRepository packRepository;

    @Autowired
    private PartRepository partRepository;

    @Override
    public CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            String topicName = getCellValueAsString(sheet, 0, 1);
            String topicImageName = getCellValueAsString(sheet, 1, 1);
            String topicDescription = getCellValueAsString(sheet, 2, 1);
            String topicType = getCellValueAsString(sheet, 3, 1);
            String workTime = getCellValueAsString(sheet, 4, 1);
            int numberQuestion = getIntCellValue(sheet, 5, 1);
            String topicPackName = getCellValueAsString(sheet, 6, 1);
            List<UUID> parts = parseParts(getCellValueAsString(sheet, 7, 1));

            return CreateTopicByExcelFileDTO.builder()
                    .topicName(topicName)
                    .topicImageName(topicImageName)
                    .topicDescription(topicDescription)
                    .topicType(topicType)
                    .workTime(workTime)
                    .numberQuestion(numberQuestion)
                    .topicPackId(packRepository.findByPackName(topicPackName)
                            .orElseThrow(() -> new IllegalArgumentException("Pack not found: " + topicPackName))
                            .getPackId())
                    .listPart(parts)
                    .build();
        }
    }

    @Override
    public CreateListQuestionByExcelFileDTO parseReadingPart5DTO(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(5);
            List<CreateQuestionByExcelFileDTO> listQuestionDTO = new ArrayList<>();

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    // Đọc từng cột từ dòng hiện tại
                    String questionContent = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                    String optionA = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
                    String optionB = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
                    String optionC = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null;
                    String optionD = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
                    String result = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;
                    int score = row.getCell(7) != null ? (int) row.getCell(7).getNumericCellValue() : 0;
//                    String image = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null;
//                    String audio = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : null;

                    List<CreateListAnswerDTO> listAnswerDTO = new ArrayList<>();

                    // Kiểm tra từng option và set correctAnswer nếu trùng với result
                    CreateListAnswerDTO answerA = new CreateListAnswerDTO();
                    answerA.setContentAnswer(optionA);
                    answerA.setCorrectAnswer(optionA != null && optionA.equalsIgnoreCase(result));
                    listAnswerDTO.add(answerA);

                    CreateListAnswerDTO answerB = new CreateListAnswerDTO();
                    answerB.setContentAnswer(optionB);
                    answerB.setCorrectAnswer(optionB != null && optionB.equalsIgnoreCase(result));
                    listAnswerDTO.add(answerB);

                    CreateListAnswerDTO answerC = new CreateListAnswerDTO();
                    answerC.setContentAnswer(optionC);
                    answerC.setCorrectAnswer(optionC != null && optionC.equalsIgnoreCase(result));
                    listAnswerDTO.add(answerC);

                    CreateListAnswerDTO answerD = new CreateListAnswerDTO();
                    answerD.setContentAnswer(optionD);
                    answerD.setCorrectAnswer(optionD != null && optionD.equalsIgnoreCase(result));
                    listAnswerDTO.add(answerD);

                    // Tạo đối tượng Question và thêm vào danh sách
                    CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                    question.setQuestionContent(questionContent);
                    question.setQuestionScore(score);
                    question.setPartId(UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"));
//                    question.setContentImage(image);
//                    question.setContentAudio(audio);
                    question.setListAnswer(listAnswerDTO);

                    listQuestionDTO.add(question);
                }
            }

            // Sau khi hoàn tất việc đọc và xử lý file Excel
            CreateListQuestionByExcelFileDTO createListQuestionByExcelFileDTO = new CreateListQuestionByExcelFileDTO();
            createListQuestionByExcelFileDTO.setQuestions(listQuestionDTO);
            return createListQuestionByExcelFileDTO;
        }
    }

    @Override
    public CreateListQuestionByExcelFileDTO parseListeningPart12DTO(MultipartFile file, int part) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(part);
            CreateListQuestionByExcelFileDTO resultDTO = new CreateListQuestionByExcelFileDTO();
            List<CreateQuestionByExcelFileDTO> listQuestionDTO = new ArrayList<>();
            CreateQuestionByExcelFileDTO questionBig = new CreateQuestionByExcelFileDTO();
            List<CreateQuestionByExcelFileDTO> listQuestionDTOMini = new ArrayList<>();

            // Lấy audio content
            Row rowAudio = sheet.getRow(1);
            String contentAudio = getStringCellValue(rowAudio.getCell(1));

            // Lấy điểm số lớn
            int scoreBig = (int) getNumericCellValue(sheet.getRow(2).getCell(1));

            questionBig.setContentAudio(contentAudio);
            questionBig.setQuestionScore(scoreBig);

            // Set partId cho câu hỏi lớn
            questionBig.setPartId(UUID.fromString(part == 1 ? "5e051716-1b41-4385-bfe6-3e350d5acb06" : "9509bfa5-0403-48db-bee1-1af41cfc73df"));

            // Bắt đầu từ dòng 5 (index 4) cho các câu hỏi
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                    question.setPartId(questionBig.getPartId());

                    // Lấy số thứ tự câu hỏi (STT)
                    int questionNumber = (int) getNumericCellValue(row.getCell(0));
                    question.setQuestionContent("Question " + questionNumber);

                    // Lấy kết quả đúng
                    String correctAnswer = getStringCellValue(row.getCell(1));

                    // Lấy điểm số cho câu hỏi
                    int score = (int) getNumericCellValue(row.getCell(2));
                    question.setQuestionScore(score);

                    List<CreateListAnswerDTO> listAnswerDTO = new ArrayList<>();
                    String[] options = {"A", "B", "C", "D"};
                    for (String option : options) {
                        CreateListAnswerDTO answer = new CreateListAnswerDTO();
                        answer.setContentAnswer(option);
                        answer.setCorrectAnswer(correctAnswer.equalsIgnoreCase(option));
                        listAnswerDTO.add(answer);
                    }

                    question.setListAnswer(listAnswerDTO);
                    listQuestionDTOMini.add(question);
                }
            }

            questionBig.setListQuestionChild(listQuestionDTOMini);
            listQuestionDTO.add(questionBig);

            resultDTO.setQuestions(listQuestionDTO);
            return resultDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper methods remain the same
    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private double getNumericCellValue(Cell cell) {
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


    private String getCellValueAsString(Sheet sheet, int rowIndex, int cellIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) return "";
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private int getIntCellValue(Sheet sheet, int rowIndex, int cellIndex) {
        String value = getCellValueAsString(sheet, rowIndex, cellIndex);
        try {
            return (int) Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format at row " + (rowIndex + 1) + ", column " + (cellIndex + 1));
        }
    }

    private List<UUID> parseParts(String partsString) {
        List<String> listPart = Arrays.stream(partsString.split(","))
                .map(String::trim)
                .toList();

        List<UUID> uuidList = new ArrayList<>();
        for (String part : listPart) {
            UUID uuid = partRepository.findByPartName(part).get().getPartId();
            uuidList.add(uuid);
        }
        return uuidList;
    }

    @Override
    public CreateListQuestionByExcelFileDTO parseReadingPart67DTO(MultipartFile file, int part) throws IOException {
        // Mở workbook từ file Excel được upload
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Lấy sheet thứ 7 (index 6) từ workbook
            Sheet sheet = workbook.getSheetAt(part);
            // Khởi tạo list để lưu các phần reading
            List<CreateQuestionByExcelFileDTO> readingParts = new ArrayList<>();
            // Biến để theo dõi phần reading hiện tại đang xử lý
            CreateQuestionByExcelFileDTO currentReadingPart = null;

            // Duyệt qua từng dòng trong sheet, bắt đầu từ dòng thứ 2 (index 1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;  // Bỏ qua dòng trống

                // Lấy giá trị của ô đầu tiên trong dòng
                String firstCellValue = getStringCellValue(row, 0);

                // Xử lý các trường hợp dựa vào giá trị của ô đầu tiên
                if (firstCellValue.equalsIgnoreCase("Question Content")) {
                    // Nếu là "Question Content", tạo một phần reading mới
                    if (currentReadingPart != null) {
                        readingParts.add(currentReadingPart);
                    }
                    currentReadingPart = new CreateQuestionByExcelFileDTO();
                    currentReadingPart.setPartId(UUID.fromString(part == 6 ? "22b25c09-33db-4e3a-b228-37b331b39c96" : "2416aa89-3284-4315-b759-f3f1b1d5ff3f"));
                    currentReadingPart.setQuestionContent(getStringCellValue(row, 1));
                } else if (firstCellValue.equalsIgnoreCase("Score")) {
                    // Nếu là "Score", set điểm cho phần reading hiện tại
                    if (currentReadingPart != null) {
                        currentReadingPart.setQuestionScore(getNumericCellValue(row, 1));
                    }
                } else if (firstCellValue.equalsIgnoreCase("STT")) {
                    // Bỏ qua dòng tiêu đề
                    continue;
                } else if (currentReadingPart != null) {
                    // Xử lý câu hỏi con
                    CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                    // Set các thuộc tính cho câu hỏi
                    question.setQuestionContent(getStringCellValue(row, 1));
                    question.setQuestionScore(getNumericCellValue(row, 7));
                    question.setPartId(UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"));
//                    question.setContentImage(getStringCellValue(row, 8));
//                    question.setContentAudio(getStringCellValue(row, 9));

                    // Xử lý các câu trả lời
                    List<CreateListAnswerDTO> listAnswerDTO = processAnswers(
                            getStringCellValue(row, 2),
                            getStringCellValue(row, 3),
                            getStringCellValue(row, 4),
                            getStringCellValue(row, 5),
                            getStringCellValue(row, 6)
                    );
                    question.setListAnswer(listAnswerDTO);

                    // Thêm câu hỏi vào danh sách câu hỏi con của phần reading hiện tại
                    if (currentReadingPart.getListQuestionChild() == null) {
                        currentReadingPart.setListQuestionChild(new ArrayList<>());
                    }
                    currentReadingPart.getListQuestionChild().add(question);
                }
            }

            // Thêm phần reading cuối cùng vào danh sách (nếu có)
            if (currentReadingPart != null) {
                readingParts.add(currentReadingPart);
            }

            // Tạo và trả về kết quả cuối cùng
            CreateListQuestionByExcelFileDTO result = new CreateListQuestionByExcelFileDTO();
            result.setQuestions(readingParts);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Trả về null nếu có lỗi xảy ra
    }


    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private int getNumericCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null) ? (int) cell.getNumericCellValue() : 0;
    }

    private List<CreateListAnswerDTO> processAnswers(String optionA, String optionB, String optionC, String optionD, String result) {
        List<CreateListAnswerDTO> listAnswerDTO = new ArrayList<>();

        listAnswerDTO.add(createAnswerDTO(optionA, result));
        listAnswerDTO.add(createAnswerDTO(optionB, result));
        listAnswerDTO.add(createAnswerDTO(optionC, result));
        listAnswerDTO.add(createAnswerDTO(optionD, result));

        return listAnswerDTO;
    }

    private CreateListAnswerDTO createAnswerDTO(String option, String result) {
        CreateListAnswerDTO answerDTO = new CreateListAnswerDTO();
        answerDTO.setContentAnswer(option);
        answerDTO.setCorrectAnswer(option != null && option.equalsIgnoreCase(result));
        return answerDTO;
    }

}
