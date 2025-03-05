package com.example.englishmaster_be.mapper;


import com.example.englishmaster_be.domain.part.dto.request.PartRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true))
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    PartEntity toPartEntity(PartRequest partDto);

    @Mapping(target = "totalQuestion", expression = "java(calculateTotalQuestion(part))")
    PartResponse toPartResponse(PartEntity part);

    default int calculateTotalQuestion(PartEntity part) {
        int totalQuestion = 0;
        if (part != null && part.getQuestions() != null) {
            for (QuestionEntity question : part.getQuestions()) {
                totalQuestion += question.getNumberOfQuestionsChild();
            }
        }
        return totalQuestion;
    }

    List<PartResponse> toPartResponseList(List<PartEntity> partList);

    PartBasicResponse toPartBasicResponse(PartEntity partEntity);

    List<PartBasicResponse> toPartBasicResponseList(List<PartEntity> partList);

    default List<String> toPartNameResponseList(List<PartEntity> partEntities) {

        partEntities = new ArrayList<>(
                partEntities.stream().collect(
                        Collectors.toMap(
                                PartEntity::getPartName,
                                part -> part,
                                (part1, part2) -> part1
                        )
                ).values());

        return partEntities.stream().map(PartEntity::getPartName).sorted(String::compareTo).toList();
    }

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartRequest partRequest, @MappingTarget PartEntity partEntity);
    
}
