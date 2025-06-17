package com.example.englishmaster_be.domain.part.mapper;


import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.*;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.mapper.QuestionMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(imports = {QuestionMapper.class}, builder = @Builder(disableBuilder = true))
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    PartEntity toPartEntity(PartRequest partDto);

    PartResponse toPartResponse(PartEntity part);

    List<PartResponse> toPartResponseList(Collection<PartEntity> partList);

    PartBasicResponse toPartBasicResponse(PartEntity partEntity);

    List<PartBasicResponse> toPartBasicResponseList(Collection<PartEntity> parts);

    PartAndTotalQuesionResponse toPartAndTotalQuestionResponse(PartEntity partEntity);

    List<PartAndTotalQuesionResponse> toPartAndTotalQuestionResponseList(Collection<PartEntity> partList);

    default List<String> toPartNameResponseList(Collection<PartEntity> partEntities) {

        return partEntities.stream().map(
                PartEntity::getPartName
        ).distinct().sorted().toList();
    }

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartRequest partRequest, @MappingTarget PartEntity partEntity);

    @Mapping(target = "questions", expression = "java(QuestionMapper.INSTANCE.toQuestionResponseList(part.getQuestions()))")
    PartQuestionResponse toPartQuestionResponse(PartEntity part);


    PartTopicResponse toPartTopicResponse(PartEntity part);

    List<PartTopicResponse> toPartTopicResponseList(Collection<PartEntity> partList);
}
