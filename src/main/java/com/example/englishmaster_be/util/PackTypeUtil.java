package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.sort.PackTypeSortBy;
import com.example.englishmaster_be.domain.pack_type.dto.projection.IPackTypeKeyProjection;
import com.example.englishmaster_be.model.pack_type.PackTypeEntity;
import com.example.englishmaster_be.model.pack_type.PackTypeJdbcRepository;
import com.example.englishmaster_be.model.pack_type.QPackTypeEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Sort;

import java.util.UUID;

public class PackTypeUtil {

    public static OrderSpecifier<?> buildPackTypeOrderSpecifier(PackTypeSortBy sortBy, Sort.Direction direction){

        boolean isAscending = direction.isAscending();

        return switch (sortBy){
            case TYPE_NAME -> isAscending ? QPackTypeEntity.packTypeEntity.name.asc() : QPackTypeEntity.packTypeEntity.name.desc();
            default -> isAscending ? QPackTypeEntity.packTypeEntity.updatedAt.asc() : QPackTypeEntity.packTypeEntity.updatedAt.desc();
        };
    }

}
