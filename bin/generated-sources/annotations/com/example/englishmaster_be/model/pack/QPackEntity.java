package com.example.englishmaster_be.model.pack;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPackEntity is a Querydsl query type for PackEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPackEntity extends EntityPathBase<PackEntity> {

    private static final long serialVersionUID = -185045473L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPackEntity packEntity = new QPackEntity("packEntity");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> packId = createComparable("packId", java.util.UUID.class);

    public final StringPath packName = createString("packName");

    public final ListPath<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity> topics = this.<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity>createList("topics", com.example.englishmaster_be.model.topic.TopicEntity.class, com.example.englishmaster_be.model.topic.QTopicEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QPackEntity(String variable) {
        this(PackEntity.class, forVariable(variable), INITS);
    }

    public QPackEntity(Path<? extends PackEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPackEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPackEntity(PathMetadata metadata, PathInits inits) {
        this(PackEntity.class, metadata, inits);
    }

    public QPackEntity(Class<? extends PackEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

