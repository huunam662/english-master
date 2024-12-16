package com.example.englishmaster_be.model.question;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestionEntity is a Querydsl query type for QuestionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestionEntity extends EntityPathBase<QuestionEntity> {

    private static final long serialVersionUID = -1832809409L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestionEntity questionEntity = new QQuestionEntity("questionEntity");

    public final ListPath<com.example.englishmaster_be.model.answer.AnswerEntity, com.example.englishmaster_be.model.answer.QAnswerEntity> answers = this.<com.example.englishmaster_be.model.answer.AnswerEntity, com.example.englishmaster_be.model.answer.QAnswerEntity>createList("answers", com.example.englishmaster_be.model.answer.AnswerEntity.class, com.example.englishmaster_be.model.answer.QAnswerEntity.class, PathInits.DIRECT2);

    public final ListPath<com.example.englishmaster_be.model.content.ContentEntity, com.example.englishmaster_be.model.content.QContentEntity> contentCollection = this.<com.example.englishmaster_be.model.content.ContentEntity, com.example.englishmaster_be.model.content.QContentEntity>createList("contentCollection", com.example.englishmaster_be.model.content.ContentEntity.class, com.example.englishmaster_be.model.content.QContentEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final BooleanPath hasHints = createBoolean("hasHints");

    public final ListPath<com.example.englishmaster_be.model.question_label.QuestionLabelEntity, com.example.englishmaster_be.model.question_label.QQuestionLabelEntity> labels = this.<com.example.englishmaster_be.model.question_label.QuestionLabelEntity, com.example.englishmaster_be.model.question_label.QQuestionLabelEntity>createList("labels", com.example.englishmaster_be.model.question_label.QuestionLabelEntity.class, com.example.englishmaster_be.model.question_label.QQuestionLabelEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> numberChoice = createNumber("numberChoice", Integer.class);

    public final com.example.englishmaster_be.model.part.QPartEntity part;

    public final StringPath questionContent = createString("questionContent");

    public final StringPath questionExplainEn = createString("questionExplainEn");

    public final StringPath questionExplainVn = createString("questionExplainVn");

    public final ListPath<QuestionEntity, QQuestionEntity> questionGroupChildren = this.<QuestionEntity, QQuestionEntity>createList("questionGroupChildren", QuestionEntity.class, QQuestionEntity.class, PathInits.DIRECT2);

    public final QQuestionEntity questionGroupParent;

    public final ComparablePath<java.util.UUID> questionId = createComparable("questionId", java.util.UUID.class);

    public final NumberPath<Integer> questionNumberical = createNumber("questionNumberical", Integer.class);

    public final NumberPath<Integer> questionScore = createNumber("questionScore", Integer.class);

    public final EnumPath<com.example.englishmaster_be.common.constant.QuestionTypeEnum> questionType = createEnum("questionType", com.example.englishmaster_be.common.constant.QuestionTypeEnum.class);

    public final ListPath<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity> topics = this.<com.example.englishmaster_be.model.topic.TopicEntity, com.example.englishmaster_be.model.topic.QTopicEntity>createList("topics", com.example.englishmaster_be.model.topic.TopicEntity.class, com.example.englishmaster_be.model.topic.QTopicEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QQuestionEntity(String variable) {
        this(QuestionEntity.class, forVariable(variable), INITS);
    }

    public QQuestionEntity(Path<? extends QuestionEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestionEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestionEntity(PathMetadata metadata, PathInits inits) {
        this(QuestionEntity.class, metadata, inits);
    }

    public QQuestionEntity(Class<? extends QuestionEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.part = inits.isInitialized("part") ? new com.example.englishmaster_be.model.part.QPartEntity(forProperty("part"), inits.get("part")) : null;
        this.questionGroupParent = inits.isInitialized("questionGroupParent") ? new QQuestionEntity(forProperty("questionGroupParent"), inits.get("questionGroupParent")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

