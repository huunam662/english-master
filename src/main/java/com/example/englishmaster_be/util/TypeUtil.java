package com.example.englishmaster_be.Util;

import com.example.englishmaster_be.common.constaint.sort.SortByTypeFieldsEnum;
import com.example.englishmaster_be.entity.QTypeEntity;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

public class TypeUtil {

    public static OrderSpecifier<?> buildTypeOrderSpecifier(SortByTypeFieldsEnum sortBy, Sort.Direction sortDirection) {

        return switch (sortBy) {
            case TypeName -> sortDirection.isAscending() ? QTypeEntity.typeEntity.typeName.asc() : QTypeEntity.typeEntity.typeName.desc();
            case NameSlug -> sortDirection.isAscending() ? QTypeEntity.typeEntity.nameSlug.asc() : QTypeEntity.typeEntity.nameSlug.desc();
            default -> sortDirection.isAscending() ? QTypeEntity.typeEntity.typeId.asc() : QTypeEntity.typeEntity.typeId.desc();
        };
    }

}
