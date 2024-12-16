package com.example.englishmaster_be.model.question_label;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestionLabelEntity is a Querydsl query type for QuestionLabelEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestionLabelEntity extends EntityPathBase<QuestionLabelEntity> {

    private static final long serialVersionUID = -1792084848L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestionLabelEntity questionLabelEntity = new QQuestionLabelEntity("questionLabelEntity");

    public final StringPath content = createString("content");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath label = createString("label");

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public QQuestionLabelEntity(String variable) {
        this(QuestionLabelEntity.class, forVariable(variable), INITS);
    }

    public QQuestionLabelEntity(Path<? extends QuestionLabelEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestionLabelEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestionLabelEntity(PathMetadata metadata, PathInits inits) {
        this(QuestionLabelEntity.class, metadata, inits);
    }

    public QQuestionLabelEntity(Class<? extends QuestionLabelEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
    }

}

