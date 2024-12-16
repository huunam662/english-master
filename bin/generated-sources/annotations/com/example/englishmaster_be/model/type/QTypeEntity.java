package com.example.englishmaster_be.model.type;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTypeEntity is a Querydsl query type for TypeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTypeEntity extends EntityPathBase<TypeEntity> {

    private static final long serialVersionUID = 944276159L;

    public static final QTypeEntity typeEntity = new QTypeEntity("typeEntity");

    public final StringPath nameSlug = createString("nameSlug");

    public final ListPath<com.example.englishmaster_be.model.status.StatusEntity, com.example.englishmaster_be.model.status.QStatusEntity> statuses = this.<com.example.englishmaster_be.model.status.StatusEntity, com.example.englishmaster_be.model.status.QStatusEntity>createList("statuses", com.example.englishmaster_be.model.status.StatusEntity.class, com.example.englishmaster_be.model.status.QStatusEntity.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> typeId = createComparable("typeId", java.util.UUID.class);

    public final StringPath typeName = createString("typeName");

    public QTypeEntity(String variable) {
        super(TypeEntity.class, forVariable(variable));
    }

    public QTypeEntity(Path<? extends TypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTypeEntity(PathMetadata metadata) {
        super(TypeEntity.class, metadata);
    }

}

