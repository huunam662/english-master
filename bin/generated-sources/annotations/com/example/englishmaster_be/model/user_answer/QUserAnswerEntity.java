package com.example.englishmaster_be.model.user_answer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAnswerEntity is a Querydsl query type for UserAnswerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAnswerEntity extends EntityPathBase<UserAnswerEntity> {

    private static final long serialVersionUID = 1934410650L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAnswerEntity userAnswerEntity = new QUserAnswerEntity("userAnswerEntity");

    public final ListPath<com.example.englishmaster_be.model.answer.AnswerEntity, com.example.englishmaster_be.model.answer.QAnswerEntity> answers = this.<com.example.englishmaster_be.model.answer.AnswerEntity, com.example.englishmaster_be.model.answer.QAnswerEntity>createList("answers", com.example.englishmaster_be.model.answer.AnswerEntity.class, com.example.englishmaster_be.model.answer.QAnswerEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> numberChoice = createNumber("numberChoice", Integer.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public QUserAnswerEntity(String variable) {
        this(UserAnswerEntity.class, forVariable(variable), INITS);
    }

    public QUserAnswerEntity(Path<? extends UserAnswerEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAnswerEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAnswerEntity(PathMetadata metadata, PathInits inits) {
        this(UserAnswerEntity.class, metadata, inits);
    }

    public QUserAnswerEntity(Class<? extends UserAnswerEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

