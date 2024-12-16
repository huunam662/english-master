package com.example.englishmaster_be.model.answer_matching;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswerMatchingEntity is a Querydsl query type for AnswerMatchingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswerMatchingEntity extends EntityPathBase<AnswerMatchingEntity> {

    private static final long serialVersionUID = -1735525536L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswerMatchingEntity answerMatchingEntity = new QAnswerMatchingEntity("answerMatchingEntity");

    public final StringPath contentLeft = createString("contentLeft");

    public final StringPath contentRight = createString("contentRight");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public QAnswerMatchingEntity(String variable) {
        this(AnswerMatchingEntity.class, forVariable(variable), INITS);
    }

    public QAnswerMatchingEntity(Path<? extends AnswerMatchingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswerMatchingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswerMatchingEntity(PathMetadata metadata, PathInits inits) {
        this(AnswerMatchingEntity.class, metadata, inits);
    }

    public QAnswerMatchingEntity(Class<? extends AnswerMatchingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
    }

}

