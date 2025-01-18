package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ExcelContentMapper {

    ExcelContentMapper INSTANCE = Mappers.getMapper(ExcelContentMapper.class);

    @Mapping(target = "partId", source = "part.partId")
    @Mapping(target = "isQuestionParent", defaultValue = "false")
    @Mapping(target = "contents", expression = "java(ContentMapper.INSTANCE.toContentBasicResponseList(questionEntity.getContentCollection()))")
    @Mapping(target = "answers", expression = "java(AnswerMapper.INSTANCE.toAnswerResponseList(questionEntity.getAnswers()))")
    @Mapping(target = "questionsChildren", expression = "java(toExcelResponseExcludeChildList(questionEntity.getQuestionGroupChildren()))")
    ExcelQuestionResponse toExcelQuestionResponse(QuestionEntity questionEntity);

    default List<ExcelQuestionResponse> toExcelResponseExcludeChildList(List<QuestionEntity> questionEntityList){

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

    List<ExcelQuestionResponse> toExcelQuestionResponseList(List<QuestionEntity> questionEntityList);

    @Mapping(target = "questionId", ignore = true)
    void flowToQuestionEntity(ExcelQuestionResponse questionByExcelFileResponse, @MappingTarget QuestionEntity questionEntity);

}
