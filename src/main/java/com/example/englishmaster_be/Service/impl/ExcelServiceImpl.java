package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Constant.PartConstant;
import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.Response.excel.CreateQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.PackRepository;
import com.example.englishmaster_be.Repository.PartRepository;
import com.example.englishmaster_be.Service.IExcelService;
import com.example.englishmaster_be.Util.ExcelUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelServiceImpl implements IExcelService {

    static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    PackRepository packRepository;

    PartRepository partRepository;

    ContentRepository contentRepository;

    @Transactional
    @Override
    public CreateTopicByExcelFileResponse parseCreateTopicDTO(MultipartFile file) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if(!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

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
            return CreateTopicByExcelFileResponse.builder()
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

    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileResponse parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (part != 1 && part != 2)
            throw new BadRequestException("Invalid Part Value. It must be either 1 or 2");

        if (!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part); // Truy cập sheet theo phần (part)
            CreateListQuestionByExcelFileResponse resultDTO = new CreateListQuestionByExcelFileResponse();
            List<CreateQuestionByExcelFileResponse> listQuestionDTO = new ArrayList<>();
            CreateQuestionByExcelFileResponse questionBig = new CreateQuestionByExcelFileResponse();
            List<CreateQuestionByExcelFileResponse> listQuestionDTOMini = new ArrayList<>();

            // Lấy thông tin câu hỏi lớn từ các hàng đầu tiên của sheet
            Row rowAudio = sheet.getRow(1);
            String contentAudio = rowAudio != null ? getStringCellValue(rowAudio.getCell(1)) : "";
            String contentAudioLink = contentRepository.findContentDataByTopicIdAndCode(topicId, contentAudio);

            Row rowScoreBig = sheet.getRow(2);
            int scoreBig = rowScoreBig != null ? (int) getNumericCellValue(rowScoreBig.getCell(1)) : 0;

            // Cập nhật thông tin cho câu hỏi lớn
            questionBig.setContentAudio(contentAudioLink);
            questionBig.setQuestionScore(scoreBig);
            UUID partId = part == 1 ? partRepository.findByPartName(PartConstant.PART_1).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId()
                    : partRepository.findByPartName(PartConstant.PART_2).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId();
            questionBig.setPartId(partId);

            // Lấy các câu hỏi con từ sheet
            for (int i = 4; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    CreateQuestionByExcelFileResponse question = new CreateQuestionByExcelFileResponse();
                    question.setPartId(partId);

                    // Xử lý câu trả lời với các tuỳ chọn A, B, C, D (phần 1) hoặc A, B, C (phần 2)
                    String[] options = part == 1 ? new String[]{"A", "B", "C", "D"} : new String[]{"A", "B", "C"};
                    String correctAnswer = getStringCellValue(row.getCell(1));
                    int score = (int) getNumericCellValue(row.getCell(2));
                    question.setQuestionScore(score);

                    // Tạo danh sách câu trả lời
                    List<SaveListAnswerDTO> listAnswerDTO = new ArrayList<>();
                    for (String option : options) {
                        SaveListAnswerDTO answer = new SaveListAnswerDTO();
                        answer.setContentAnswer(option);
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

            // Đặt danh sách câu hỏi vào DTO kết quả
            resultDTO.setQuestions(listQuestionDTO);
            return resultDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(part == 1 ? Error.CAN_NOT_CREATE_PART_1_BY_EXCEL : Error.CAN_NOT_CREATE_PART_2_BY_EXCEL);
        }
    }


    @Transactional
    @Override
    public CreateListQuestionByExcelFileResponse parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if(!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(5);
            CreateListQuestionByExcelFileResponse resultDTO = new CreateListQuestionByExcelFileResponse();
            List<CreateQuestionByExcelFileResponse> listQuestionDTO = new ArrayList<>();
            CreateQuestionByExcelFileResponse questionBig = new CreateQuestionByExcelFileResponse();
            List<CreateQuestionByExcelFileResponse> listQuestionDTOMini = new ArrayList<>();
            int scoreBig = (int) getNumericCellValue(sheet.getRow(1).getCell(1));
            questionBig.setQuestionScore(scoreBig);
            questionBig.setPartId(partRepository.findByPartName(PartConstant.PART_5).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId());
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    CreateQuestionByExcelFileResponse question = new CreateQuestionByExcelFileResponse();
                    question.setPartId(questionBig.getPartId());
                    question.setQuestionContent(row.getCell(1).getStringCellValue());
                    int score = (int) getNumericCellValue(row.getCell(7));
                    question.setQuestionScore(score);
                    List<SaveListAnswerDTO> listAnswerDTO = processAnswers(
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
            throw new CustomException(Error.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileResponse parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (part != 3 && part != 4)
            throw new BadRequestException("Invalid Part Value. It must be either 3 or 4");

        if(!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);
            List<CreateQuestionByExcelFileResponse> listeningParts = new ArrayList<>();
            CreateQuestionByExcelFileResponse currentListeningPart = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String firstCellValue = getStringCellValue(row, 0);
                if (firstCellValue.equalsIgnoreCase("Audio")) {
                    if (currentListeningPart != null) {
                        listeningParts.add(currentListeningPart);
                    }
                    currentListeningPart = new CreateQuestionByExcelFileResponse();
                    currentListeningPart.setPartId(part == 3 ? partRepository.findByPartName(PartConstant.PART_3).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId() : partRepository.findByPartName(PartConstant.PART_4).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId());
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
                    CreateQuestionByExcelFileResponse question = new CreateQuestionByExcelFileResponse();
                    question.setQuestionContent(getStringCellValue(row, 1));
                    question.setQuestionScore(getNumericCellValue(row, 7));
                    question.setPartId(currentListeningPart.getPartId());
                    List<SaveListAnswerDTO> listAnswerDTO = processAnswers(
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
            CreateListQuestionByExcelFileResponse result = new CreateListQuestionByExcelFileResponse();
            result.setQuestions(listeningParts);
            return result;
        } catch (Exception e) {

            throw new CustomException(part == 3 ? Error.CAN_NOT_CREATE_PART_3_BY_EXCEL : Error.CAN_NOT_CREATE_PART_4_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileResponse parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

        if (part != 6 && part != 7)
            throw new BadRequestException("Invalid Part Value. It must be either 6 or 7");

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);
            List<CreateQuestionByExcelFileResponse> readingParts = new ArrayList<>();
            CreateQuestionByExcelFileResponse currentReadingPart = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String firstCellValue = getStringCellValue(row, 0);
                if (firstCellValue.equalsIgnoreCase("Question Content")) {
                    if (currentReadingPart != null) {
                        readingParts.add(currentReadingPart);
                    }
                    currentReadingPart = new CreateQuestionByExcelFileResponse();
                    currentReadingPart.setPartId(part == 6 ? partRepository.findByPartName(PartConstant.PART_6).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId() : partRepository.findByPartName(PartConstant.PART_7).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId());
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
                    CreateQuestionByExcelFileResponse question = new CreateQuestionByExcelFileResponse();
                    question.setQuestionContent(getStringCellValue(row, 1));
                    question.setQuestionScore(getNumericCellValue(row, 7));
                    question.setPartId(currentReadingPart.getPartId());
                    List<SaveListAnswerDTO> listAnswerDTO = processAnswers(
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
            CreateListQuestionByExcelFileResponse result = new CreateListQuestionByExcelFileResponse();
            result.setQuestions(readingParts);
            return result;
        } catch (Exception e) {

            throw new CustomException(part == 6 ? Error.CAN_NOT_CREATE_PART_6_BY_EXCEL : Error.CAN_NOT_CREATE_PART_7_BY_EXCEL);
        }
    }

    @Transactional
    @Override
    public CreateListQuestionByExcelFileResponse parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file to upload");

        if (!ExcelUtil.isExcelFile(file))
            throw new CustomException(Error.FILE_IMPORT_IS_NOT_EXCEL);

        CreateListQuestionByExcelFileResponse result = new CreateListQuestionByExcelFileResponse();
        List<CreateQuestionByExcelFileResponse> allQuestions = new ArrayList<>();
        for (int part : new int[]{1, 2}) {
            CreateListQuestionByExcelFileResponse part12DTO = parseListeningPart12DTO(topicId, file, part);
            allQuestions.addAll(part12DTO.getQuestions());
        }
        for (int part : new int[]{3, 4}) {
            CreateListQuestionByExcelFileResponse part34DTO = parseListeningPart34DTO(topicId, file, part);
            allQuestions.addAll(part34DTO.getQuestions());
        }
        CreateListQuestionByExcelFileResponse part5DTO = parseReadingPart5DTO(topicId, file);
        allQuestions.addAll(part5DTO.getQuestions());
        for (int part : new int[]{6, 7}) {
            CreateListQuestionByExcelFileResponse part67DTO = parseReadingPart67DTO(topicId, file, part);
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
            UUID uuid = partRepository.findByPartName(part).orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND)).getPartId();
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

    private List<SaveListAnswerDTO> processAnswers(String optionA, String optionB, String optionC, String optionD, String result) {
        List<SaveListAnswerDTO> listAnswerDTO = new ArrayList<>();
        listAnswerDTO.add(createAnswerDTO(optionA, result));
        listAnswerDTO.add(createAnswerDTO(optionB, result));
        listAnswerDTO.add(createAnswerDTO(optionC, result));
        listAnswerDTO.add(createAnswerDTO(optionD, result));
        return listAnswerDTO;
    }

    private SaveListAnswerDTO createAnswerDTO(String option, String result) {
        SaveListAnswerDTO answerDTO = new SaveListAnswerDTO();
        answerDTO.setContentAnswer(option);
        answerDTO.setCorrectAnswer(option != null && option.equalsIgnoreCase(result));
        return answerDTO;
    }

}
