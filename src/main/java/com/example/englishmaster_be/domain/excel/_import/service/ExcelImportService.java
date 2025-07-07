package com.example.englishmaster_be.domain.excel._import.service;

import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.evaluator_writing.util.SpringApplicationContext;
import com.example.englishmaster_be.domain.excel._import.dto.ExcelContentEntityHolder;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelPartIdsRes;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelTopicPartIdsRes;
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.projection.ITopicKeyProjection;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyRes;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.repository.jdbc.TopicTypeJdbcRepository;
import com.example.englishmaster_be.domain.topic_type.repository.jpa.TopicTypeRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.answer.repository.jdbc.AnswerJdbcRepository;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.pack.repository.jdbc.PackJdbcRepository;
import com.example.englishmaster_be.domain.pack.repository.jpa.PackRepository;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.pack_type.repository.jdbc.PackTypeJdbcRepository;
import com.example.englishmaster_be.domain.pack_type.repository.jpa.PackTypeRepository;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.part.repository.jdbc.PartJdbcRepository;
import com.example.englishmaster_be.domain.part.repository.jpa.PartRepository;
import com.example.englishmaster_be.domain.question.repository.jdbc.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.topic.repository.jdbc.TopicJdbcRepository;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.topic.repository.jpa.TopicRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.util.DomUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j(topic = "EXCEL-IMPORT-SERVICE")
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelImportService implements IExcelImportService {

    PartRepository partRepository;
    TopicRepository topicRepository;
    PackRepository packRepository;
    ITopicService topicService;
    IUserService userService;
    PackTypeRepository packTypeRepository;
    PackTypeJdbcRepository packTypeJdbcRepository;
    PackJdbcRepository packJdbcRepository;
    TopicJdbcRepository topicJdbcRepository;
    PartJdbcRepository partJdbcRepository;
    QuestionJdbcRepository questionJdbcRepository;
    AnswerJdbcRepository answerJdbcRepository;
    TopicTypeRepository topicTypeRepository;
    TopicTypeJdbcRepository topicTypeJdbcRepository;

    @Transactional
    @Override
    public TopicKeyRes importTopicFromExcel(MultipartFile file, String imageUrl, String audioUrl) {

        TopicKeyRes topicKey = importTopicFromExcel(file);

        topicJdbcRepository.updateTopicImage(topicKey.getTopicId(), imageUrl);
        topicJdbcRepository.updateTopicAudio(topicKey.getTopicId(), audioUrl);

        return topicKey;
    }

    @Transactional
    @Override
    @SneakyThrows
    public TopicKeyRes importTopicFromExcel(MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheetTopic = workbook.getSheetAt(0);

            if(sheetTopic == null)
                throw new ErrorHolder(Error.BAD_REQUEST, "Sheet topic information does not existed.");

            String packTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(0).getCell(1));
            String packTypeDescription = ExcelUtil.getStringCellValue(sheetTopic.getRow(1).getCell(1));

            UUID packTypeId = packTypeRepository.findPackTypeIdByName(packTypeName);

            // Nếu pack type không tồn tại thì thêm mới
            if(packTypeId == null){
                packTypeId = packTypeJdbcRepository.insertPackType(
                        PackTypeEntity.builder()
                                .name(packTypeName)
                                .description(packTypeDescription)
                                .createdBy(userImport)
                                .updatedBy(userImport)
                                .build()
                );
            }

            String packExamName = ExcelUtil.getStringCellValue(sheetTopic.getRow(2).getCell(1));

            IPackKeyProjection packKey = packRepository.findPackIdByName(packExamName);

            UUID packId = packKey != null ? packKey.getPackId() : null;

            // Nếu pack không thuộc pack type thì thêm mới
            if(packId == null || !packTypeId.equals(packKey.getPackTypeId())){
                packId = packJdbcRepository.insertPack(
                        PackEntity.builder()
                                .packName(packExamName)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .packTypeId(packTypeId)
                                .build()
                );
            }

            String topicTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(3).getCell(1));
            UUID topicTypeId = topicTypeRepository.findIdByTypeName(topicTypeName);
            if(topicTypeId == null){
                topicTypeId = topicTypeJdbcRepository.insertTopicType(
                        TopicTypeEntity.builder()
                                .topicTypeName(topicTypeName)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build()
                );
            }

            String topicName = ExcelUtil.getStringCellValue(sheetTopic.getRow(4).getCell(1));
            String topicImage = ExcelUtil.getStringCellValue(sheetTopic.getRow(5).getCell(1));
            String topicAudio = ExcelUtil.getStringCellValue(sheetTopic.getRow(6).getCell(1));
            String topicDescription = ExcelUtil.getStringCellValue(sheetTopic.getRow(7).getCell(1));
            Cell cellWorkTime = sheetTopic.getRow(8).getCell(1);
            LocalTime workTime = LocalTime.of(1, 0, 0);
            try{
                if(cellWorkTime.getCellType().equals(CellType.STRING)){
                    workTime = LocalTime.parse(
                            ExcelUtil.getStringCellValue(cellWorkTime),
                            DateTimeFormatter.ofPattern("HH:mm[:ss]")
                    );
                }
                else{
                    workTime = LocalDateTime.parse(
                            ExcelUtil.getStringCellValue(cellWorkTime),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    ).toLocalTime();
                }
            }
            catch (Exception e){
                log.error(e.getMessage());
            }

            ITopicKeyProjection topicKey = topicRepository.findTopicIdByName(topicName);

            UUID topicId = topicKey != null ? topicKey.getTopicId() : null;

            // Nếu topic không thuộc pack thì thêm mới
            if(topicId == null || !packId.equals(topicKey.getPackId())){
                topicId = UUID.randomUUID();
                topicJdbcRepository.insertTopic(
                        TopicEntity.builder()
                                .topicId(topicId)
                                .topicName(topicName)
                                .topicImage(topicImage)
                                .topicAudio(topicAudio)
                                .topicDescription(topicDescription)
                                .numberQuestion(0)
                                .enable(false)
                                .workTime(workTime)
                                .topicDescription(topicDescription)
                                .packId(packId)
                                .topicTypeId(topicTypeId)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build()
                );
            }

            return TopicKeyRes.builder()
                    .topicId(topicId)
                    .build();
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }
    }


    @Transactional
    @Override
    @SneakyThrows
    public ExcelPartIdsRes importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file) {

        Assert.notNull(topicId, "Topic id is required.");

        TopicEntity topic = topicService.getTopicById(topicId);

        UserEntity currentUser = userService.currentUser();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Map<String, String> partNamesAndTypes = new HashMap<>();

            int sheetPartsStep = 1;
            int numberOfSheets = workbook.getNumberOfSheets();
            while(sheetPartsStep < numberOfSheets){
                Sheet sheetPart = workbook.getSheetAt(sheetPartsStep);
                String partName = ExcelUtil.getStringCellValue(sheetPart.getRow(0).getCell(0));
                String partType = ExcelUtil.getStringCellValue(sheetPart.getRow(0).getCell(1));
                partNamesAndTypes.put(partName, partType);
                sheetPartsStep++;
            }

            List<String> partNames = partNamesAndTypes.keySet().stream().toList();


            List<PartEntity> partsTopic = partNames.stream()
                    .map(partName -> {
                        String partType = partNamesAndTypes.get(partName);
                        return PartEntity.builder()
                                .partId(UUID.randomUUID())
                                .topicId(topicId)
                                .topic(topic)
                                .partType(partType)
                                .partDescription(String.format("%s: %s", partName, partType))
                                .userCreate(currentUser)
                                .userUpdate(currentUser)
                                .build();
                    })
                    .toList();

            partJdbcRepository.batchInsertPart(partsTopic);

            return ExcelPartIdsRes.builder()
                    .partIds(partRepository.findPartIdsByTopicId(topicId))
                    .build();
        }
    }

    protected ExcelContentEntityHolder fillListeningPart12FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = PartEntity.builder()
                .partId(UUID.randomUUID())
                .partName(partName)
                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                .userCreate(userImport)
                .userUpdate(userImport)
                .topicId(topicId)
                .build();
        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partTopic)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionScore(0)
                    .questionNumber(++questionParentNumber)
                    .questionType(QuestionType.Question_Parent)
                    .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                    .build();
            entityHolder.getQuestionParents().add(questionParent);

            // bỏ qua header question
            int nextRow = iRowAudio + 2;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio")){
                    iRowAudio = nextRow;
                    break;
                }

                int jQuestionResult = 1;
                int jQuestionScore = jQuestionResult + 1;
                int jContentImage = jQuestionScore + 1;
                int jContentAudio = jContentImage + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partTopic)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionNumber(++countOfQuestionsTopic)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .contentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentImage)))
                        .contentAudio(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentAudio)))
                        .build();
                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(String answerContent : List.of("A", "B", "C", "D")){
                    entityHolder.getAnswers().add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowAudio = rowQuestionChild;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    protected ExcelContentEntityHolder fillListeningPart34FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = PartEntity.builder()
                .partId(UUID.randomUUID())
                .partName(partName)
                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                .userCreate(userImport)
                .userUpdate(userImport)
                .topicId(topicId)
                .build();
        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            int iRowImage = iRowAudio + 1;
            Row rowImage = sheet.getRow(iRowImage);
            if(rowImage == null) break;

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partTopic)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionScore(0)
                    .questionNumber(++questionParentNumber)
                    .questionType(QuestionType.Question_Parent)
                    .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                    .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                    .build();
            entityHolder.getQuestionParents().add(questionParent);

            // bỏ qua header question
            int nextRow = iRowImage + 2;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio")) {
                    iRowAudio = nextRow;
                    break;
                }

                int jQuestionContent = 1;
                int jStartAnswer = jQuestionContent + 1;
                int jQuestionResult = jStartAnswer + 4;
                int jQuestionScore = jQuestionResult + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partTopic)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionNumber(++countOfQuestionsTopic)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .build();
                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    entityHolder.getAnswers().add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowAudio = rowQuestionChild;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    protected ExcelContentEntityHolder fillReadingPart5FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook){

        Sheet sheet = workbook.getSheetAt(5);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet 5 not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part 5"))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet 5 must be name is Part 5.");

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = PartEntity.builder()
                .partId(UUID.randomUUID())
                .partName(partName)
                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                .userCreate(userImport)
                .userUpdate(userImport)
                .topicId(topicId)
                .build();
        entityHolder.getParts().add(partTopic);

        QuestionEntity questionParent = QuestionEntity.builder()
                .questionId(UUID.randomUUID())
                .part(partTopic)
                .userCreate(userImport)
                .userUpdate(userImport)
                .isQuestionParent(true)
                .questionScore(0)
                .questionNumber(1)
                .questionType(QuestionType.Question_Parent)
                .build();
        entityHolder.getQuestionParents().add(questionParent);

        int countOfQuestionsTopic = 0;

        // bỏ qua header question
        int nextRow = iRowPart + 2;
        Row rowQuestionChild = sheet.getRow(nextRow);
        while (rowQuestionChild != null){

            int jQuestionContent = 1;
            int jStartAnswer = jQuestionContent + 1;
            int jQuestionResult = jStartAnswer + 4;
            int jQuestionScore = jQuestionResult + 1;

            QuestionEntity questionChild = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partTopic)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(false)
                    .questionNumber(++countOfQuestionsTopic)
                    .questionType(QuestionType.Question_Child)
                    .numberChoice(1)
                    .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                    .questionGroupParent(questionParent)
                    .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                    .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                    .build();
            entityHolder.getQuestionChilds().add(questionChild);
            questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

            for(int i = jStartAnswer; i < jQuestionResult; i++){
                String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                entityHolder.getAnswers().add(
                        AnswerEntity.builder()
                                .answerId(UUID.randomUUID())
                                .answerContent(answerContent)
                                .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .question(questionChild)
                                .build()
                );
            }
            nextRow++;
            rowQuestionChild = sheet.getRow(nextRow);
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    protected ExcelContentEntityHolder fillReadingPart67FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ErrorHolder(Error.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = PartEntity.builder()
                .partId(UUID.randomUUID())
                .partName(partName)
                .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                .userCreate(userImport)
                .userUpdate(userImport)
                .topicId(topicId)
                .build();
        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowParentContent = iRowPart + 1;
        Row rowParentContent = sheet.getRow(iRowParentContent);
        while (rowParentContent != null){

            QuestionEntity questionParent = QuestionEntity.builder()
                    .questionId(UUID.randomUUID())
                    .part(partTopic)
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .isQuestionParent(true)
                    .questionContent(ExcelUtil.getStringCellValue(rowParentContent.getCell(1)))
                    .questionScore(0)
                    .questionNumber(++questionParentNumber)
                    .questionType(QuestionType.Question_Parent)
                    .build();
            if(part == 7) {
                questionParent.setContentImage(ExcelUtil.getStringCellValue(sheet.getRow(iRowParentContent + 1).getCell(1)));
            }
            entityHolder.getQuestionParents().add(questionParent);

            // bỏ qua header question
            int nextRow = part == 6 ? iRowParentContent + 2 : iRowParentContent + 3;
            Row rowQuestionChild = sheet.getRow(nextRow);
            while (rowQuestionChild != null){

                if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).toLowerCase().contains("question")){
                    iRowParentContent = nextRow;
                    break;
                }

                int jQuestionContent = 1;
                int jStartAnswer = jQuestionContent + 1;
                int jQuestionResult = jStartAnswer + 4;
                int jQuestionScore = jQuestionResult + 1;

                QuestionEntity questionChild = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partTopic)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .isQuestionParent(false)
                        .questionNumber(++countOfQuestionsTopic)
                        .questionType(QuestionType.Question_Child)
                        .numberChoice(1)
                        .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                        .questionGroupParent(questionParent)
                        .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                        .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)))
                        .build();
                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    entityHolder.getAnswers().add(
                            AnswerEntity.builder()
                                    .answerId(UUID.randomUUID())
                                    .answerContent(answerContent)
                                    .correctAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                    .userCreate(userImport)
                                    .userUpdate(userImport)
                                    .question(questionChild)
                                    .build()
                    );
                }
                nextRow++;
                rowQuestionChild = sheet.getRow(nextRow);
            }

            rowParentContent = rowQuestionChild;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    @SneakyThrows
    protected ExcelContentEntityHolder fillQuestionForTopicAnyPartFromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part) {

        if (part == 1 || part == 2)
            return fillListeningPart12FromExcelToDb(topicId, userImport, workbook, part);
        else if (part == 3 || part == 4)
            return fillListeningPart34FromExcelToDb(topicId, userImport, workbook, part);
        else if (part == 5)
            return fillReadingPart5FromExcelToDb(topicId, userImport, workbook);
        else if (part == 6 || part == 7)
            return fillReadingPart67FromExcelToDb(topicId, userImport, workbook, part);
        return new ExcelContentEntityHolder();
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsRes importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            ExcelContentEntityHolder entityHolder = fillQuestionForTopicAnyPartFromExcelToDb(topicId, userImport, workbook, part);
            partJdbcRepository.batchInsertPart(entityHolder.getParts());
            questionJdbcRepository.batchInsertQuestion(entityHolder.getQuestionParents());
            questionJdbcRepository.batchInsertQuestion(entityHolder.getQuestionChilds());
            answerJdbcRepository.batchInsertAnswer(entityHolder.getAnswers());
            topicJdbcRepository.updateTopicNumberQuestion(entityHolder.getTopicNumberQuestions());
        }
        catch (Exception e){

            if(e instanceof ErrorHolder errorHolder)
                throw errorHolder;

            throw new ErrorHolder(Error.CONFLICT, e.getMessage(), false);
        }

        return ExcelTopicPartIdsRes.builder()
                .topicId(topicId)
                .partIds(partRepository.findPartIdsByTopicId(topicId))
                .build();
    }

    @Transactional
    @Override
    @SneakyThrows
    public ExcelTopicPartIdsRes importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file) {

        UserEntity userImport = userService.currentUser();

        TopicEntity topic = topicService.getTopicById(topicId);

        ExcelContentEntityHolder entityHolder = fillAllPartToEntityHolder(file, topic, userImport);
        partJdbcRepository.batchInsertPart(entityHolder.getParts());
        questionJdbcRepository.batchInsertQuestion(entityHolder.getQuestionParents());
        questionJdbcRepository.batchInsertQuestion(entityHolder.getQuestionChilds());
        answerJdbcRepository.batchInsertAnswer(entityHolder.getAnswers());
        topicJdbcRepository.updateTopicNumberQuestion(entityHolder.getTopicNumberQuestions());

        return ExcelTopicPartIdsRes.builder()
                .topicId(topicId)
                .partIds(partRepository.findPartIdsByTopicId(topicId))
                .build();
    }

    protected ExcelContentEntityHolder fillAllPartToEntityHolder(MultipartFile file, TopicEntity topic, UserEntity userImport) throws IOException {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();
            TopicTypeEntity topicType = topic.getTopicType();
            if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.READING.getType())){
                entityHolder = fillTopicPartsQuestionsAnswersReadingToDb(topic.getTopicId(), userImport, workbook, file);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.SPEAKING.getType())){
                entityHolder = fillSpeakingPartsToDb(topic.getTopicId(), userImport, workbook);
            }
            else if(topicType.getTopicTypeName().equalsIgnoreCase(TopicType.WRITING.getType())){
                entityHolder = fillWritingPartsToDb(topic.getTopicId(), userImport, workbook);
            }
            else{
                List<ExcelContentEntityHolder> entityHolderList = new ArrayList<>();
                int numberOfSheets = workbook.getNumberOfSheets();
                for(int part = 1; part < numberOfSheets; part++) {
                    entityHolderList.add(
                            fillQuestionForTopicAnyPartFromExcelToDb(topic.getTopicId(), userImport, workbook, part)
                    );
                }
                entityHolder.setParts(entityHolderList.stream().flatMap(elm -> elm.getParts().stream()).toList());
                entityHolder.setAnswers(entityHolderList.stream().flatMap(elm -> elm.getAnswers().stream()).toList());
                entityHolder.setTopicNumberQuestions(entityHolderList.stream().flatMap(elm -> elm.getTopicNumberQuestions().entrySet().stream())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Integer::sum
                        )));

                Map<PartEntity, List<QuestionEntity>> partQuestionsParentGroup = entityHolderList.stream()
                        .flatMap(elm -> elm.getQuestionParents().stream())
                        .collect(Collectors.groupingBy(QuestionEntity::getPart));
                Map<PartEntity, List<QuestionEntity>> partQuestionsChildGroup = entityHolderList.stream()
                        .flatMap(elm -> elm.getQuestionChilds().stream())
                        .collect(Collectors.groupingBy(QuestionEntity::getPart));
                List<PartEntity> parts = partQuestionsParentGroup.keySet().stream()
                        .sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder())))
                        .toList();
                int numberQuestionParentToUpdate = 0;
                int numberQuestionChildToUpdate = 0;
                int partsSize = parts.size();
                for(int i = 0; i < partsSize; i++){
                    if(i == 0) {
                        entityHolder.getQuestionParents().addAll(partQuestionsParentGroup.get(parts.get(i)));
                        entityHolder.getQuestionChilds().addAll(partQuestionsChildGroup.get(parts.get(i)));
                        continue;
                    }
                    PartEntity partPrevious = parts.get(i - 1);
                    numberQuestionParentToUpdate += partQuestionsParentGroup.getOrDefault(partPrevious, new ArrayList<>()).size();
                    numberQuestionChildToUpdate += partQuestionsChildGroup.getOrDefault(partPrevious, new ArrayList<>()).size();
                    List<QuestionEntity> questionsParentPartCurrent = partQuestionsParentGroup.getOrDefault(parts.get(i), new ArrayList<>());
                    List<QuestionEntity> questionsChildPartCurrent = partQuestionsChildGroup.getOrDefault(parts.get(i), new ArrayList<>());
                    int questionsParentPartCurrentSize = questionsParentPartCurrent.size();
                    for(int jp = 0; jp < questionsParentPartCurrentSize; jp++){
                        questionsParentPartCurrent.get(jp).setQuestionNumber(numberQuestionParentToUpdate + jp + 1);
                        entityHolder.getQuestionParents().add(questionsParentPartCurrent.get(jp));
                    }
                    int questionsChildPartCurrentSize = questionsChildPartCurrent.size();
                    for(int jc = 0; jc < questionsChildPartCurrentSize; jc++){
                        questionsChildPartCurrent.get(jc).setQuestionNumber(numberQuestionChildToUpdate + jc + 1);
                        entityHolder.getQuestionChilds().add(questionsChildPartCurrent.get(jc));
                    }
                }
            }
            return entityHolder;
        }
    }

    @SneakyThrows
    protected ExcelContentEntityHolder fillTopicPartsQuestionsAnswersReadingToDb(UUID topicId, UserEntity userImport, Workbook workbook, MultipartFile file){

        int numberOfSheet = workbook.getNumberOfSheets();

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        int countOfQuestionsTopic = 0;

        int sheetPartStep = 1;
        // Thêm part, question, answer cho topic
        while(sheetPartStep < numberOfSheet){

            Sheet sheetPart = workbook.getSheetAt(sheetPartStep);

            int iRowPart = 0;
            Row rowPart = sheetPart.getRow(iRowPart);
            if(rowPart == null) break;

            String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));
            PartEntity partTopic = PartEntity.builder()
                    .partId(UUID.randomUUID())
                    .partName(partName)
                    .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .topicId(topicId)
                    .build();
            entityHolder.getParts().add(partTopic);

            int iRowAudioQuestionParent = iRowPart + 1;

            Row rowAudio = sheetPart.getRow(iRowAudioQuestionParent);
            while (rowAudio != null){

                int iRowImageQuestionParent = iRowAudioQuestionParent + 1;
                Row rowImage = sheetPart.getRow(iRowImageQuestionParent);
                if(rowImage == null) break;

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .part(partTopic)
                        .isQuestionParent(true)
                        .questionNumber(0)
                        .questionType(QuestionType.Question_Parent)
                        .questionScore(0)
                        .contentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)))
                        .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .build();
                entityHolder.getQuestionParents().add(questionParent);

                // Bỏ qua header table
                int nextRow = iRowImageQuestionParent + 2;
                Row rowQuestionChild = sheetPart.getRow(nextRow);
                while (rowQuestionChild != null){

                    if(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(0)).equalsIgnoreCase("audio"))
                        break;

                    int jQuestionContent = 1;
                    int jStartAnswer = jQuestionContent + 1;
                    int jQuestionResult = jStartAnswer + 4;
                    int jScoreQuestionChild = jQuestionResult + 1;
                    int jImageQuestionChild = jScoreQuestionChild + 1;

                    QuestionEntity questionChild = QuestionEntity.builder()
                            .questionId(UUID.randomUUID())
                            .part(partTopic)
                            .questionGroupParent(questionParent)
                            .questionType(QuestionType.Question_Child)
                            .isQuestionParent(false)
                            .questionNumber(++countOfQuestionsTopic)
                            .numberChoice(1)
                            .questionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)))
                            .questionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)))
                            .contentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jImageQuestionChild)))
                            .questionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jScoreQuestionChild)))
                            .userCreate(userImport)
                            .userUpdate(userImport)
                            .build();
                    questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                    entityHolder.getQuestionChilds().add(questionChild);

                    for(int i = jStartAnswer; i < jQuestionResult; i++){
                        String answerChildContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                        entityHolder.getAnswers().add(
                                AnswerEntity.builder()
                                        .answerId(UUID.randomUUID())
                                        .question(questionChild)
                                        .answerContent(answerChildContent)
                                        .correctAnswer(answerChildContent.equalsIgnoreCase(questionChild.getQuestionResult()))
                                        .userCreate(userImport)
                                        .userUpdate(userImport)
                                        .build()
                        );
                    }
                    nextRow++;
                    rowQuestionChild = sheetPart.getRow(nextRow);
                }

                rowAudio = sheetPart.getRow(nextRow);
                iRowAudioQuestionParent = nextRow;
            }

            sheetPartStep++;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    protected ExcelContentEntityHolder fillSpeakingPartsToDb(UUID topicId, UserEntity userImport, Workbook workbook){

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        int numberOfSheets = workbook.getNumberOfSheets();
        int sheetPartStep = 1;
        int countOfQuestionsTopic = 0;
        while(sheetPartStep < numberOfSheets){
            Sheet sheet = workbook.getSheetAt(sheetPartStep);
            int iRowPart = 0;
            Row rowPart = sheet.getRow(iRowPart);
            if(rowPart == null) break;

            String partName = ExcelUtil.getStringCellValue(sheet.getRow(0).getCell(0));
            PartEntity partTopic = PartEntity.builder()
                    .partId(UUID.randomUUID())
                    .partName(partName)
                    .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .topicId(topicId)
                    .build();
            entityHolder.getParts().add(partTopic);

            int iRowImage = iRowPart + 1;
            Row rowImage = sheet.getRow(iRowImage);
            while (rowImage != null){
                int iRowNext = iRowImage + 1;
                Row rowNext = sheet.getRow(iRowNext);
                String nameCell0 = ExcelUtil.getStringCellValue(rowNext.getCell(0));
                if(nameCell0.equalsIgnoreCase("title")){
                    String title = ExcelUtil.getStringCellValue(rowNext.getCell(1));
                    List<String> questionContents = new ArrayList<>();
                    int iRowQuestionContent = iRowNext + 2;
                    Row rowQuestionContent = sheet.getRow(iRowQuestionContent);
                    while (rowQuestionContent != null){
                        if(ExcelUtil.getStringCellValue(rowQuestionContent.getCell(0)).equalsIgnoreCase("image")){
                            break;
                        }
                        String questionContent = ExcelUtil.getStringCellValue(rowQuestionContent.getCell(1));
                        questionContents.add(questionContent);
                        rowQuestionContent = sheet.getRow(++iRowQuestionContent);
                    }

                    if(!questionContents.isEmpty()){
                        String questionContentHtml = DomUtil.toHtmlQuestionContentSpeaking(title, questionContents);
                        QuestionEntity questionSpeaking = QuestionEntity.builder()
                                .questionId(UUID.randomUUID())
                                .questionContent(questionContentHtml)
                                .part(partTopic)
                                .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                                .questionType(QuestionType.Speaking)
                                .questionNumber(++countOfQuestionsTopic)
                                .isQuestionParent(true)
                                .questionScore(0)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build();
                        entityHolder.getQuestionParents().add(questionSpeaking);
                    }

                    iRowImage = iRowQuestionContent;
                    rowImage = rowQuestionContent;
                }
                else{
                    Row rowQuestionContent = sheet.getRow(iRowNext);
                    while (rowQuestionContent != null){
                        if(ExcelUtil.getStringCellValue(rowQuestionContent.getCell(0)).equalsIgnoreCase("image")){
                            break;
                        }
                        QuestionEntity questionSpeaking = QuestionEntity.builder()
                                .questionId(UUID.randomUUID())
                                .questionContent(ExcelUtil.getStringCellValue(rowQuestionContent.getCell(1)))
                                .part(partTopic)
                                .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                                .questionType(QuestionType.Speaking)
                                .questionNumber(++countOfQuestionsTopic)
                                .isQuestionParent(true)
                                .questionScore(0)
                                .userCreate(userImport)
                                .userUpdate(userImport)
                                .build();
                        entityHolder.getQuestionParents().add(questionSpeaking);
                        rowQuestionContent = sheet.getRow(++iRowNext);
                    }
                    iRowImage = iRowNext;
                    rowImage = rowQuestionContent;
                }
            }
            sheetPartStep++;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    protected ExcelContentEntityHolder fillWritingPartsToDb(UUID topicId, UserEntity userImport, Workbook workbook){

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        int numberOfSheets = workbook.getNumberOfSheets();
        int sheetPartStep = 1;
        int countOfQuestionsTopic = 0;
        while(sheetPartStep < numberOfSheets){
            Sheet sheet = workbook.getSheetAt(sheetPartStep);
            int iRowPart = 0;
            Row rowPart = sheet.getRow(iRowPart);
            if(rowPart == null) break;

            String partName = ExcelUtil.getStringCellValue(sheet.getRow(0).getCell(0));
            PartEntity partTopic = PartEntity.builder()
                    .partId(UUID.randomUUID())
                    .partName(partName)
                    .partType(ExcelUtil.getStringCellValue(rowPart.getCell(1)))
                    .userCreate(userImport)
                    .userUpdate(userImport)
                    .topicId(topicId)
                    .build();
            entityHolder.getParts().add(partTopic);

            int iRowImage = iRowPart + 1;
            Row rowImage = sheet.getRow(iRowImage);
            while (rowImage != null){

                int iRowQuestionSpeaking = iRowImage + 1;

                QuestionEntity questionParent = QuestionEntity.builder()
                        .questionId(UUID.randomUUID())
                        .questionContent(ExcelUtil.getStringCellValue(sheet.getRow(iRowQuestionSpeaking).getCell(1)))
                        .part(partTopic)
                        .contentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)))
                        .questionType(QuestionType.Writing)
                        .isQuestionParent(true)
                        .questionNumber(++countOfQuestionsTopic)
                        .questionScore(0)
                        .userCreate(userImport)
                        .userUpdate(userImport)
                        .build();
                entityHolder.getQuestionParents().add(questionParent);
                rowImage = sheet.getRow(iRowQuestionSpeaking + 1);
            }
            sheetPartStep++;
        }

        entityHolder.getTopicNumberQuestions().put(topicId, countOfQuestionsTopic);
        return entityHolder;
    }

    @Override
    public List<ExcelTopicPartIdsRes> importAllQuestionsFromPartForMultipleTopic(List<MultipartFile> files) throws BadRequestException {
        if(files == null || files.isEmpty())
            throw new BadRequestException("Files is required.");
        UserEntity userImport = userService.currentUser();
        List<CompletableFuture<ExcelTopicPartIdsRes>> futures = new ArrayList<>();
        for(MultipartFile file : files){
            if(file == null) continue;
            CompletableFuture<ExcelTopicPartIdsRes> future = CompletableFuture.supplyAsync(() -> {
                TransactionTemplate transactionTemplate = SpringApplicationContext.getBean(TransactionTemplate.class);
                return transactionTemplate.execute(status -> {
                    try {
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userImport, null, userImport.getAuthorities()
                        );
                        securityContext.setAuthentication(authentication);
                        SecurityContextHolder.setContext(securityContext);
                        ExcelImportService excelImportService = SpringApplicationContext.getBean(ExcelImportService.class);
                        TopicKeyRes topicKeyRes = excelImportService.importTopicFromExcel(file);
                        ExcelTopicPartIdsRes topicPartIdsRes = excelImportService.importQuestionAtAllPartForTopicFromExcel(topicKeyRes.getTopicId(), file);
                        SecurityContextHolder.clearContext();
                        return topicPartIdsRes;
                    }
                    catch (Exception e){
                        status.setRollbackOnly();
                        throw new RuntimeException(e);
                    }
                });
            }).exceptionally((e) -> {
                log.error(e.getMessage());
                return null;
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        List<ExcelTopicPartIdsRes> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        if(results.isEmpty())
            throw new ErrorHolder(Error.CONFLICT, "Cannot import files", true);
        return results;
    }
}
