package com.example.englishmaster_be.domain.pack_type.repository.factory;

import com.example.englishmaster_be.domain.pack_type.dto.request.PackTypeFilterRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.domain.pack_type.model.PackTypeEntity;
import com.example.englishmaster_be.domain.pack_type.mapper.PackTypeMapper;
import com.example.englishmaster_be.model.pack_type.QPackTypeEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.pack_type.util.PackTypeUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackTypeQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<PackTypeEntity> selectFromPackTypeEntity() {

        return jpaQueryFactory.selectFrom(QPackTypeEntity.packTypeEntity);
    }

    public FilterResponse<PackTypeResponse> filterToResponse(PackTypeFilterRequest request){

        BooleanExpression whereExpression = QPackTypeEntity.packTypeEntity.isNotNull().and(
                QPackTypeEntity.packTypeEntity.name.isNotNull().and(
                        QPackTypeEntity.packTypeEntity.name
                                .likeIgnoreCase("%" + request.getSearch().toLowerCase() + "%")
                )
        );

        long totalElements = Optional.ofNullable(
                jpaQueryFactory.select(QPackTypeEntity.packTypeEntity.count())
                        .from(QPackTypeEntity.packTypeEntity)
                        .where(whereExpression)
                        .fetchOne()
        ).orElse(0L);

        long totalPages = (long) Math.ceil((float) totalElements / request.getPageSize());

        long offset = (long) request.getPageSize() * (request.getPage() - 1);

        JPAQuery<PackTypeEntity> query = selectFromPackTypeEntity()
                .where(whereExpression)
                .orderBy(PackTypeUtil.buildPackTypeOrderSpecifier(request.getSortBy(), request.getDirection()))
                .offset(offset)
                .limit(request.getPageSize());

        List<PackTypeEntity> packTypeList = query.fetch();

        return FilterResponse.<PackTypeResponse>builder()
                .pageNumber(request.getPage())
                .pageSize(request.getPageSize())
                .offset(offset)
                .totalPages(totalPages)
                .contentLength(packTypeList.size())
                .content(PackTypeMapper.INSTANCE.toPackTypeResponseList(packTypeList))
                .build();

    }
}
