package com.example.englishmaster_be.model.status;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStatusEntity is a Querydsl query type for StatusEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStatusEntity extends EntityPathBase<StatusEntity> {

    private static final long serialVersionUID = 2035480511L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStatusEntity statusEntity = new QStatusEntity("statusEntity");

    public final BooleanPath flag = createBoolean("flag");

    public final ComparablePath<java.util.UUID> statusId = createComparable("statusId", java.util.UUID.class);

    public final EnumPath<com.example.englishmaster_be.common.constant.StatusEnum> statusName = createEnum("statusName", com.example.englishmaster_be.common.constant.StatusEnum.class);

    public final ListPath<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity> topicList = this.<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity>createList("topicList", com.example.englishmaster_be.model.topic.TopicEntity.class, com.example.englishmaster_be.model.topic.QTopicEntity.class, PathInits.DIRECT2);

    public final com.example.englishmaster_be.model.type.QTypeEntity type;

    public QStatusEntity(String variable) {
        this(StatusEntity.class, forVariable(variable), INITS);
    }

    public QStatusEntity(Path<? extends StatusEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStatusEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStatusEntity(PathMetadata metadata, PathInits inits) {
        this(StatusEntity.class, metadata, inits);
    }

    public QStatusEntity(Class<? extends StatusEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.type = inits.isInitialized("type") ? new com.example.englishmaster_be.model.type.QTypeEntity(forProperty("type")) : null;
    }

}

