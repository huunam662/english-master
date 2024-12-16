package com.example.englishmaster_be.model.content;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContentEntity is a Querydsl query type for ContentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContentEntity extends EntityPathBase<ContentEntity> {

    private static final long serialVersionUID = -727493099L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentEntity contentEntity = new QContentEntity("contentEntity");

    public final StringPath code = createString("code");

    public final StringPath contentData = createString("contentData");

    public final ComparablePath<java.util.UUID> contentId = createComparable("contentId", java.util.UUID.class);

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public final ComparablePath<java.util.UUID> topicId = createComparable("topicId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QContentEntity(String variable) {
        this(ContentEntity.class, forVariable(variable), INITS);
    }

    public QContentEntity(Path<? extends ContentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContentEntity(PathMetadata metadata, PathInits inits) {
        this(ContentEntity.class, metadata, inits);
    }

    public QContentEntity(Class<? extends ContentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

