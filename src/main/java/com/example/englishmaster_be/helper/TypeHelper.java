package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.common.constant.sort.SortByTypeFieldsEnum;
import com.example.englishmaster_be.model.type.QTypeEntity;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

public class TypeHelper {

    public static OrderSpecifier<?> buildTypeOrderSpecifier(SortByTypeFieldsEnum sortBy, Sort.Direction sortDirection) {

        boolean isAscending = sortDirection != null && sortDirection.isAscending();

        return switch (sortBy) {
            case TypeName -> isAscending ? QTypeEntity.typeEntity.typeName.asc() : QTypeEntity.typeEntity.typeName.desc();
            case NameSlug -> isAscending ? QTypeEntity.typeEntity.nameSlug.asc() : QTypeEntity.typeEntity.nameSlug.desc();
            default -> isAscending ? QTypeEntity.typeEntity.typeId.asc() : QTypeEntity.typeEntity.typeId.desc();
        };
    }

}
