package com.example.englishmaster_be.model.user_blank_answer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserBlankAnswerEntity is a Querydsl query type for UserBlankAnswerEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBlankAnswerEntity extends EntityPathBase<UserBlankAnswerEntity> {

    private static final long serialVersionUID = -932909177L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserBlankAnswerEntity userBlankAnswerEntity = new QUserBlankAnswerEntity("userBlankAnswerEntity");

    public final StringPath answer = createString("answer");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public QUserBlankAnswerEntity(String variable) {
        this(UserBlankAnswerEntity.class, forVariable(variable), INITS);
    }

    public QUserBlankAnswerEntity(Path<? extends UserBlankAnswerEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserBlankAnswerEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserBlankAnswerEntity(PathMetadata metadata, PathInits inits) {
        this(UserBlankAnswerEntity.class, metadata, inits);
    }

    public QUserBlankAnswerEntity(Class<? extends UserBlankAnswerEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

