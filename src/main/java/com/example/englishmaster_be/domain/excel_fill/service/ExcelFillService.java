package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.common.constant.PartType;
import com.example.englishmaster_be.common.constant.excel.ExcelQuestionConstant;
import com.example.englishmaster_be.domain.excel_fill.dto.response.*;
import com.example.englishmaster_be.domain.status.service.IStatusService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.mapper.ExcelContentMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.pack.PackQueryFactory;
import com.example.englishmaster_be.model.pack.PackRepository;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.PartQueryFactory;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.util.ExcelUtil;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.model.status.StatusEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.topic.TopicQueryFactory;
import com.example.englishmaster_be.model.topic.TopicRepository;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.helper.ContentHelper;
import com.example.englishmaster_be.helper.FileHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFillService implements IExcelFillService {

    FileHelper fileUtil;

    ContentHelper contentUtil;

    PartQueryFactory partQueryFactory;

    PackQueryFactory packQueryFactory;

    TopicQueryFactory topicQueryFactory;

    PartRepository partRepository;

    ContentRepository contentRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    TopicRepository topicRepository;

    PackRepository packRepository;

    IPartService partService;

    ITopicService topicService;

    IUserService userService;

    IStatusService statusService;


    @Override
    @SneakyThrows
    public ExcelTopicContentResponse readTopicContentFromExcel(MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", sheetNumber));

            return ExcelUtil.collectTopicContentWith(sheet);
        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicResponse importTopicExcel(MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        StatusEntity statusEntity = statusService.getStatusByName("ACTIVE");

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", sheetNumber));

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            PackEntity packEntity = packQueryFactory.findPackByName(excelTopicContentResponse.getPackName())
                    .orElse(null);

            if (packEntity == null) {

                packEntity = PackEntity.builder()
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .packName(excelTopicContentResponse.getPackName())
                        .build();

                packEntity = packRepository.save(packEntity);
            }

            TopicEntity topicEntity = topicQueryFactory.findTopicByNameAndPack(excelTopicContentResponse.getTopicName(), packEntity)
                    .orElse(null);

            if (topicEntity == null)
                topicEntity = TopicEntity.builder()
                        .status(statusEntity)
                        .pack(packEntity)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

            TopicMapper.INSTANCE.flowToTopicEntity(excelTopicContentResponse, topicEntity);

            topicEntity = topicRepository.save(topicEntity);

            if (topicEntity.getContents() == null)
                topicEntity.setContents(new ArrayList<>());

            if (topicEntity.getParts() == null)
                topicEntity.setParts(new ArrayList<>());

            ContentEntity contentImage = contentUtil.makeContentEntity(
                    currentUser,
                    topicEntity,
                    excelTopicContentResponse.getTopicImage()
            );

            contentImage = contentRepository.save(contentImage);

            topicEntity.setTopicImage(contentImage.getContentData());

            int partNamesSize = excelTopicContentResponse.getPartNamesList().size();

            for (int i = 0; i < partNamesSize; i++) {

                String partNameAtI = excelTopicContentResponse.getPartNamesList().get(i);

                String partTypeAtI = excelTopicContentResponse.getPartTypesList().get(i);

                PartEntity partEntity = partQueryFactory.findPartByNameAndType(partNameAtI, partTypeAtI)
                        .orElse(null);

                if (partEntity == null) {

                    partEntity = PartEntity.builder()
                            .contentData("")
                            .contentType(fileUtil.mimeTypeFile(""))
                            .partName(partNameAtI)
                            .partType(partTypeAtI)
                            .partDescription(String.join(": ", List.of(partNameAtI, partTypeAtI)))
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    partEntity = partRepository.save(partEntity);
                }

                if (!topicEntity.getParts().contains(partEntity))
                    topicEntity.getParts().add(partEntity);
            }

            if (topicEntity.getTopicName().contains("TOEIC")) {
                topicEntity.setTopicType("TOEIC");
            }
            if (topicEntity.getTopicName().contains("IELTS")) {
                topicEntity.setTopicType("IELTS");
            }

            topicEntity = topicRepository.save(topicEntity);

            return ExcelContentMapper.INSTANCE.toExcelTopicResponse(topicEntity);

        }
//        catch (Exception e) {
//            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
//        }

    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicResponse importAllPartsForTopicExcel(UUID topicId, MultipartFile file) {

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int sheetNumber = 0;

            Sheet sheet = workbook.getSheetAt(sheetNumber);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", sheetNumber));

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            List<String> partNamesList = excelTopicContentResponse.getPartNamesList();
            List<String> partTypesList = excelTopicContentResponse.getPartTypesList();

            if (partNamesList == null || partNamesList.isEmpty())
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Part name list is not exist or empty in sheet %d", sheetNumber));

            if (partTypesList == null || partTypesList.isEmpty())
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Part type list is not exist or empty in sheet %d", sheetNumber));

            if (topicEntity.getParts() == null)
                topicEntity.setParts(new ArrayList<>());

            int partNamesSize = partNamesList.size();

            for (int i = 0; i < partNamesSize; i++) {

                String partName = partNamesList.get(i);
                String partType = partTypesList.get(i);

                PartEntity partEntity = partQueryFactory.findPartByNameAndType(partName, partType).orElse(null);

                if (partEntity == null) {

                    partEntity = PartEntity.builder()
                            .partId(UUID.randomUUID())
                            .partName(partName)
                            .partType(partType)
                            .partDescription(String.join(": ", List.of(partName, partType)))
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    partEntity = partRepository.save(partEntity);
                }

                if (!topicEntity.getParts().contains(partEntity))
                    topicEntity.getParts().add(partEntity);
            }

            topicEntity.setUserUpdate(currentUser);

            topicEntity = topicRepository.save(topicEntity);
        }

        return ExcelContentMapper.INSTANCE.toExcelTopicResponse(topicEntity);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 1 && part != 2)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 1 or 2");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 1 or PART 2 !");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_1_TOEIC.getName()) && part == 1
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_2_TOEIC.getName()) && part == 2
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 1 or PART 2.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_1_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_1_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_2_TOEIC.getName(), partType, topicEntity);

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            int iRowTotalScore = iRowAudioPath + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowAudioPath, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .part(partEntity)
                    .topics(List.of(topicEntity))
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionType.Question_Parent)
                    .build();

            questionParent = questionRepository.save(questionParent);

            if (questionParent.getContentCollection() == null)
                questionParent.setContentCollection(new ArrayList<>());

            ContentEntity contentAudio = contentUtil.makeContentEntity(
                    currentUser,
                    topicEntity,
                    excelQuestionContentResponse.getAudioPath()
            );

            contentAudio = contentRepository.save(contentAudio);

            questionParent.setContentAudio(contentAudio.getContentData());

            if (!questionParent.getContentCollection().contains(contentAudio))
                questionParent.getContentCollection().add(contentAudio);

            if (questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new ArrayList<>());

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 3 dòng vì đã đọc được audio path, total score và check structure header table
            countRowWillFetch -= 3;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0) {

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionResult = 1;
                int jColQuestionScore = 2;

                String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionResult);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionGroupParent(questionParent)
                        .part(partEntity)
                        .isQuestionParent(Boolean.FALSE)
                        .questionResult(resultTrueQuestion)
                        .questionType(QuestionType.Single_Choice)
                        .numberChoice(1)
                        .questionScore(scoreTrueQuestion)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

                if (part == 1) {

                    if (questionChildren.getContentCollection() == null)
                        questionChildren.setContentCollection(new ArrayList<>());

                    int jColQuestionImage = 3;

                    String imageQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionImage);

                    ContentEntity contentImage = contentUtil.makeContentEntity(
                            currentUser,
                            topicEntity,
                            imageQuestion
                    );

                    contentImage = contentRepository.save(contentImage);

                    questionChildren.setContentImage(contentImage.getContentData());

                    if (!questionChildren.getContentCollection().contains(contentImage))
                        questionChildren.getContentCollection().add(contentImage);

                }

                questionChildren = questionRepository.save(questionChildren);
                topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                if (questionChildren.getAnswers() == null)
                    questionChildren.setAnswers(new ArrayList<>());

                String[] keys = new String[]{
                        "A", "B", "C", "D"
                };

                int keysLength = keys.length;

                for (int i = 0; i < keysLength; i++) {

                    AnswerEntity answer = AnswerEntity.builder()
                            .question(questionChildren)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .answerContent(keys[i])
                            .correctAnswer(keys[i].equalsIgnoreCase(resultTrueQuestion))
                            .build();

                    answer = answerRepository.save(answer);

                    questionChildren.getAnswers().add(answer);

                }

                questionParent.getQuestionGroupChildren().add(questionChildren);

                countRowWillFetch--;

            }

            ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

            excelQuestionResponse.setTopicId(topicId);

            List<ExcelQuestionResponse> excelQuestionResponseList = List.of(excelQuestionResponse);

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();

        }
//        catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException(part == 1 ? ErrorEnum.CAN_NOT_CREATE_PART_1_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_2_BY_EXCEL);
//        }
    }


    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionReadingPart5Excel(UUID topicId, MultipartFile file) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        int part = 5;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 5!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_5_TOEIC.getName())
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 5.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity = partService.getPartToName(PartType.PART_5_TOEIC.getName(), partType, topicEntity);

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowTotalScore = iRowPartName + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, null, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .part(partEntity)
                    .topics(List.of(topicEntity))
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionType.Question_Parent)
                    .build();

            questionParent = questionRepository.save(questionParent);

            if (questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new ArrayList<>());

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 2 dòng vì đã đọc được total score và check structure header table
            countRowWillFetch -= 2;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0) {

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionContent = 1;
                int jColResultTrueAnswer = 6;
                int jColQuestionScore = 7;

                String questionContent = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContent);
                String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionContent(questionContent)
                        .part(partEntity)
                        .questionGroupParent(questionParent)
                        .isQuestionParent(Boolean.FALSE)
                        .questionType(QuestionType.Single_Choice)
                        .numberChoice(1)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(scoreTrueQuestion)
                        .questionResult(resultTrueQuestion)
                        .build();

                questionChildren = questionRepository.save(questionChildren);
                topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                if (questionChildren.getAnswers() == null)
                    questionChildren.setAnswers(new ArrayList<>());

                int jA_Begin = 2;
                int jD_Last = 5;

                for (int j = jA_Begin; j <= jD_Last; j++) {

                    String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                    AnswerEntity answerEntity = AnswerEntity.builder()
                            .answerContent(answerContent)
                            .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                            .question(questionChildren)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    answerEntity = answerRepository.save(answerEntity);

                    questionChildren.getAnswers().add(answerEntity);
                }

                questionParent.getQuestionGroupChildren().add(questionChildren);

                countRowWillFetch--;

            }

            ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

            excelQuestionResponse.setTopicId(topicId);

            List<ExcelQuestionResponse> excelQuestionResponseList = List.of(excelQuestionResponse);

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();
        }
//        catch (Exception e) {
//            throw new CustomException(ErrorEnum.CAN_NOT_CREATE_TOPIC_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionListeningPart34Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 3 && part != 4)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 3 or 4");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 3 or PART 4!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_3_TOEIC.getName()) && part == 3
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_4_TOEIC.getName()) && part == 4
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 3 or PART 4.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_3_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_3_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_4_TOEIC.getName(), partType, topicEntity);

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            while (countRowWillFetch >= 0) {

                int iRowImage = iRowAudioPath + 1;
                int iRowTotalScore = iRowImage + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowAudioPath, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .part(partEntity)
                        .topics(List.of(topicEntity))
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionType.Question_Parent)
                        .build();

                questionParent = questionRepository.save(questionParent);

                if (questionParent.getContentCollection() == null)
                    questionParent.setContentCollection(new ArrayList<>());

                ContentEntity contentAudio = contentUtil.makeContentEntity(
                        currentUser,
                        topicEntity,
                        excelQuestionContentResponse.getAudioPath()
                );

                contentAudio = contentRepository.save(contentAudio);

                questionParent.setContentAudio(contentAudio.getContentData());

                if (!questionParent.getContentCollection().contains(contentAudio))
                    questionParent.getContentCollection().add(contentAudio);

                ContentEntity contentImage = contentUtil.makeContentEntity(
                        currentUser,
                        topicEntity,
                        excelQuestionContentResponse.getImagePath()
                );

                contentImage = contentRepository.save(contentImage);

                questionParent.setContentImage(contentImage.getContentData());

                if (!questionParent.getContentCollection().contains(contentImage))
                    questionParent.getContentCollection().add(contentImage);

                if (questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new ArrayList<>());

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 4 dòng vị đã dọc audio, image, score, và check structure header table
                countRowWillFetch -= 4;

                while (true) {

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if (rowBodyTable == null || countRowWillFetch < 0) {
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if (
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                                    && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelQuestionConstant.Audio.getHeaderName())
                    ) {

                        iRowAudioPath = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContent = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContent = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContent);
                    String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionContent(questionContent)
                            .part(partEntity)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionType(QuestionType.Single_Choice)
                            .numberChoice(1)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .questionScore(scoreTrueQuestion)
                            .questionResult(resultTrueQuestion)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);
                    topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                    if (questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new ArrayList<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for (int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                                .question(questionChildren)
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();

                        answerEntity = answerRepository.save(answerEntity);

                        questionChildren.getAnswers().add(answerEntity);
                    }

                    questionParent.getQuestionGroupChildren().add(questionChildren);

                    countRowWillFetch--;

                    iRowBodyTable++;
                }

                ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

                excelQuestionResponse.setTopicId(topicId);

                excelQuestionResponseList.add(excelQuestionResponse);
            }

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();

        }
//        catch (Exception e) {
//
//            throw new CustomException(part == 3 ? ErrorEnum.CAN_NOT_CREATE_PART_3_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_4_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionReadingPart67Excel(UUID topicId, MultipartFile file, int part) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Please select a file excel to upload");

        if (part != 6 && part != 7)
            throw new ErrorHolder(Error.BAD_REQUEST, "Invalid Part value. It must be either 6 or 7");

        if (ExcelUtil.isExcelFile(file))
            throw new ErrorHolder(Error.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            int numberOfSheetTopicInformation = 0;

            Sheet sheet = workbook.getSheetAt(numberOfSheetTopicInformation);

            ExcelTopicContentResponse excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);

            sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new ErrorHolder(Error.BAD_REQUEST, String.format("Sheet %d does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if (firstRow == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "First row for part name is required in sheet with name is PART 6 or PART 7!");

            String partNameAtFirstRow = ExcelUtil.getStringCellValue(firstRow, 0);

            if (
                    !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_6_TOEIC.getName()) && part == 6
                            || !partNameAtFirstRow.equalsIgnoreCase(PartType.PART_7_TOEIC.getName()) && part == 7
            ) throw new ErrorHolder(Error.BAD_REQUEST, "Part name at first row must defined with PART 6 or PART 7.");

            String partType = excelTopicContentResponse.getPartTypesList().get(part - 1);

            PartEntity partEntity;

            if (partNameAtFirstRow.equalsIgnoreCase(PartType.PART_6_TOEIC.getName()))
                partEntity = partService.getPartToName(PartType.PART_6_TOEIC.getName(), partType, topicEntity);
            else partEntity = partService.getPartToName(PartType.PART_7_TOEIC.getName(), partType, topicEntity);

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowQuestionContent = iRowPartName + 1;

            while (countRowWillFetch >= 0) {

                Integer iRowImage = part == 7 ? iRowQuestionContent + 1 : null;
                int iRowTotalScore = part == 7 ? iRowImage + 1 : iRowQuestionContent + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelUtil.collectQuestionContentPart1234567With(sheet, iRowQuestionContent, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .part(partEntity)
                        .topics(List.of(topicEntity))
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionType.Question_Parent)
                        .questionContent(excelQuestionContentResponse.getQuestionContent())
                        .build();

                questionParent = questionRepository.save(questionParent);

                if (part == 7) {

                    if (questionParent.getContentCollection() == null)
                        questionParent.setContentCollection(new ArrayList<>());

                    ContentEntity contentImage = contentUtil.makeContentEntity(
                            currentUser,
                            topicEntity,
                            excelQuestionContentResponse.getImagePath()
                    );

                    contentImage = contentRepository.save(contentImage);

                    questionParent.setContentImage(contentImage.getContentData());

                    if (!questionParent.getContentCollection().contains(contentImage))
                        questionParent.getContentCollection().add(contentImage);
                }

                if (questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new ArrayList<>());

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelUtil.checkHeaderTableQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 2 dòng nếu là part 6 hay 3 dòng nếu là part 7 vị đã dọc question content, image, score, và check structure header table
                countRowWillFetch -= part == 7 ? 3 : 2;

                while (true) {

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if (rowBodyTable == null || countRowWillFetch < 0) {
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if (
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                                    && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelQuestionConstant.Question_Content.getHeaderName())
                    ) {

                        iRowQuestionContent = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContentChild = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContentChild = ExcelUtil.getStringCellValue(rowBodyTable, jColQuestionContentChild);
                    String resultTrueQuestion = ExcelUtil.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionContent(questionContentChild)
                            .part(partEntity)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionType(QuestionType.Single_Choice)
                            .numberChoice(1)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .questionScore(scoreTrueQuestion)
                            .questionResult(resultTrueQuestion)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);
                    topicEntity.setNumberQuestion(topicEntity.getNumberQuestion() + 1);
                    if (questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new ArrayList<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for (int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelUtil.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(resultTrueQuestion))
                                .question(questionChildren)
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();

                        answerEntity = answerRepository.save(answerEntity);

                        questionChildren.getAnswers().add(answerEntity);
                    }

                    questionParent.getQuestionGroupChildren().add(questionChildren);

                    countRowWillFetch--;

                    iRowBodyTable++;
                }

                ExcelQuestionResponse excelQuestionResponse = ExcelContentMapper.INSTANCE.toExcelQuestionResponse(questionParent);

                excelQuestionResponse.setTopicId(topicId);

                excelQuestionResponseList.add(excelQuestionResponse);
            }

            return ExcelQuestionListResponse.builder()
                    .questions(excelQuestionResponseList)
                    .build();
        }
//        catch (Exception e) {
//
//            throw new CustomException(part == 6 ? ErrorEnum.CAN_NOT_CREATE_PART_6_BY_EXCEL : ErrorEnum.CAN_NOT_CREATE_PART_7_BY_EXCEL);
//        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public List<ExcelQuestionResponse> importQuestionForTopicAndPart(UUID topicId, int partNumber, MultipartFile file) {

        ExcelUtil.checkPartInScope(partNumber);

        List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

        if (List.of(1, 2).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart12Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        } else if (List.of(3, 4).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart34Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        } else if (partNumber == 5) {

            ExcelQuestionListResponse excelQuestionP5ListResponse = importQuestionReadingPart5Excel(topicId, file);

            excelQuestionResponseList = excelQuestionP5ListResponse.getQuestions();
        } else if (List.of(6, 7).contains(partNumber)) {

            ExcelQuestionListResponse excelQuestionListResponse = importQuestionReadingPart67Excel(topicId, file, partNumber);

            excelQuestionResponseList = excelQuestionListResponse.getQuestions();
        }

        return excelQuestionResponseList;
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file) {

        ExcelTopicContentResponse excelTopicContentResponse;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            excelTopicContentResponse = ExcelUtil.collectTopicContentWith(sheet);
        }

        List<ExcelQuestionResponse> excelQuestionResponseList = excelTopicContentResponse.getPartNamesList().stream()
                .map(
                        partName -> Integer.valueOf(partName.split(" ")[1])
                )
                .flatMap(
                        partNumber -> importQuestionForTopicAndPart(topicId, partNumber, file).stream()
                ).toList();

        return ExcelQuestionListResponse.builder()
                .questions(excelQuestionResponseList)
                .build();
    }

}
