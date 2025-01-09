package com.example.englishmaster_be.mapper;

import com.example.englishmaster_be.domain.user_answer.dto.response.UserAnswerBlankResponse;
import com.example.englishmaster_be.model.user_blank_answer.UserBlankAnswerEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface UserBlankAnswerMapper {

    UserBlankAnswerMapper INSTANCE = Mappers.getMapper(UserBlankAnswerMapper.class);


}
