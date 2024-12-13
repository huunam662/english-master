package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Mapper.TypeMapper;
import com.example.englishmaster_be.Model.Request.Type.TypeRequest;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.entity.QTypeEntity;
import com.example.englishmaster_be.entity.TypeEntity;
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
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TypeServiceImpl implements ITypeService {

    TypeRepository typeRepository;

    JPAQueryFactory queryFactory;

    @Override
    public List<TypeEntity> getAllTypes() {

        QTypeEntity type = QTypeEntity.typeEntity;

        JPAQuery<TypeEntity> query = queryFactory.selectFrom(type);

        return query.fetch();
    }

    public TypeEntity getTypeById(UUID id) {

        QTypeEntity type = QTypeEntity.typeEntity;

        TypeEntity result = queryFactory.selectFrom(type)
                .where(type.typeId.eq(id))
                .fetchOne();

        return Optional.ofNullable(result).orElseThrow(
                () -> new BadRequestException("Type not found")
        );
    }

    @Override
    public TypeEntity saveType(TypeRequest typeRequest) {

        TypeEntity typeEntity;

        if(typeRequest.getTypeId() != null) {
            typeEntity = getTypeById(typeRequest.getTypeId());

            TypeMapper.INSTANCE.flowToTypeEntity(typeRequest, typeEntity);
        }
        else typeEntity = TypeMapper.INSTANCE.toTypeEntity(typeRequest);

        return typeRepository.save(typeEntity);

    }

    @Override
    public void deleteTypeById(UUID id) {

        TypeEntity type = typeRepository.findById(id).orElseThrow(
                () -> new BadRequestException("TypeEntity not found")
        );

        typeRepository.delete(type);
    }
}
