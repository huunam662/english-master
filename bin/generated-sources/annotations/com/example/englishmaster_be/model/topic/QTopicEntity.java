package com.example.englishmaster_be.model.topic;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTopicEntity is a Querydsl query type for TopicEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopicEntity extends EntityPathBase<TopicEntity> {

    private static final long serialVersionUID = 673897409L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTopicEntity topicEntity = new QTopicEntity("topicEntity");

    public final ListPath<com.example.englishmaster_be.model.comment.CommentEntity, com.example.englishmaster_be.model.comment.QCommentEntity> comments = this.<com.example.englishmaster_be.model.comment.CommentEntity, com.example.englishmaster_be.model.comment.QCommentEntity>createList("comments", com.example.englishmaster_be.model.comment.CommentEntity.class, com.example.englishmaster_be.model.comment.QCommentEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final BooleanPath enable = createBoolean("enable");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final ListPath<com.example.englishmaster_be.model.mock_test.MockTestEntity, com.example.englishmaster_be.model.mock_test.QMockTestEntity> mockTests = this.<com.example.englishmaster_be.model.mock_test.MockTestEntity, com.example.englishmaster_be.model.mock_test.QMockTestEntity>createList("mockTests", com.example.englishmaster_be.model.mock_test.MockTestEntity.class, com.example.englishmaster_be.model.mock_test.QMockTestEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> numberQuestion = createNumber("numberQuestion", Integer.class);

    public final com.example.englishmaster_be.model.pack.QPackEntity pack;

    public final ListPath<com.example.englishmaster_be.model.part.PartEntity, com.example.englishmaster_be.model.part.QPartEntity> parts = this.<com.example.englishmaster_be.model.part.PartEntity, com.example.englishmaster_be.model.part.QPartEntity>createList("parts", com.example.englishmaster_be.model.part.PartEntity.class, com.example.englishmaster_be.model.part.QPartEntity.class, PathInits.DIRECT2);

    public final ListPath<com.example.englishmaster_be.model.question.QuestionEntity, com.example.englishmaster_be.model.question.QQuestionEntity> questions = this.<com.example.englishmaster_be.model.question.QuestionEntity, com.example.englishmaster_be.model.question.QQuestionEntity>createList("questions", com.example.englishmaster_be.model.question.QuestionEntity.class, com.example.englishmaster_be.model.question.QQuestionEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.status.QStatusEntity status;

    public final StringPath topicDescription = createString("topicDescription");

    public final ComparablePath<java.util.UUID> topicId = createComparable("topicId", java.util.UUID.class);

    public final StringPath topicImage = createString("topicImage");

    public final StringPath topicName = createString("topicName");

    public final StringPath topicType = createString("topicType");

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public final StringPath workTime = createString("workTime");

    public QTopicEntity(String variable) {
        this(TopicEntity.class, forVariable(variable), INITS);
    }

    public QTopicEntity(Path<? extends TopicEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTopicEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTopicEntity(PathMetadata metadata, PathInits inits) {
        this(TopicEntity.class, metadata, inits);
    }

    public QTopicEntity(Class<? extends TopicEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pack = inits.isInitialized("pack") ? new com.example.englishmaster_be.model.pack.QPackEntity(forProperty("pack"), inits.get("pack")) : null;
        this.status = inits.isInitialized("status") ? new com.example.englishmaster_be.model.status.QStatusEntity(forProperty("status"), inits.get("status")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

