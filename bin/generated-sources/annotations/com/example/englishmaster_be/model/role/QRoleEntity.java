package com.example.englishmaster_be.model.role;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoleEntity is a Querydsl query type for RoleEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoleEntity extends EntityPathBase<RoleEntity> {

    private static final long serialVersionUID = -1525168065L;

    public static final QRoleEntity roleEntity = new QRoleEntity("roleEntity");

    public final ComparablePath<java.util.UUID> roleId = createComparable("roleId", java.util.UUID.class);

    public final EnumPath<com.example.englishmaster_be.common.constant.RoleEnum> roleName = createEnum("roleName", com.example.englishmaster_be.common.constant.RoleEnum.class);

    public final ListPath<com.example.englishmaster_be.model.user.UserEntity, com.example.englishmaster_be.model.user.QUserEntity> users = this.<com.example.englishmaster_be.model.user.UserEntity, com.example.englishmaster_be.model.user.QUserEntity>createList("users", com.example.englishmaster_be.model.user.UserEntity.class, com.example.englishmaster_be.model.user.QUserEntity.class, PathInits.DIRECT2);

    public QRoleEntity(String variable) {
        super(RoleEntity.class, forVariable(variable));
    }

    public QRoleEntity(Path<? extends RoleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleEntity(PathMetadata metadata) {
        super(RoleEntity.class, metadata);
    }

}

