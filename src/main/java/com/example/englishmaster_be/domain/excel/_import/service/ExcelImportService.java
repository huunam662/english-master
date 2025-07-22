package com.example.englishmaster_be.domain.excel._import.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.mock_test.evaluator_writing.util.SpringApplicationContext;
import com.example.englishmaster_be.domain.excel._import.dto.ExcelContentEntityHolder;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelPartIdsRes;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelTopicPartIdsRes;
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackKeyView;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicKeyView;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicKeyRes;
import com.example.englishmaster_be.domain.exam.topic.topic.service.ITopicService;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.type.repository.TopicTypeJdbcRepository;
import com.example.englishmaster_be.domain.exam.topic.type.repository.TopicTypeRepository;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerJdbcRepository;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.pack.pack.repository.PackJdbcRepository;
import com.example.englishmaster_be.domain.exam.pack.pack.repository.PackRepository;
import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.exam.pack.type.repository.PackTypeJdbcRepository;
import com.example.englishmaster_be.domain.exam.pack.type.repository.PackTypeRepository;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.part.repository.PartJdbcRepository;
import com.example.englishmaster_be.domain.exam.part.repository.PartRepository;
import com.example.englishmaster_be.domain.exam.question.repository.QuestionJdbcRepository;
import com.example.englishmaster_be.domain.exam.topic.topic.repository.TopicJdbcRepository;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.repository.TopicRepository;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.util.DomUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
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
public class ExcelImportService implements IExcelImportService {

    private final PartRepository partRepository;
    private final TopicRepository topicRepository;
    private final PackRepository packRepository;
    private final ITopicService topicService;
    private final IUserService userService;
    private final PackTypeRepository packTypeRepository;
    private final PackTypeJdbcRepository packTypeJdbcRepository;
    private final PackJdbcRepository packJdbcRepository;
    private final TopicJdbcRepository topicJdbcRepository;
    private final PartJdbcRepository partJdbcRepository;
    private final QuestionJdbcRepository questionJdbcRepository;
    private final AnswerJdbcRepository answerJdbcRepository;
    private final TopicTypeRepository topicTypeRepository;
    private final TopicTypeJdbcRepository topicTypeJdbcRepository;

    @Lazy
    public ExcelImportService(PartRepository partRepository, TopicRepository topicRepository, PackRepository packRepository, ITopicService topicService, IUserService userService, PackTypeRepository packTypeRepository, PackTypeJdbcRepository packTypeJdbcRepository, PackJdbcRepository packJdbcRepository, TopicJdbcRepository topicJdbcRepository, PartJdbcRepository partJdbcRepository, QuestionJdbcRepository questionJdbcRepository, AnswerJdbcRepository answerJdbcRepository, TopicTypeRepository topicTypeRepository, TopicTypeJdbcRepository topicTypeJdbcRepository) {
        this.partRepository = partRepository;
        this.topicRepository = topicRepository;
        this.packRepository = packRepository;
        this.topicService = topicService;
        this.userService = userService;
        this.packTypeRepository = packTypeRepository;
        this.packTypeJdbcRepository = packTypeJdbcRepository;
        this.packJdbcRepository = packJdbcRepository;
        this.topicJdbcRepository = topicJdbcRepository;
        this.partJdbcRepository = partJdbcRepository;
        this.questionJdbcRepository = questionJdbcRepository;
        this.answerJdbcRepository = answerJdbcRepository;
        this.topicTypeRepository = topicTypeRepository;
        this.topicTypeJdbcRepository = topicTypeJdbcRepository;
    }

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
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet topic information does not existed.");

            String packTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(0).getCell(1));
            String packTypeDescription = ExcelUtil.getStringCellValue(sheetTopic.getRow(1).getCell(1));

            UUID packTypeId = packTypeRepository.findPackTypeIdByName(packTypeName);

            // Nếu pack type không tồn tại thì thêm mới
            if(packTypeId == null){
                PackTypeEntity packType = new PackTypeEntity();
                packType.setName(packTypeName);
                packType.setDescription(packTypeDescription);
                packType.setCreatedBy(userImport);
                packType.setUpdatedBy(userImport);
                packTypeId = packTypeJdbcRepository.insertPackType(packType);
            }

            String packExamName = ExcelUtil.getStringCellValue(sheetTopic.getRow(2).getCell(1));

            IPackKeyView packKey = packRepository.findPackIdByName(packExamName);

            UUID packId = packKey != null ? packKey.getPackId() : null;

            // Nếu pack không thuộc pack type thì thêm mới
            if(packId == null || !packTypeId.equals(packKey.getPackTypeId())){
                PackEntity pack = new PackEntity();
                pack.setPackName(packExamName);
                pack.setUserCreate(userImport);
                pack.setUserUpdate(userImport);
                pack.setPackTypeId(packTypeId);
                packId = packJdbcRepository.insertPack(pack);
            }

            String topicTypeName = ExcelUtil.getStringCellValue(sheetTopic.getRow(3).getCell(1));
            UUID topicTypeId = topicTypeRepository.findIdByTypeName(topicTypeName);
            if(topicTypeId == null){
                TopicTypeEntity topicType = new TopicTypeEntity();
                topicType.setTopicTypeName(topicTypeName);
                topicType.setUserCreate(userImport);
                topicType.setUserUpdate(userImport);
                topicTypeId = topicTypeJdbcRepository.insertTopicType(topicType);
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

            ITopicKeyView topicKey = topicRepository.findTopicIdByName(topicName);

            UUID topicId = topicKey != null ? topicKey.getTopicId() : null;

            // Nếu topic không thuộc pack thì thêm mới
            if(topicId == null || !packId.equals(topicKey.getPackId())){
                topicId = UUID.randomUUID();
                TopicEntity topic = new TopicEntity();
                topic.setTopicId(topicId);
                topic.setTopicName(topicName);
                topic.setTopicImage(topicImage);
                topic.setTopicAudio(topicAudio);
                topic.setTopicDescription(topicDescription);
                topic.setNumberQuestion(0);
                topic.setEnable(false);
                topic.setWorkTime(workTime);
                topic.setPackId(packId);
                topic.setTopicTypeId(topicTypeId);
                topic.setUserCreate(userImport);
                topic.setUserUpdate(userImport);
                topicJdbcRepository.insertTopic(topic);
            }

            return new TopicKeyRes(topicId);
        }
        catch (Exception e){

            if(e instanceof ApplicationException errorHolder)
                throw errorHolder;

            throw new RuntimeException("Cannot import excel.");
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
                        PartEntity part = new PartEntity();
                        part.setPartId(UUID.randomUUID());
                        part.setTopicId(topicId);
                        part.setTopic(topic);
                        part.setPartType(partType);
                        part.setPartDescription(String.format("%s: %s", partName, partType));
                        part.setUserCreate(currentUser);
                        part.setUserUpdate(currentUser);
                        return part;
                    })
                    .toList();

            partJdbcRepository.batchInsertPart(partsTopic);

            return new ExcelPartIdsRes(partRepository.findPartIdsByTopicId(topicId));
        }
    }

    protected ExcelContentEntityHolder fillListeningPart12FromExcelToDb(UUID topicId, UserEntity userImport, Workbook workbook, int part){

        Sheet sheet = workbook.getSheetAt(part);

        if(sheet == null)
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();
        PartEntity partTopic = new PartEntity();
        partTopic.setPartId(UUID.randomUUID());
        partTopic.setPartName(partName);
        partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
        partTopic.setUserCreate(userImport);
        partTopic.setUserUpdate(userImport);
        partTopic.setTopicId(topicId);

        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            QuestionEntity questionParent = new QuestionEntity();
            questionParent.setQuestionId(UUID.randomUUID());
            questionParent.setPart(partTopic);
            questionParent.setUserCreate(userImport);
            questionParent.setUserUpdate(userImport);
            questionParent.setIsQuestionParent(true);
            questionParent.setQuestionScore(0);
            questionParent.setQuestionNumber(++questionParentNumber);
            questionParent.setQuestionType(QuestionType.Question_Parent);
            questionParent.setContentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)));

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

                QuestionEntity questionChild = new QuestionEntity();
                questionChild.setQuestionId(UUID.randomUUID());
                questionChild.setPart(partTopic);
                questionChild.setUserCreate(userImport);
                questionChild.setUserUpdate(userImport);
                questionChild.setIsQuestionParent(false);
                questionChild.setQuestionNumber(++countOfQuestionsTopic);
                questionChild.setQuestionType(QuestionType.Question_Child);
                questionChild.setNumberChoice(1);
                questionChild.setQuestionGroupParent(questionParent);
                questionChild.setQuestionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)));
                questionChild.setQuestionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)));
                questionChild.setContentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentImage)));
                questionChild.setContentAudio(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jContentAudio)));

                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(String answerContent : List.of("A", "B", "C", "D")){
                    AnswerEntity answer = new AnswerEntity();
                    answer.setAnswerId(UUID.randomUUID());
                    answer.setAnswerContent(answerContent);
                    answer.setCorrectAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()));
                    answer.setUserCreate(userImport);
                    answer.setUserUpdate(userImport);
                    answer.setQuestion(questionChild);
                    entityHolder.getAnswers().add(answer);
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
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = new PartEntity();
        partTopic.setPartId(UUID.randomUUID());
        partTopic.setPartName(partName);
        partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
        partTopic.setUserCreate(userImport);
        partTopic.setUserUpdate(userImport);
        partTopic.setTopicId(topicId);

        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowAudio = iRowPart + 1;
        Row rowAudio = sheet.getRow(iRowAudio);
        while (rowAudio != null){

            int iRowImage = iRowAudio + 1;
            Row rowImage = sheet.getRow(iRowImage);
            if(rowImage == null) break;

            QuestionEntity questionParent = new QuestionEntity();
            questionParent.setQuestionId(UUID.randomUUID());
            questionParent.setPart(partTopic);
            questionParent.setUserCreate(userImport);
            questionParent.setUserUpdate(userImport);
            questionParent.setIsQuestionParent(true);
            questionParent.setQuestionScore(0);
            questionParent.setQuestionNumber(++questionParentNumber);
            questionParent.setQuestionType(QuestionType.Question_Parent);
            questionParent.setContentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)));
            questionParent.setContentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)));

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

                QuestionEntity questionChild = new QuestionEntity();
                questionChild.setQuestionId(UUID.randomUUID());
                questionChild.setPart(partTopic);
                questionChild.setUserCreate(userImport);
                questionChild.setUserUpdate(userImport);
                questionChild.setIsQuestionParent(false);
                questionChild.setQuestionNumber(++countOfQuestionsTopic);
                questionChild.setQuestionType(QuestionType.Question_Child);
                questionChild.setNumberChoice(1);
                questionChild.setQuestionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)));
                questionChild.setQuestionGroupParent(questionParent);
                questionChild.setQuestionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)));
                questionChild.setQuestionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)));

                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    AnswerEntity answer = new AnswerEntity();
                    answer.setAnswerId(UUID.randomUUID());
                    answer.setAnswerContent(answerContent);
                    answer.setCorrectAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()));
                    answer.setUserCreate(userImport);
                    answer.setUserUpdate(userImport);
                    answer.setQuestion(questionChild);
                    entityHolder.getAnswers().add(answer);
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
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet 5 not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part 5"))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet 5 must be name is Part 5.");

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = new PartEntity();
        partTopic.setPartId(UUID.randomUUID());
        partTopic.setPartName(partName);
        partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
        partTopic.setUserCreate(userImport);
        partTopic.setUserUpdate(userImport);
        partTopic.setTopicId(topicId);

        entityHolder.getParts().add(partTopic);

        QuestionEntity questionParent = new QuestionEntity();
        questionParent.setQuestionId(UUID.randomUUID());
        questionParent.setPart(partTopic);
        questionParent.setUserCreate(userImport);
        questionParent.setUserUpdate(userImport);
        questionParent.setIsQuestionParent(true);
        questionParent.setQuestionScore(0);
        questionParent.setQuestionNumber(1);
        questionParent.setQuestionType(QuestionType.Question_Parent);

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

            QuestionEntity questionChild = new QuestionEntity();
            questionChild.setQuestionId(UUID.randomUUID());
            questionChild.setPart(partTopic);
            questionChild.setUserCreate(userImport);
            questionChild.setUserUpdate(userImport);
            questionChild.setIsQuestionParent(false);
            questionChild.setQuestionNumber(++countOfQuestionsTopic);
            questionChild.setQuestionType(QuestionType.Question_Child);
            questionChild.setNumberChoice(1);
            questionChild.setQuestionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)));
            questionChild.setQuestionGroupParent(questionParent);
            questionChild.setQuestionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)));
            questionChild.setQuestionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)));

            entityHolder.getQuestionChilds().add(questionChild);
            questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

            for(int i = jStartAnswer; i < jQuestionResult; i++){
                String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                AnswerEntity answer = new AnswerEntity();
                answer.setAnswerId(UUID.randomUUID());
                answer.setAnswerContent(answerContent);
                answer.setCorrectAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()));
                answer.setUserCreate(userImport);
                answer.setUserUpdate(userImport);
                answer.setQuestion(questionChild);
                entityHolder.getAnswers().add(answer);
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
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " not existed.");

        int iRowPart = 0;
        Row rowPart = sheet.getRow(iRowPart);
        String partName = ExcelUtil.getStringCellValue(rowPart.getCell(0));

        if(!partName.equalsIgnoreCase("part " + part))
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Sheet " + part + " must be name is Part " + part);

        ExcelContentEntityHolder entityHolder = new ExcelContentEntityHolder();

        PartEntity partTopic = new PartEntity();
        partTopic.setPartId(UUID.randomUUID());
        partTopic.setPartName(partName);
        partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
        partTopic.setUserCreate(userImport);
        partTopic.setUserUpdate(userImport);
        partTopic.setTopicId(topicId);

        entityHolder.getParts().add(partTopic);

        int questionParentNumber = 0;
        int countOfQuestionsTopic = 0;
        int iRowParentContent = iRowPart + 1;
        Row rowParentContent = sheet.getRow(iRowParentContent);
        while (rowParentContent != null){

            QuestionEntity questionParent = new QuestionEntity();
            questionParent.setQuestionId(UUID.randomUUID());
            questionParent.setPart(partTopic);
            questionParent.setUserCreate(userImport);
            questionParent.setUserUpdate(userImport);
            questionParent.setIsQuestionParent(true);
            questionParent.setQuestionContent(ExcelUtil.getStringCellValue(rowParentContent.getCell(1)));
            questionParent.setQuestionScore(0);
            questionParent.setQuestionNumber(++questionParentNumber);
            questionParent.setQuestionType(QuestionType.Question_Parent);

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

                QuestionEntity questionChild = new QuestionEntity();
                questionChild.setQuestionId(UUID.randomUUID());
                questionChild.setPart(partTopic);
                questionChild.setUserCreate(userImport);
                questionChild.setUserUpdate(userImport);
                questionChild.setIsQuestionParent(false);
                questionChild.setQuestionNumber(++countOfQuestionsTopic);
                questionChild.setQuestionType(QuestionType.Question_Child);
                questionChild.setNumberChoice(1);
                questionChild.setQuestionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)));
                questionChild.setQuestionGroupParent(questionParent);
                questionChild.setQuestionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)));
                questionChild.setQuestionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jQuestionScore)));

                entityHolder.getQuestionChilds().add(questionChild);
                questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());

                for(int i = jStartAnswer; i < jQuestionResult; i++){
                    String answerContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                    AnswerEntity answer = new AnswerEntity();
                    answer.setAnswerId(UUID.randomUUID());
                    answer.setAnswerContent(answerContent);
                    answer.setCorrectAnswer(answerContent.equalsIgnoreCase(questionChild.getQuestionResult()));
                    answer.setUserCreate(userImport);
                    answer.setUserUpdate(userImport);
                    answer.setQuestion(questionChild);
                    entityHolder.getAnswers().add(answer);
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

            if(e instanceof ApplicationException errorHolder)
                throw errorHolder;

            throw new RuntimeException("Cannot import excel.");
        }

        ExcelTopicPartIdsRes res = new ExcelTopicPartIdsRes();
        res.setTopicId(topicId);
        res.setPartIds(partRepository.findPartIdsByTopicId(topicId));
        return res;
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

        ExcelTopicPartIdsRes res = new ExcelTopicPartIdsRes();
        res.setTopicId(topicId);
        res.setPartIds(partRepository.findPartIdsByTopicId(topicId));
        return res;
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

            PartEntity partTopic = new PartEntity();
            partTopic.setPartId(UUID.randomUUID());
            partTopic.setPartName(partName);
            partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
            partTopic.setUserCreate(userImport);
            partTopic.setUserUpdate(userImport);
            partTopic.setTopicId(topicId);

            entityHolder.getParts().add(partTopic);

            int iRowAudioQuestionParent = iRowPart + 1;

            Row rowAudio = sheetPart.getRow(iRowAudioQuestionParent);
            while (rowAudio != null){

                int iRowImageQuestionParent = iRowAudioQuestionParent + 1;
                Row rowImage = sheetPart.getRow(iRowImageQuestionParent);
                if(rowImage == null) break;

                QuestionEntity questionParent = new QuestionEntity();
                questionParent.setQuestionId(UUID.randomUUID());
                questionParent.setPart(partTopic);
                questionParent.setIsQuestionParent(true);
                questionParent.setQuestionNumber(0);
                questionParent.setQuestionType(QuestionType.Question_Parent);
                questionParent.setQuestionScore(0);
                questionParent.setContentAudio(ExcelUtil.getStringCellValue(rowAudio.getCell(1)));
                questionParent.setContentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)));
                questionParent.setUserCreate(userImport);
                questionParent.setUserUpdate(userImport);

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

                    QuestionEntity questionChild = new QuestionEntity();
                    questionChild.setQuestionId(UUID.randomUUID());
                    questionChild.setPart(partTopic);
                    questionChild.setQuestionGroupParent(questionParent);
                    questionChild.setQuestionType(QuestionType.Question_Child);
                    questionChild.setIsQuestionParent(false);
                    questionChild.setQuestionNumber(++countOfQuestionsTopic);
                    questionChild.setNumberChoice(1);
                    questionChild.setQuestionContent(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionContent)));
                    questionChild.setQuestionResult(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jQuestionResult)));
                    questionChild.setContentImage(ExcelUtil.getStringCellValue(rowQuestionChild.getCell(jImageQuestionChild)));
                    questionChild.setQuestionScore((int) ExcelUtil.getNumericCellValue(rowQuestionChild.getCell(jScoreQuestionChild)));
                    questionChild.setUserCreate(userImport);
                    questionChild.setUserUpdate(userImport);

                    questionParent.setQuestionScore(questionParent.getQuestionScore() + questionChild.getQuestionScore());
                    entityHolder.getQuestionChilds().add(questionChild);

                    for(int i = jStartAnswer; i < jQuestionResult; i++){
                        String answerChildContent = ExcelUtil.getStringCellValue(rowQuestionChild.getCell(i));
                        AnswerEntity answer = new AnswerEntity();
                        answer.setAnswerId(UUID.randomUUID());
                        answer.setAnswerContent(answerChildContent);
                        answer.setCorrectAnswer(answerChildContent.equalsIgnoreCase(questionChild.getQuestionResult()));
                        answer.setUserCreate(userImport);
                        answer.setUserUpdate(userImport);
                        answer.setQuestion(questionChild);
                        entityHolder.getAnswers().add(answer);
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

            PartEntity partTopic = new PartEntity();
            partTopic.setPartId(UUID.randomUUID());
            partTopic.setPartName(partName);
            partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
            partTopic.setUserCreate(userImport);
            partTopic.setUserUpdate(userImport);
            partTopic.setTopicId(topicId);

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

                        QuestionEntity questionSpeaking = new QuestionEntity();
                        questionSpeaking.setQuestionId(UUID.randomUUID());
                        questionSpeaking.setQuestionContent(questionContentHtml);
                        questionSpeaking.setPart(partTopic);
                        questionSpeaking.setContentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)));
                        questionSpeaking.setQuestionType(QuestionType.Speaking);
                        questionSpeaking.setQuestionNumber(++countOfQuestionsTopic);
                        questionSpeaking.setIsQuestionParent(true);
                        questionSpeaking.setQuestionScore(0);
                        questionSpeaking.setUserCreate(userImport);
                        questionSpeaking.setUserUpdate(userImport);

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
                        QuestionEntity questionSpeaking = new QuestionEntity();
                        questionSpeaking.setQuestionId(UUID.randomUUID());
                        questionSpeaking.setQuestionContent(ExcelUtil.getStringCellValue(rowQuestionContent.getCell(1)));
                        questionSpeaking.setPart(partTopic);
                        questionSpeaking.setContentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)));
                        questionSpeaking.setQuestionType(QuestionType.Speaking);
                        questionSpeaking.setIsQuestionParent(true);
                        questionSpeaking.setQuestionNumber(++countOfQuestionsTopic);
                        questionSpeaking.setQuestionScore(0);
                        questionSpeaking.setUserCreate(userImport);
                        questionSpeaking.setUserUpdate(userImport);

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

            PartEntity partTopic = new PartEntity();
            partTopic.setPartId(UUID.randomUUID());
            partTopic.setPartName(partName);
            partTopic.setPartType(ExcelUtil.getStringCellValue(rowPart.getCell(1)));
            partTopic.setUserCreate(userImport);
            partTopic.setUserUpdate(userImport);
            partTopic.setTopicId(topicId);

            entityHolder.getParts().add(partTopic);

            int iRowImage = iRowPart + 1;
            Row rowImage = sheet.getRow(iRowImage);
            while (rowImage != null){

                int iRowQuestionSpeaking = iRowImage + 1;

                QuestionEntity questionParent = new QuestionEntity();
                questionParent.setQuestionId(UUID.randomUUID());
                questionParent.setQuestionContent(ExcelUtil.getStringCellValue(sheet.getRow(iRowQuestionSpeaking).getCell(1)));
                questionParent.setPart(partTopic);
                questionParent.setContentImage(ExcelUtil.getStringCellValue(rowImage.getCell(1)));
                questionParent.setQuestionType(QuestionType.Writing);
                questionParent.setIsQuestionParent(true);
                questionParent.setQuestionNumber(++countOfQuestionsTopic);
                questionParent.setQuestionScore(0);
                questionParent.setUserCreate(userImport);
                questionParent.setUserUpdate(userImport);

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
            throw new ApplicationException(HttpStatus.CONFLICT, "Cannot import files");
        return results;
    }
}
