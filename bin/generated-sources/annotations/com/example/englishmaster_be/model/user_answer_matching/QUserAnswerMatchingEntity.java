package com.example.englishmaster_be.model.user_answer_matching;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAnswerMatchingEntity is a Querydsl query type for UserAnswerMatchingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAnswerMatchingEntity extends EntityPathBase<UserAnswerMatchingEntity> {

    private static final long serialVersionUID = 706937819L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAnswerMatchingEntity userAnswerMatchingEntity = new QUserAnswerMatchingEntity("userAnswerMatchingEntity");

    public final StringPath contentLeft = createString("contentLeft");

    public final StringPath contentRight = createString("contentRight");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public QUserAnswerMatchingEntity(String variable) {
        this(UserAnswerMatchingEntity.class, forVariable(variable), INITS);
    }

    public QUserAnswerMatchingEntity(Path<? extends UserAnswerMatchingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAnswerMatchingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAnswerMatchingEntity(PathMetadata metadata, PathInits inits) {
        this(UserAnswerMatchingEntity.class, metadata, inits);
    }

    public QUserAnswerMatchingEntity(Class<? extends UserAnswerMatchingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

