package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Answer.CreateListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.CreateQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Repository.ContentRepository;
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
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private ContentRepository contentRepository;

    @Transactional
    @Override
    public CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) {
        if (isExcelFile(file)) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);

                String topicName = getCellValueAsString(sheet, 0, 1);
                String topicImageName = getCellValueAsString(sheet, 1, 1);
                String topicDescription = getCellValueAsString(sheet, 2, 1);
                String topicType = getCellValueAsString(sheet, 3, 1);
                String workTime = getCellValueAsString(sheet, 4, 1).replace(".0", "");
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
            } catch (Exception e) {
                throw new CustomException(Error.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
            }
        } else {
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileDTO parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) {
        if (isExcelFile(file)) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

                Sheet sheet = workbook.getSheetAt(part);
                CreateListQuestionByExcelFileDTO resultDTO = new CreateListQuestionByExcelFileDTO();
                List<CreateQuestionByExcelFileDTO> listQuestionDTO = new ArrayList<>();
                CreateQuestionByExcelFileDTO questionBig = new CreateQuestionByExcelFileDTO();
                List<CreateQuestionByExcelFileDTO> listQuestionDTOMini = new ArrayList<>();

                // Retrieve audio content
                Row rowAudio = sheet.getRow(1);
                String contentAudio = rowAudio != null ? getStringCellValue(rowAudio.getCell(1)) : "";
                System.out.println(contentAudio);
                String contentAudioLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentAudio);

                // Retrieve big score
                Row rowScoreBig = sheet.getRow(2);
                int scoreBig = rowScoreBig != null ? (int) getNumericCellValue(rowScoreBig.getCell(1)) : 0;

                questionBig.setContentAudio(contentAudioLink);
                questionBig.setQuestionScore(scoreBig);

                // Set partId for the big question
                UUID partId = part == 1 ? UUID.fromString("5e051716-1b41-4385-bfe6-3e350d5acb06")
                        : UUID.fromString("9509bfa5-0403-48db-bee1-1af41cfc73df");
                questionBig.setPartId(partId);

                // Start from row 5 (index 4) for the questions
                for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                        question.setPartId(partId);

                        if (part == 1) {
                            String image = getStringCellValue(row.getCell(3));
                            String imageLink = contentRepository.findContentDataByTopicIdAndCode(topicId, image);
                            question.setContentImage(imageLink);
                        }

                        // Retrieve the correct answer
                        String correctAnswer = getStringCellValue(row.getCell(1));

                        // Retrieve the score for the question
                        int score = (int) getNumericCellValue(row.getCell(2));
                        question.setQuestionScore(score);

                        // Generate answer options
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
                throw new CustomException(part == 1 ? Error.CAN_NOT_CREATE_PART_1_BY_EXCEL : Error.CAN_NOT_CREATE_PART_2_BY_EXCEL);
            }
        } else {
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileDTO parseReadingPart5DTO(UUID topicId, MultipartFile file) {
        if (isExcelFile(file)) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(5);
                CreateListQuestionByExcelFileDTO resultDTO = new CreateListQuestionByExcelFileDTO();
                List<CreateQuestionByExcelFileDTO> listQuestionDTO = new ArrayList<>();
                CreateQuestionByExcelFileDTO questionBig = new CreateQuestionByExcelFileDTO();
                List<CreateQuestionByExcelFileDTO> listQuestionDTOMini = new ArrayList<>();

                int scoreBig = (int) getNumericCellValue(sheet.getRow(1).getCell(1));
                questionBig.setQuestionScore(scoreBig);

                questionBig.setPartId(UUID.fromString("57572f04-27cf-4da7-8344-ac484c7d9e08"));

                for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                        question.setPartId(questionBig.getPartId());

                        int score = (int) getNumericCellValue(row.getCell(7));
                        question.setQuestionScore(score);

                        List<CreateListAnswerDTO> listAnswerDTO = processAnswers(
                                getStringCellValue(row, 2),
                                getStringCellValue(row, 3),
                                getStringCellValue(row, 4),
                                getStringCellValue(row, 5),
                                getStringCellValue(row, 6)
                        );
                        question.setListAnswer(listAnswerDTO);
                        listQuestionDTOMini.add(question);
                    }
                }
                questionBig.setListQuestionChild(listQuestionDTOMini);
                listQuestionDTO.add(questionBig);

                resultDTO.setQuestions(listQuestionDTO);
                return resultDTO;
            } catch (Exception e) {
                throw new CustomException(Error.CAN_NOT_CREATE_PART_5_BY_EXCEL);
            }
        } else {
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileDTO parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) {
        if (isExcelFile(file)) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(part);
                List<CreateQuestionByExcelFileDTO> listeningParts = new ArrayList<>();
                CreateQuestionByExcelFileDTO currentListeningPart = null;

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;  // Bỏ qua dòng trống
                    String firstCellValue = getStringCellValue(row, 0);
                    if (firstCellValue.equalsIgnoreCase("Audio")) {
                        // Nếu là "Question Content", tạo một phần reading mới
                        if (currentListeningPart != null) {
                            listeningParts.add(currentListeningPart);
                        }
                        currentListeningPart = new CreateQuestionByExcelFileDTO();
                        currentListeningPart.setPartId(UUID.fromString(part == 3 ? "2496a543-49c3-4580-80b6-c9984e4142e1" : "3b4d6b90-fc31-484e-afe3-3a21162b6454"));
                        String contentAudio = getStringCellValue(row, 1) == null ? null : getStringCellValue(row, 1);
                        if (contentAudio != null) {
                            String contentAudioLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentAudio);
                            currentListeningPart.setContentAudio(contentAudioLink);
                        }
                    } else if (firstCellValue.equalsIgnoreCase("Score")) {
                        // Nếu là "Score", set điểm cho phần reading hiện tại
                        if (currentListeningPart != null) {
                            currentListeningPart.setQuestionScore(getNumericCellValue(row, 1));
                        }
                    } else if (firstCellValue.equalsIgnoreCase("STT")) {
                        // Bỏ qua dòng tiêu đề
                        continue;
                    } else if (firstCellValue.equalsIgnoreCase("Image")) {
                        if (currentListeningPart != null) {
                            String contentImage = getStringCellValue(row, 1);
                            String contentImageLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentImage);
                            currentListeningPart.setContentImage(contentImageLink);
                        }
                    } else if (currentListeningPart != null) {
                        // Xử lý câu hỏi con
                        CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                        // Set các thuộc tính cho câu hỏi
                        question.setQuestionContent(getStringCellValue(row, 1));
                        question.setQuestionScore(getNumericCellValue(row, 7));
                        question.setPartId(currentListeningPart.getPartId());


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
                        if (currentListeningPart.getListQuestionChild() == null) {
                            currentListeningPart.setListQuestionChild(new ArrayList<>());
                        }
                        currentListeningPart.getListQuestionChild().add(question);
                    }
                }

                // Thêm phần reading cuối cùng vào danh sách (nếu có)
                if (currentListeningPart != null) {
                    listeningParts.add(currentListeningPart);
                }

                // Tạo và trả về kết quả cuối cùng
                CreateListQuestionByExcelFileDTO result = new CreateListQuestionByExcelFileDTO();
                result.setQuestions(listeningParts);
                return result;
            } catch (Exception e) {
                throw new CustomException(part == 3 ? Error.CAN_NOT_CREATE_PART_3_BY_EXCEL : Error.CAN_NOT_CREATE_PART_4_BY_EXCEL);
            }
        } else {
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileDTO parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) {
        if (isExcelFile(file)) {
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
                    } else if (firstCellValue.equalsIgnoreCase("Image")) {
                        if (currentReadingPart != null) {
                            String contentImage = getStringCellValue(row, 1);
                            String contentImageLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentImage);
                            currentReadingPart.setContentImage(contentImageLink);
                        }
                    } else if (currentReadingPart != null) {
                        // Xử lý câu hỏi con
                        CreateQuestionByExcelFileDTO question = new CreateQuestionByExcelFileDTO();
                        // Set các thuộc tính cho câu hỏi
                        question.setQuestionContent(getStringCellValue(row, 1));
                        question.setQuestionScore(getNumericCellValue(row, 7));
                        question.setPartId(currentReadingPart.getPartId());


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
                throw new CustomException(part == 6 ? Error.CAN_NOT_CREATE_PART_6_BY_EXCEL : Error.CAN_NOT_CREATE_PART_7_BY_EXCEL);

            }
        } else {
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);
        }
    }

    @Override
    public CreateListQuestionByExcelFileDTO parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException {
        if (isExcelFile(file)) {

            CreateListQuestionByExcelFileDTO result = new CreateListQuestionByExcelFileDTO();
            List<CreateQuestionByExcelFileDTO> allQuestions = new ArrayList<>();

            // Parse Listening Part 1 & 2
            for (int part : new int[]{1, 2}) {
                CreateListQuestionByExcelFileDTO part12DTO = parseListeningPart12DTO(topicId, file, part);
                allQuestions.addAll(part12DTO.getQuestions());
            }

//             Parse Listening Part 3 & 4
            for (int part : new int[]{3, 4}) {
                CreateListQuestionByExcelFileDTO part34DTO = parseListeningPart34DTO(topicId, file, part);
                allQuestions.addAll(part34DTO.getQuestions());
            }

            // Parse Reading Part 5
            CreateListQuestionByExcelFileDTO part5DTO = parseReadingPart5DTO(topicId, file);
            allQuestions.addAll(part5DTO.getQuestions());

            // Parse Reading Part 6 & 7
            for (int part : new int[]{6, 7}) {
                CreateListQuestionByExcelFileDTO part67DTO = parseReadingPart67DTO(topicId, file, part);
                allQuestions.addAll(part67DTO.getQuestions());
            }

            result.setQuestions(allQuestions);
            return result;
        }
        return null;
    }

    @Override
    public boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        return contentType != null &&
                (contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) &&
                (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")));
    }

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
