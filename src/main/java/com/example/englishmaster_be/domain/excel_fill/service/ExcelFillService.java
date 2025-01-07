package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.common.constant.PartEnum;
import com.example.englishmaster_be.common.constant.excel.ExcelHeaderContentConstant;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.answer.AnswerRepository;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.part.PartRepository;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.part.service.IPartService;
import com.example.englishmaster_be.helper.ExcelHelper;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.question.QuestionRepository;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.util.ContentUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFillService implements IExcelFillService {

    ContentUtil contentUtil;

    PartRepository partRepository;

    ContentRepository contentRepository;

    QuestionRepository questionRepository;

    AnswerRepository answerRepository;

    IPackService packService;

    IPartService partService;

    ITopicService topicService;

    IUserService userService;


    @Transactional
    @Override
    public ExcelTopicResponse parseCreateTopicDTO(MultipartFile file) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file storage to upload");

        if(ExcelHelper.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %s does not exist", 0));

            String topicName = ExcelHelper.getCellValueAsString(sheet, 0, 1);
            String topicImageName = ExcelHelper.getCellValueAsString(sheet, 1, 1);
            String topicDescription = ExcelHelper.getCellValueAsString(sheet, 2, 1);
            String topicType = ExcelHelper.getCellValueAsString(sheet, 3, 1);
            String workTime = ExcelHelper.getCellValueAsString(sheet, 4, 1).replace(".0", "");
            int numberQuestion = ExcelHelper.getIntCellValue(sheet, 5, 1);
            String topicPackName = ExcelHelper.getCellValueAsString(sheet, 6, 1);
            List<UUID> parts = parseParts(ExcelHelper.getCellValueAsString(sheet, 7, 1));
            return ExcelTopicResponse.builder()
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
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part) {

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file excel to upload");

        if (part != 1 && part != 2)
            throw new BadRequestException("Invalid Part value. It must be either 1 or 2");

        if(ExcelHelper.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %s does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if(firstRow == null)
                throw new BadRequestException("First row for part name is required in sheet with name is PART 1 or PART 2 !");

            String partNameAtFirstRow = ExcelHelper.getStringCellValue(firstRow, 0);

            if(
                    !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_1.getName()) && part == 1
                            || !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_2.getName()) && part == 2
            ) throw new BadRequestException("Part name at first row must defined with PART 1 or PART 2.");

            PartEntity partEntity;

            if(partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_1.getName()))
                partEntity = partService.getPartToName(PartEnum.PART_1.getName());
            else partEntity = partService.getPartToName(PartEnum.PART_2.getName());

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            int iRowTotalScore = iRowAudioPath + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelHelper.collectContentPart1234567With(sheet, iRowAudioPath, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partEntity)
                    .topics(List.of(topicEntity))
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionTypeEnum.Question_Parent)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .build();

            questionParent = questionRepository.save(questionParent);

            if(questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new ArrayList<>());

            if(questionParent.getContentCollection() == null)
                questionParent.setContentCollection(new ArrayList<>());

            ContentEntity contentAudio = contentUtil.makeQuestionContentEntity(
                    currentUser,
                    questionParent,
                    excelQuestionContentResponse.getAudioPath()
            );

            contentAudio = contentRepository.save(contentAudio);
            questionParent.getContentCollection().add(contentAudio);

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelHelper.checkHeaderTabQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 3 dòng vì đã đọc được audio path, total score và check structure header table
            countRowWillFetch -= 3;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0){

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionResult = 1;
                int jColQuestionScore = 2;

                String resultTrueQuestion = ExcelHelper.getStringCellValue(rowBodyTable, jColQuestionResult);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .questionScore(scoreTrueQuestion)
                        .questionGroupParent(questionParent)
                        .isQuestionParent(Boolean.FALSE)
                        .questionResult(resultTrueQuestion)
                        .questionType(QuestionTypeEnum.Multiple_Choice)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

                questionChildren = questionRepository.save(questionChildren);

                if(part == 1){

                    int jColQuestionImage = 3;

                    String imageQuestion = ExcelHelper.getStringCellValue(rowBodyTable, jColQuestionImage);

                    ContentEntity contentImage = contentUtil.makeQuestionContentEntity(
                            currentUser,
                            questionChildren,
                            imageQuestion
                    );

                    contentImage = contentRepository.save(contentImage);

                    if (questionChildren.getContentCollection() == null)
                        questionChildren.setContentCollection(new ArrayList<>());

                    questionChildren.getContentCollection().add(contentImage);
                }

                questionParent.getQuestionGroupChildren().add(questionChildren);

                countRowWillFetch--;

            }

            ExcelQuestionResponse excelQuestionResponse = QuestionMapper.INSTANCE.toExcelQuestionResponse(questionParent);

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

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file excel to upload");

        if(ExcelHelper.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        UserEntity currentUser = userService.currentUser();

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        int part = 5;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %s does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if(firstRow == null)
                throw new BadRequestException("First row for part name is required in sheet with name is PART 5!");

            String partNameAtFirstRow = ExcelHelper.getStringCellValue(firstRow, 0);

            if(
                    !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_5.getName())
            ) throw new BadRequestException("Part name at first row must defined with PART 5.");

            PartEntity partEntity = partService.getPartToName(PartEnum.PART_5.getName());

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowTotalScore = iRowPartName + 1;

            ExcelQuestionContentResponse excelQuestionContentResponse = ExcelHelper.collectContentPart1234567With(sheet, null, null, iRowTotalScore, part);

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partEntity)
                    .topics(List.of(topicEntity))
                    .userCreate(currentUser)
                    .userUpdate(currentUser)
                    .isQuestionParent(Boolean.TRUE)
                    .questionType(QuestionTypeEnum.Question_Parent)
                    .questionScore(excelQuestionContentResponse.getTotalScore())
                    .build();

            questionParent = questionRepository.save(questionParent);

            if(questionParent.getQuestionGroupChildren() == null)
                questionParent.setQuestionGroupChildren(new ArrayList<>());

            int iRowHeaderTable = iRowTotalScore + 1;

            ExcelHelper.checkHeaderTabQuestionPart1234567With(sheet, iRowHeaderTable, part);

            // bỏ qua 2 dòng vì đã đọc được total score và check structure header table
            countRowWillFetch -= 2;

            int totalRowInSheet = sheet.getLastRowNum();

            while (countRowWillFetch >= 0){

                int iRowBodyTable = totalRowInSheet - countRowWillFetch;

                Row rowBodyTable = sheet.getRow(iRowBodyTable);

                int jColQuestionContent = 1;
                int jColResultTrueAnswer = 6;
                int jColQuestionScore = 7;

                String questionContent = ExcelHelper.getStringCellValue(rowBodyTable, jColQuestionContent);
                String resultTrueQuestion = ExcelHelper.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                QuestionEntity questionChildren = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .questionContent(questionContent)
                        .questionScore(scoreTrueQuestion)
                        .questionGroupParent(questionParent)
                        .isQuestionParent(Boolean.FALSE)
                        .questionResult(resultTrueQuestion)
                        .questionType(QuestionTypeEnum.Multiple_Choice_To_Fill_In_Blank)
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .build();

                questionChildren = questionRepository.save(questionChildren);

                if(questionChildren.getAnswers() == null)
                    questionChildren.setAnswers(new ArrayList<>());

                int jA_Begin = 2;
                int jD_Last = 5;

                for(int j = jA_Begin; j <= jD_Last; j++) {

                    String answerContent = ExcelHelper.getStringCellValue(rowBodyTable, j);

                    AnswerEntity answerEntity = AnswerEntity.builder()
                            .answerId(UUID.randomUUID())
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

            ExcelQuestionResponse excelQuestionResponse = QuestionMapper.INSTANCE.toExcelQuestionResponse(questionParent);

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

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file excel to upload");

        if (part != 3 && part != 4)
            throw new BadRequestException("Invalid Part value. It must be either 3 or 4");

        if(ExcelHelper.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %s does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if(firstRow == null)
                throw new BadRequestException("First row for part name is required in sheet with name is PART 3 or PART 4!");

            String partNameAtFirstRow = ExcelHelper.getStringCellValue(firstRow, 0);

            if(
                    !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_3.getName()) && part == 3
                || !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_4.getName()) && part == 4
            ) throw new BadRequestException("Part name at first row must defined with PART 3 or PART 4.");

            PartEntity partEntity;

            if(partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_3.getName()))
                partEntity = partService.getPartToName(PartEnum.PART_3.getName());
            else partEntity = partService.getPartToName(PartEnum.PART_4.getName());

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowAudioPath = iRowPartName + 1;

            while(countRowWillFetch >= 0) {

                int iRowImage = iRowAudioPath + 1;
                int iRowTotalScore = iRowImage + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelHelper.collectContentPart1234567With(sheet, iRowAudioPath, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partEntity)
                        .topics(List.of(topicEntity))
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionTypeEnum.Question_Parent)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .build();

                questionParent = questionRepository.save(questionParent);

                if(questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new ArrayList<>());

                if(questionParent.getContentCollection() == null)
                    questionParent.setContentCollection(new ArrayList<>());

                ContentEntity contentAudio = contentUtil.makeQuestionContentEntity(
                        currentUser,
                        questionParent,
                        excelQuestionContentResponse.getAudioPath()
                );

                contentAudio = contentRepository.save(contentAudio);
                questionParent.getContentCollection().add(contentAudio);

                ContentEntity contentImage = contentUtil.makeQuestionContentEntity(
                        currentUser,
                        questionParent,
                        excelQuestionContentResponse.getImagePath()
                );

                contentImage = contentRepository.save(contentImage);
                questionParent.getContentCollection().add(contentImage);

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelHelper.checkHeaderTabQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 4 dòng vị đã dọc audio, image, score, và check structure header table
                countRowWillFetch -= 4;

                while(true){

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if(rowBodyTable == null || countRowWillFetch < 0){
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if(
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                            && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelHeaderContentConstant.Audio.getHeaderName())
                    ) {

                        iRowAudioPath = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContent = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContent = ExcelHelper.getStringCellValue(rowBodyTable, jColQuestionContent);
                    String resultTrueQuestion = ExcelHelper.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .questionContent(questionContent)
                            .questionScore(scoreTrueQuestion)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionResult(resultTrueQuestion)
                            .questionType(QuestionTypeEnum.Multiple_Choice)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);

                    if(questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new ArrayList<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for(int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelHelper.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerId(UUID.randomUUID())
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

                ExcelQuestionResponse excelQuestionResponse = QuestionMapper.INSTANCE.toExcelQuestionResponse(questionParent);

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

        if(file == null || file.isEmpty())
            throw new BadRequestException("Please select a file excel to upload");

        if (part != 6 && part != 7)
            throw new BadRequestException("Invalid Part value. It must be either 6 or 7");

        if(ExcelHelper.isExcelFile(file))
            throw new CustomException(ErrorEnum.FILE_IMPORT_IS_NOT_EXCEL);

        TopicEntity topicEntity = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(part);

            if (sheet == null)
                throw new BadRequestException(String.format("Sheet %s does not exist", part));

            int iRowPartName = 0;

            Row firstRow = sheet.getRow(iRowPartName);

            if(firstRow == null)
                throw new BadRequestException("First row for part name is required in sheet with name is PART 6 or PART 7!");

            String partNameAtFirstRow = ExcelHelper.getStringCellValue(firstRow, 0);

            if(
                    !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_6.getName()) && part == 6
                            || !partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_7.getName()) && part == 7
            ) throw new BadRequestException("Part name at first row must defined with PART 6 or PART 7.");

            PartEntity partEntity;

            if(partNameAtFirstRow.equalsIgnoreCase(PartEnum.PART_6.getName()))
                partEntity = partService.getPartToName(PartEnum.PART_6.getName());
            else partEntity = partService.getPartToName(PartEnum.PART_7.getName());

            List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

            // bỏ qua dòng đầu tiên vì đã được đọc
            int countRowWillFetch = sheet.getLastRowNum() - 1;

            int iRowQuestionContent = iRowPartName + 1;

            while(countRowWillFetch >= 0) {

                Integer iRowImage = part == 7 ? iRowQuestionContent + 1 : null;
                int iRowTotalScore = part == 7 ? iRowImage + 1 : iRowQuestionContent + 1;

                ExcelQuestionContentResponse excelQuestionContentResponse = ExcelHelper.collectContentPart1234567With(sheet, iRowQuestionContent, iRowImage, iRowTotalScore, part);

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partEntity)
                        .topics(List.of(topicEntity))
                        .userCreate(currentUser)
                        .userUpdate(currentUser)
                        .isQuestionParent(Boolean.TRUE)
                        .questionType(QuestionTypeEnum.Question_Parent)
                        .questionScore(excelQuestionContentResponse.getTotalScore())
                        .questionContent(excelQuestionContentResponse.getQuestionContent())
                        .build();

                questionParent = questionRepository.save(questionParent);

                if(questionParent.getQuestionGroupChildren() == null)
                    questionParent.setQuestionGroupChildren(new ArrayList<>());

                if(part == 7){

                    if(questionParent.getContentCollection() == null)
                        questionParent.setContentCollection(new ArrayList<>());

                    ContentEntity contentImage = contentUtil.makeQuestionContentEntity(
                            currentUser,
                            questionParent,
                            excelQuestionContentResponse.getImagePath()
                    );

                    contentImage = contentRepository.save(contentImage);
                    questionParent.getContentCollection().add(contentImage);
                }

                int iRowHeaderTable = iRowTotalScore + 1;

                ExcelHelper.checkHeaderTabQuestionPart1234567With(sheet, iRowHeaderTable, part);

                int iRowBodyTable = iRowHeaderTable + 1;

                //bỏ qua 2 dòng nếu là part 6 hay 3 dòng nếu là part 7 vị đã dọc question content, image, score, và check structure header table
                countRowWillFetch -= part == 7 ? 3 : 2;

                while(true){

                    Row rowBodyTable = sheet.getRow(iRowBodyTable);

                    if(rowBodyTable == null || countRowWillFetch < 0){
                        countRowWillFetch = -1;
                        break;
                    }

                    Cell cellQuestionContent = rowBodyTable.getCell(0);

                    if(
                            cellQuestionContent.getCellType().equals(CellType.STRING)
                                    && cellQuestionContent.getStringCellValue().equalsIgnoreCase(ExcelHeaderContentConstant.Question_Content.getHeaderName())
                    ) {

                        iRowQuestionContent = iRowBodyTable;
                        break;
                    }

                    int jColQuestionContentChild = 1;
                    int jColResultTrueAnswer = 6;
                    int jColQuestionScore = 7;

                    String questionContentChild = ExcelHelper.getStringCellValue(rowBodyTable, jColQuestionContentChild);
                    String resultTrueQuestion = ExcelHelper.getStringCellValue(rowBodyTable, jColResultTrueAnswer);
                    int scoreTrueQuestion = (int) rowBodyTable.getCell(jColQuestionScore).getNumericCellValue();

                    QuestionEntity questionChildren = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .questionContent(questionContentChild)
                            .questionScore(scoreTrueQuestion)
                            .questionGroupParent(questionParent)
                            .isQuestionParent(Boolean.FALSE)
                            .questionResult(resultTrueQuestion)
                            .questionType(QuestionTypeEnum.Multiple_Choice)
                            .userCreate(currentUser)
                            .userUpdate(currentUser)
                            .build();

                    questionChildren = questionRepository.save(questionChildren);

                    if(questionChildren.getAnswers() == null)
                        questionChildren.setAnswers(new ArrayList<>());

                    int jA_Begin = 2;
                    int jD_Last = 5;

                    for(int j = jA_Begin; j <= jD_Last; j++) {

                        String answerContent = ExcelHelper.getStringCellValue(rowBodyTable, j);

                        AnswerEntity answerEntity = AnswerEntity.builder()
                                .answerId(UUID.randomUUID())
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

                ExcelQuestionResponse excelQuestionResponse = QuestionMapper.INSTANCE.toExcelQuestionResponse(questionParent);

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
    public ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file) {

        List<ExcelQuestionResponse> excelQuestionResponseList = new ArrayList<>();

        List.of(1, 2).forEach(
                part ->{

                    ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart12Excel(topicId, file, part);

                    excelQuestionResponseList.addAll(excelQuestionListResponse.getQuestions());
                });

        List.of(3, 4).forEach(
                part ->{

                    ExcelQuestionListResponse excelQuestionListResponse = importQuestionListeningPart34Excel(topicId, file, part);

                    excelQuestionResponseList.addAll(excelQuestionListResponse.getQuestions());
                });


        ExcelQuestionListResponse excelQuestionP5ListResponse = importQuestionReadingPart5Excel(topicId, file);

        excelQuestionResponseList.addAll(excelQuestionP5ListResponse.getQuestions());

        List.of(6, 7).forEach(
                part ->{

                    ExcelQuestionListResponse excelQuestionListResponse = importQuestionReadingPart67Excel(topicId, file, part);

                    excelQuestionResponseList.addAll(excelQuestionListResponse.getQuestions());
                });

        return ExcelQuestionListResponse.builder()
                .questions(excelQuestionResponseList)
                .build();
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


}
