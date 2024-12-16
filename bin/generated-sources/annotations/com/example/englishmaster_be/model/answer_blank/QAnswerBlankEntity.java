package com.example.englishmaster_be.model.answer_blank;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswerBlankEntity is a Querydsl query type for AnswerBlankEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswerBlankEntity extends EntityPathBase<AnswerBlankEntity> {

    private static final long serialVersionUID = 1352237712L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswerBlankEntity answerBlankEntity = new QAnswerBlankEntity("answerBlankEntity");

    public final StringPath answer = createString("answer");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public QAnswerBlankEntity(String variable) {
        this(AnswerBlankEntity.class, forVariable(variable), INITS);
    }

    public QAnswerBlankEntity(Path<? extends AnswerBlankEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswerBlankEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswerBlankEntity(PathMetadata metadata, PathInits inits) {
        this(AnswerBlankEntity.class, metadata, inits);
    }

    public QAnswerBlankEntity(Class<? extends AnswerBlankEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
    }

}

