package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.common.constaint.error.ErrorEnum;
import com.example.englishmaster_be.common.constaint.PartEnum;
import com.example.englishmaster_be.model.request.Answer.AnswerBasicRequest;
import com.example.englishmaster_be.model.response.excel.QuestionByExcelFileResponse;
import com.example.englishmaster_be.model.response.excel.ListQuestionByExcelFileResponse;
import com.example.englishmaster_be.model.response.excel.TopicByExcelFileResponse;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.repository.ContentRepository;
import com.example.englishmaster_be.repository.PartRepository;
import com.example.englishmaster_be.service.IExcelService;
import com.example.englishmaster_be.service.IPackService;
import com.example.englishmaster_be.service.IPartService;
import com.example.englishmaster_be.util.ExcelUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.DateUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelServiceImpl implements IExcelService {

    PartRepository partRepository;

    ContentRepository contentRepository;

    IPackService packService;

    IPartService partService;


    @Transactional
    @Override
    public TopicByExcelFileResponse parseCreateTopicDTO(MultipartFile file) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if(ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

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
            return TopicByExcelFileResponse.builder()
                    .topicName(topicName)
                    .topicImageName(topicImageName)
                    .topicDescription(topicDescription)
                    .topicType(topicType)
                    .workTime(workTime)
                    .numberQuestion(numberQuestion)
                    .packId(packService.getPackByName(topicName).getPackId())
                    .listPart(parts)
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
        }

    }

    @Transactional
    @Override
    public ListQuestionByExcelFileResponse parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (part != 1 && part != 2)
            throw new BadRequestException("Invalid PartEntity Value. It must be either 1 or 2");

        if (ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part); // Truy cập sheet theo phần (part)
            ListQuestionByExcelFileResponse resultDTO = new ListQuestionByExcelFileResponse();
            List<QuestionByExcelFileResponse> listQuestionDTO = new ArrayList<>();
            QuestionByExcelFileResponse questionBig = new QuestionByExcelFileResponse();
            List<QuestionByExcelFileResponse> listQuestionDTOMini = new ArrayList<>();

            // Lấy thông tin câu hỏi lớn từ các hàng đầu tiên của sheet
            Row rowAudio = sheet.getRow(1);
            String contentAudio = rowAudio != null ? getStringCellValue(rowAudio.getCell(1)) : "";
            String contentAudioLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentAudio);

            Row rowScoreBig = sheet.getRow(2);
            int scoreBig = rowScoreBig != null ? (int) getNumericCellValue(rowScoreBig.getCell(1)) : 0;

            // Cập nhật thông tin cho câu hỏi lớn
            questionBig.setContentAudio(contentAudioLink);
            questionBig.setQuestionScore(scoreBig);
            UUID partId = part == 1 ? partRepository.findByPartName(PartEnum.PART_1.getContent()).orElseThrow(() -> new CustomException(ErrorEnum.PART_NOT_FOUND)).getPartId()
                    : partRepository.findByPartName(PartEnum.PART_2.getContent()).orElseThrow(() -> new CustomException(ErrorEnum.PART_NOT_FOUND)).getPartId();
            questionBig.setPartId(partId);

            // Lấy các câu hỏi con từ sheet
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    QuestionByExcelFileResponse question = new QuestionByExcelFileResponse();
                    question.setPartId(partId);

                    // Xử lý câu trả lời với các tuỳ chọn A, B, C, D (phần 1) hoặc A, B, C (phần 2)
                    String[] options = part == 1 ? new String[]{"A", "B", "C", "D"} : new String[]{"A", "B", "C"};
                    String correctAnswer = getStringCellValue(row.getCell(1));
                    int score = (int) getNumericCellValue(row.getCell(2));
                    question.setQuestionScore(score);

                    // Tạo danh sách câu trả lời
                    List<AnswerBasicRequest> listAnswerDTO = new ArrayList<>();
                    for (String option : options) {
                        AnswerBasicRequest answer = new AnswerBasicRequest();
                        answer.setAnswerContent(option);
                        answer.setCorrectAnswer(correctAnswer.equalsIgnoreCase(option));
                        listAnswerDTO.add(answer);
                    }
                    question.setListAnswer(listAnswerDTO);

                    // Thêm câu hỏi vào danh sách câu hỏi con
                    listQuestionDTOMini.add(question);
                }
            }

            // Cập nhật thông tin câu hỏi lớn với danh sách câu hỏi con
            questionBig.setListQuestionChild(listQuestionDTOMini);
            listQuestionDTO.add(questionBig);

            // Đặt danh sách câu hỏi vào Request kết quả
            resultDTO.setQuestions(listQuestionDTO);
            return resultDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(part == 1 ? ErrorEnum.CAN_NOT_CREATE_PART_1_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_2_BY_EXCEL);
        }
    }


    @Transactional
    @Override
    public ListQuestionByExcelFileResponse parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if(ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(5);
            ListQuestionByExcelFileResponse resultDTO = new ListQuestionByExcelFileResponse();
            List<QuestionByExcelFileResponse> listQuestionDTO = new ArrayList<>();
            QuestionByExcelFileResponse questionBig = new QuestionByExcelFileResponse();
            List<QuestionByExcelFileResponse> listQuestionDTOMini = new ArrayList<>();
            int scoreBig = (int) getNumericCellValue(sheet.getRow(1).getCell(1));
            questionBig.setQuestionScore(scoreBig);
            questionBig.setPartId(partRepository.findByPartName(PartEnum.PART_5.getContent()).orElseThrow(() -> new CustomException(ErrorEnum.PART_NOT_FOUND)).getPartId());
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    QuestionByExcelFileResponse question = new QuestionByExcelFileResponse();
                    question.setPartId(questionBig.getPartId());
                    question.setQuestionContent(row.getCell(1).getStringCellValue());
                    int score = (int) getNumericCellValue(row.getCell(7));
                    question.setQuestionScore(score);
                    List<AnswerBasicRequest> listAnswerDTO = processAnswers(
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
            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public ListQuestionByExcelFileResponse parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (part != 3 && part != 4)
            throw new BadRequestException("Invalid PartEntity Value. It must be either 3 or 4");

        if(ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);
            List<QuestionByExcelFileResponse> listeningParts = new ArrayList<>();
            QuestionByExcelFileResponse currentListeningPart = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String firstCellValue = getStringCellValue(row, 0);
                if (firstCellValue.equalsIgnoreCase("Audio")) {
                    if (currentListeningPart != null) {
                        listeningParts.add(currentListeningPart);
                    }
                    currentListeningPart = new QuestionByExcelFileResponse();
                    currentListeningPart.setPartId(part == 3 ? partService.getPartToName(PartEnum.PART_3.getContent()).getPartId() : partService.getPartToName(PartEnum.PART_4.getContent()).getPartId());
                    String contentAudio = getStringCellValue(row, 1) == null ? null : getStringCellValue(row, 1);
                    if (contentAudio != null) {
                        String contentAudioLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentAudio);
                        currentListeningPart.setContentAudio(contentAudioLink);
                    }
                } else if (firstCellValue.equalsIgnoreCase("Score")) {
                    if (currentListeningPart != null) {
                        currentListeningPart.setQuestionScore(getNumericCellValue(row, 1));
                    }
                } else if (firstCellValue.equalsIgnoreCase("STT")) {
                    continue;
                } else if (firstCellValue.equalsIgnoreCase("Image")) {
                    if (currentListeningPart != null) {
                        String contentImage = getStringCellValue(row, 1);
                        String contentImageLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentImage);
                        currentListeningPart.setContentImage(contentImageLink);
                    }
                } else if (currentListeningPart != null) {
                    QuestionByExcelFileResponse question = new QuestionByExcelFileResponse();
                    question.setQuestionContent(getStringCellValue(row, 1));
                    question.setQuestionScore(getNumericCellValue(row, 7));
                    question.setPartId(currentListeningPart.getPartId());
                    List<AnswerBasicRequest> listAnswerDTO = processAnswers(
                            getStringCellValue(row, 2),
                            getStringCellValue(row, 3),
                            getStringCellValue(row, 4),
                            getStringCellValue(row, 5),
                            getStringCellValue(row, 6)
                    );
                    question.setListAnswer(listAnswerDTO);
                    if (currentListeningPart.getListQuestionChild() == null) {
                        currentListeningPart.setListQuestionChild(new ArrayList<>());
                    }
                    currentListeningPart.getListQuestionChild().add(question);
                }
            }
            if (currentListeningPart != null) {
                listeningParts.add(currentListeningPart);
            }
            ListQuestionByExcelFileResponse result = new ListQuestionByExcelFileResponse();
            result.setQuestions(listeningParts);
            return result;
        } catch (Exception e) {

            throw new CustomException(part == 3 ? ErrorEnum.CAN_NOT_CREATE_PART_3_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_4_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public ListQuestionByExcelFileResponse parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        if (part != 6 && part != 7)
            throw new BadRequestException("Invalid PartEntity Value. It must be either 6 or 7");

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);
            List<QuestionByExcelFileResponse> readingParts = new ArrayList<>();
            QuestionByExcelFileResponse currentReadingPart = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String firstCellValue = getStringCellValue(row, 0);
                if (firstCellValue.equalsIgnoreCase("QuestionEntity ContentEntity")) {
                    if (currentReadingPart != null) {
                        readingParts.add(currentReadingPart);
                    }
                    currentReadingPart = new QuestionByExcelFileResponse();
                    currentReadingPart.setPartId(part == 6 ? partService.getPartToName(PartEnum.PART_6.getContent()).getPartId() : partService.getPartToName(PartEnum.PART_5.getContent()).getPartId());
                    currentReadingPart.setQuestionContent(getStringCellValue(row, 1));
                } else if (firstCellValue.equalsIgnoreCase("Score")) {
                    // Nếu là "Score", set điểm cho phần reading hiện tại
                    if (currentReadingPart != null) {
                        currentReadingPart.setQuestionScore(getNumericCellValue(row, 1));
                    }
                } else if (firstCellValue.equalsIgnoreCase("STT")) {
                    continue;
                } else if (firstCellValue.equalsIgnoreCase("Image")) {
                    if (currentReadingPart != null) {
                        String contentImage = getStringCellValue(row, 1);
                        String contentImageLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentImage);
                        currentReadingPart.setContentImage(contentImageLink);
                    }
                } else if (currentReadingPart != null) {
                    QuestionByExcelFileResponse question = new QuestionByExcelFileResponse();
                    question.setQuestionContent(getStringCellValue(row, 1));
                    question.setQuestionScore(getNumericCellValue(row, 7));
                    question.setPartId(currentReadingPart.getPartId());
                    List<AnswerBasicRequest> listAnswerDTO = processAnswers(
                            getStringCellValue(row, 2),
                            getStringCellValue(row, 3),
                            getStringCellValue(row, 4),
                            getStringCellValue(row, 5),
                            getStringCellValue(row, 6)
                    );
                    question.setListAnswer(listAnswerDTO);
                    if (currentReadingPart.getListQuestionChild() == null) {
                        currentReadingPart.setListQuestionChild(new ArrayList<>());
                    }
                    currentReadingPart.getListQuestionChild().add(question);
                }
            }
            if (currentReadingPart != null) {
                readingParts.add(currentReadingPart);
            }
            ListQuestionByExcelFileResponse result = new ListQuestionByExcelFileResponse();
            result.setQuestions(readingParts);
            return result;
        } catch (Exception e) {

            throw new CustomException(part == 6 ? ErrorEnum.CAN_NOT_CREATE_PART_6_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_7_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public ListQuestionByExcelFileResponse parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        ListQuestionByExcelFileResponse result = new ListQuestionByExcelFileResponse();
        List<QuestionByExcelFileResponse> allQuestions = new ArrayList<>();
        for (int part : new int[]{1, 2}) {
            ListQuestionByExcelFileResponse part12DTO = parseListeningPart12DTO(topicId, file, part);
            allQuestions.addAll(part12DTO.getQuestions());
        }
        for (int part : new int[]{3, 4}) {
            ListQuestionByExcelFileResponse part34DTO = parseListeningPart34DTO(topicId, file, part);
            allQuestions.addAll(part34DTO.getQuestions());
        }
        ListQuestionByExcelFileResponse part5DTO = parseReadingPart5DTO(topicId, file);
        allQuestions.addAll(part5DTO.getQuestions());
        for (int part : new int[]{6, 7}) {
            ListQuestionByExcelFileResponse part67DTO = parseReadingPart67DTO(topicId, file, part);
            allQuestions.addAll(part67DTO.getQuestions());
        }

        result.setQuestions(allQuestions);

        return result;
    }


    private String getStringCellValue(Cell cell) {

        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> "";
        };
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

    private int getIntCellValue(Sheet sheet, int rowIndex, int cellIndex) {

        String value = getCellValueAsString(sheet, rowIndex, cellIndex);

        try {
            return (int) Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format at row " + (rowIndex + 1) + ", column " + (cellIndex + 1));
        }
    }

    private List<UUID> parseParts(String partsString) {

        List<String> listPart = Arrays.stream(partsString.split(", "))
                .map(String::trim)
                .toList();

        List<UUID> uuidList = new ArrayList<>();

        for (String part : listPart) {
            UUID uuid = partRepository.findByPartName(part).orElseThrow(() -> new CustomException(ErrorEnum.PART_NOT_FOUND)).getPartId();
            uuidList.add(uuid);
        }

        return uuidList;
    }

    private String getStringCellValue(Row row, int cellIndex) {

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

    private int getNumericCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null) ? (int) cell.getNumericCellValue() : 0;
    }

    private List<AnswerBasicRequest> processAnswers(String optionA, String optionB, String optionC, String optionD, String result) {
        List<AnswerBasicRequest> listAnswerDTO = new ArrayList<>();
        listAnswerDTO.add(createAnswerDTO(optionA, result));
        listAnswerDTO.add(createAnswerDTO(optionB, result));
        listAnswerDTO.add(createAnswerDTO(optionC, result));
        listAnswerDTO.add(createAnswerDTO(optionD, result));
        return listAnswerDTO;
    }

    private AnswerBasicRequest createAnswerDTO(String option, String result) {
        AnswerBasicRequest answerDTO = new AnswerBasicRequest();
        answerDTO.setAnswerContent(option);
        answerDTO.setCorrectAnswer(option != null && option.equalsIgnoreCase(result));
        return answerDTO;
    }

}
