package com.example.englishmaster_be.domain.type.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.mapper.TypeMapper;
import com.example.englishmaster_be.domain.type.dto.request.TypeFilterRequest;
import com.example.englishmaster_be.domain.type.dto.request.TypeRequest;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.domain.type.dto.response.TypeResponse;
import com.example.englishmaster_be.model.type.QTypeEntity;
import com.example.englishmaster_be.util.TypeUtil;
import com.example.englishmaster_be.model.type.TypeEntity;
import com.example.englishmaster_be.model.type.TypeRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TypeService implements ITypeService {

    TypeRepository typeRepository;

    JPAQueryFactory queryFactory;

    @Override
    public FilterResponse<?> getTypeList(TypeFilterRequest filterRequest) {

        FilterResponse<TypeResponse> filterResponse = FilterResponse.<TypeResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getPageSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
                .build();

        BooleanExpression wherePattern = QTypeEntity.typeEntity.isNotNull();

        if(filterRequest.getSearch() != null) {

            String likeExpression = "%" + filterRequest.getSearch().trim().replaceAll("\\s+", "%") + "%";

            wherePattern = wherePattern.and(QTypeEntity.typeEntity.typeName.like(likeExpression).or(QTypeEntity.typeEntity.nameSlug.like(likeExpression)));

        }

        long totalElements = Optional.ofNullable(
                queryFactory.select(QTypeEntity.typeEntity.count())
                            .from(QTypeEntity.typeEntity)
                            .where(wherePattern)
                            .fetchOne()
                ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier = TypeUtil.buildTypeOrderSpecifier(filterRequest.getSortBy(), filterRequest.getDirection());

        JPAQuery<TypeEntity> query = queryFactory
                .selectFrom(QTypeEntity.typeEntity)
                .where(wherePattern)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                TypeMapper.INSTANCE.toTypeResponseList(query.fetch())
        );

        return filterResponse;
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
