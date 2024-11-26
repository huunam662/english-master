package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.dto.type.CreateTypeDTO;
import com.example.englishmaster_be.model.QType;
import com.example.englishmaster_be.model.response.TypeResponse;
import com.example.englishmaster_be.model.Type;
import com.example.englishmaster_be.repository.TypeRepository;
import com.example.englishmaster_be.service.ITypeService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

        if (result == null) {
            return null;
        }

        return new TypeResponse(result);
    }

    @Override
    public TypeResponse createType(CreateTypeDTO createTypeDTO) {
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
        typeRepository.deleteById(id);
    }
}
