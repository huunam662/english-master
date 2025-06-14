package com.example.englishmaster_be.domain.excel.mapper;

import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.excel.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.excel.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.pack.mapper.PackMapper;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        imports = {AnswerMapper.class, PackMapper.class, PartMapper.class},
        builder = @Builder(disableBuilder = true)
)
public interface ExcelContentMapper {

    ExcelContentMapper INSTANCE = Mappers.getMapper(ExcelContentMapper.class);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "isQuestionParent", defaultValue = "false")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", expression = "java(toExcelResponseExcludeChildList(questionEntity.getQuestionGroupChildren()))")
    ExcelQuestionResponse toExcelQuestionResponse(QuestionEntity questionEntity);

    default List<ExcelQuestionResponse> toExcelResponseExcludeChildList(Collection<QuestionEntity> questionEntityList){

        if(questionEntityList == null) return null;

        return questionEntityList.stream().map(
                question -> {
                    if(question.getQuestionGroupChildren() != null){
                        question.getQuestionGroupChildren().forEach(
                                questionChild -> questionChild.setQuestionGroupChildren(null)
                        );
                    }

                    return toExcelQuestionResponse(question);
                }
        ).toList();
    }

    List<ExcelQuestionResponse> toExcelQuestionResponseList(Collection<QuestionEntity> questionEntityList);

    default ExcelQuestionListResponse toExcelQuestionListResponse(Collection<ExcelQuestionResponse> excelQuestionResponseList){

        if(excelQuestionResponseList == null) return null;

        return ExcelQuestionListResponse.builder()
                .questions(excelQuestionResponseList.stream().toList())
                .build();
    }

    @Mapping(target = "pack", expression = "java(PackMapper.INSTANCE.toPackResponse(topicEntity.getPack()))")
    @Mapping(target = "parts", expression = "java(PartMapper.INSTANCE.toPartResponseList(topicEntity.getParts()))")
    @Mapping(target = "topicType", source = "topicType.topicTypeName")
    ExcelTopicResponse toExcelTopicResponse(TopicEntity topicEntity);

}
