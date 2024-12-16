package com.example.englishmaster_be.model.answer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswerEntity is a Querydsl query type for AnswerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswerEntity extends EntityPathBase<AnswerEntity> {

    private static final long serialVersionUID = 1747188287L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswerEntity answerEntity = new QAnswerEntity("answerEntity");

    public final StringPath answerContent = createString("answerContent");

    public final ComparablePath<java.util.UUID> answerId = createComparable("answerId", java.util.UUID.class);

    public final BooleanPath correctAnswer = createBoolean("correctAnswer");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath explainDetails = createString("explainDetails");

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final ListPath<com.example.englishmaster_be.model.user_answer.UserAnswerEntity, com.example.englishmaster_be.model.user_answer.QUserAnswerEntity> userAnswers = this.<com.example.englishmaster_be.model.user_answer.UserAnswerEntity, com.example.englishmaster_be.model.user_answer.QUserAnswerEntity>createList("userAnswers", com.example.englishmaster_be.model.user_answer.UserAnswerEntity.class, com.example.englishmaster_be.model.user_answer.QUserAnswerEntity.class, PathInits.DIRECT2);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QAnswerEntity(String variable) {
        this(AnswerEntity.class, forVariable(variable), INITS);
    }

    public QAnswerEntity(Path<? extends AnswerEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswerEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswerEntity(PathMetadata metadata, PathInits inits) {
        this(AnswerEntity.class, metadata, inits);
    }

    public QAnswerEntity(Class<? extends AnswerEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

