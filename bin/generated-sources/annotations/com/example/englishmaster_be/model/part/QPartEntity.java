package com.example.englishmaster_be.model.part;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartEntity is a Querydsl query type for PartEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartEntity extends EntityPathBase<PartEntity> {

    private static final long serialVersionUID = 640570975L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartEntity partEntity = new QPartEntity("partEntity");

    public final StringPath contentData = createString("contentData");

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath partDescription = createString("partDescription");

    public final ComparablePath<java.util.UUID> partId = createComparable("partId", java.util.UUID.class);

    public final StringPath partName = createString("partName");

    public final StringPath partType = createString("partType");

    public final ListPath<com.example.englishmaster_be.model.question.QuestionEntity, com.example.englishmaster_be.model.question.QQuestionEntity> questions = this.<com.example.englishmaster_be.model.question.QuestionEntity, com.example.englishmaster_be.model.question.QQuestionEntity>createList("questions", com.example.englishmaster_be.model.question.QuestionEntity.class, com.example.englishmaster_be.model.question.QQuestionEntity.class, PathInits.DIRECT2);

    public final ListPath<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity> topics = this.<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity>createList("topics", com.example.englishmaster_be.model.topic.TopicEntity.class, com.example.englishmaster_be.model.topic.QTopicEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QPartEntity(String variable) {
        this(PartEntity.class, forVariable(variable), INITS);
    }

    public QPartEntity(Path<? extends PartEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartEntity(PathMetadata metadata, PathInits inits) {
        this(PartEntity.class, metadata, inits);
    }

    public QPartEntity(Class<? extends PartEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

