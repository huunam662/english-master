package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Type.SaveTypeDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.QType;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.Model.Type;
import com.example.englishmaster_be.Repository.TypeRepository;
import com.example.englishmaster_be.Service.ITypeService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TypeServiceImpl implements ITypeService {

    TypeRepository typeRepository;

    JPAQueryFactory queryFactory;

    @Override
    public List<TypeResponse> getAllTypes() {

        QType type = QType.type;

        JPAQuery<Type> query = queryFactory.selectFrom(type);

        List<Type> typeList = query.fetch();

        return typeList.stream()
                .map(TypeResponse::new)
                .collect(Collectors.toList());
    }

    public TypeResponse getTypeById(UUID id) {

        QType type = QType.type;

        Type result = queryFactory.selectFrom(type)
                .where(type.typeId.eq(id))
                .fetchOne();

        if (result == null) throw new BadRequestException("Type not found");

        return new TypeResponse(result);
    }

    @Override
    public TypeResponse createType(SaveTypeDTO createTypeDTO) {

        Type type = Type.builder()
                .typeName(createTypeDTO.getTypeName())
                .nameSlug(createTypeDTO.getNameSlug())
                .build();

        typeRepository.save(type);

        return TypeResponse.builder()
                .typeId(type.getTypeId())
                .typeName(createTypeDTO.getTypeName())
                .nameSlug(createTypeDTO.getNameSlug())
                .build();
    }

    @Override
    public void deleteTypeById(UUID id) {

        Type type = typeRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Type not found")
        );

        typeRepository.delete(type);
    }
}
