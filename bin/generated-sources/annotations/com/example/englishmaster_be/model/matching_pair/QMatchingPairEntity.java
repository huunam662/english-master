package com.example.englishmaster_be.model.matching_pair;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchingPairEntity is a Querydsl query type for MatchingPairEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchingPairEntity extends EntityPathBase<MatchingPairEntity> {

    private static final long serialVersionUID = 1938175678L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchingPairEntity matchingPairEntity = new QMatchingPairEntity("matchingPairEntity");

    public final StringPath answer = createString("answer");

    public final StringPath content = createString("content");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.example.englishmaster_be.model.question.QQuestionEntity question;

    public QMatchingPairEntity(String variable) {
        this(MatchingPairEntity.class, forVariable(variable), INITS);
    }

    public QMatchingPairEntity(Path<? extends MatchingPairEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchingPairEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchingPairEntity(PathMetadata metadata, PathInits inits) {
        this(MatchingPairEntity.class, metadata, inits);
    }

    public QMatchingPairEntity(Class<? extends MatchingPairEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new com.example.englishmaster_be.model.question.QQuestionEntity(forProperty("question"), inits.get("question")) : null;
    }

}

