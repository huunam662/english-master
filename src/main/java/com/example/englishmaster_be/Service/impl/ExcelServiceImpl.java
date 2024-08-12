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
    public CreateListQuestionByExcelFileDTO parseCreateListQuestionDTO(MultipartFile file) throws IOException {
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
                    String image = row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null;
                    String audio = row.getCell(9) != null ? row.getCell(9).getStringCellValue() : null;

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
                    question.setContentImage(image);
                    question.setContentAudio(audio);
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
    public CreateListQuestionByExcelFileDTO parseReadingPartDTO(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(6); // Giả định dữ liệu nằm ở sheet đầu tiên
            List<CreateQuestionByExcelFileDTO> readingParts = new ArrayList<>();

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row != null) {
                    // Kiểm tra nếu là tiêu đề của một đoạn Reading mới (ví dụ: "Question Content")
                    if (isReadingPartHeader(row)) {
                        CreateQuestionByExcelFileDTO readingPart = new CreateQuestionByExcelFileDTO();

                        // Lấy tiêu đề của đoạn Reading
                        String readingContent = sheet.getRow(i + 1).getCell(1).getStringCellValue();
                        readingPart.setQuestionContent(readingContent);

                        System.out.println(readingContent);

                        List<CreateQuestionByExcelFileDTO> questionsChild = new ArrayList<>();
                        i += 2; // Bỏ qua dòng "Score"

                        while (i <= sheet.getLastRowNum()) {
                            row = sheet.getRow(i);
                            if (row == null || isReadingPartHeader(row)) {
                                break; // Dừng lại nếu gặp tiêu đề mới hoặc dòng rỗng
                            }

                            // Lấy dữ liệu của câu hỏi
                            CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                            question.setQuestionContent(getStringCellValue(row, 1));
                            question.setQuestionScore(getNumericCellValue(row, 7));
                            question.setPartId(UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"));
                            question.setContentImage(getStringCellValue(row, 8));
                            question.setContentAudio(getStringCellValue(row, 9));

                            List<CreateListAnswerDTO> listAnswerDTO = processAnswers(
                                    getStringCellValue(row, 2),
                                    getStringCellValue(row, 3),
                                    getStringCellValue(row, 4),
                                    getStringCellValue(row, 5),
                                    getStringCellValue(row, 6)
                            );
                            question.setListAnswer(listAnswerDTO);

                            questionsChild.add(question);
                            i++;
                        }

                        readingPart.setListQuestionChild(questionsChild);
                        readingParts.add(readingPart);
                    }
                }
            }
            CreateListQuestionByExcelFileDTO createListQuestionByExcelFileDTO = new CreateListQuestionByExcelFileDTO();
            createListQuestionByExcelFileDTO.setQuestions(readingParts);

            return createListQuestionByExcelFileDTO;
        }
    }

    private boolean isReadingPartHeader(Row row) {
        // Kiểm tra nếu ô đầu tiên có nội dung như "Question Content" hoặc "STT"
        return row.getCell(1) != null &&
                (row.getCell(1).getStringCellValue().equalsIgnoreCase("Question Content") ||
                        row.getCell(1).getStringCellValue().equalsIgnoreCase("STT"));
    }

    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null) ? cell.getStringCellValue() : null;
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
