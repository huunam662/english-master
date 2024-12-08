package com.example.englishmaster_be.Mapper;


import com.example.englishmaster_be.DTO.Part.SavePartDTO;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Response.PartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PartMapper {

    PartMapper INSTANCE = Mappers.getMapper(PartMapper.class);

    Part savePartDtoToPartEntity(SavePartDTO partDto);

    PartResponse partEntityToPartResponse(Part part);

    List<PartResponse> partEntityListToPartResponseList(List<Part> partList);

}
