package com.example.englishmaster_be.model.news;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNewsEntity is a Querydsl query type for NewsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNewsEntity extends EntityPathBase<NewsEntity> {

    private static final long serialVersionUID = -708140449L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNewsEntity newsEntity = new QNewsEntity("newsEntity");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final BooleanPath enable = createBoolean("enable");

    public final StringPath image = createString("image");

    public final ComparablePath<java.util.UUID> newsId = createComparable("newsId", java.util.UUID.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QNewsEntity(String variable) {
        this(NewsEntity.class, forVariable(variable), INITS);
    }

    public QNewsEntity(Path<? extends NewsEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNewsEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNewsEntity(PathMetadata metadata, PathInits inits) {
        this(NewsEntity.class, metadata, inits);
    }

    public QNewsEntity(Class<? extends NewsEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

