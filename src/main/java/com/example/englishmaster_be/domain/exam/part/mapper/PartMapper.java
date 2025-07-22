package com.example.englishmaster_be.domain.exam.part.mapper;


import com.example.englishmaster_be.domain.exam.part.dto.res.*;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.part.dto.req.PartReq;
import com.example.englishmaster_be.domain.exam.question.mapper.QuestionMapper;
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

    PartEntity toPartEntity(PartReq partDto);

    PartRes toPartResponse(PartEntity part);

    List<PartRes> toPartResponseList(Collection<PartEntity> partList);

    PartBasicRes toPartBasicResponse(PartEntity partEntity);

    List<PartBasicRes> toPartBasicResponseList(Collection<PartEntity> parts);

    PartAndTotalQuestionRes toPartAndTotalQuestionResponse(PartEntity partEntity);

    List<PartAndTotalQuestionRes> toPartAndTotalQuestionResponseList(Collection<PartEntity> partList);

    default List<String> toPartNameResponseList(Collection<PartEntity> partEntities) {
        return partEntities.stream().map(
                PartEntity::getPartName
        ).distinct().sorted().toList();
    }

    @Mapping(target = "partId", ignore = true)
    void flowToPartEntity(PartReq partRequest, @MappingTarget PartEntity partEntity);

    @Mapping(target = "questions", expression = "java(QuestionMapper.INSTANCE.toQuestionResponseList(part.getQuestions()))")
    PartQuestionRes toPartQuestionResponse(PartEntity part);


    PartTopicRes toPartTopicResponse(PartEntity part);

    List<PartTopicRes> toPartTopicResponseList(Collection<PartEntity> partList);
}
