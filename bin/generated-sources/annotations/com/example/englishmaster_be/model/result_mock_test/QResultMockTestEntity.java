package com.example.englishmaster_be.model.result_mock_test;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResultMockTestEntity is a Querydsl query type for ResultMockTestEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResultMockTestEntity extends EntityPathBase<ResultMockTestEntity> {

    private static final long serialVersionUID = 626173139L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResultMockTestEntity resultMockTestEntity = new QResultMockTestEntity("resultMockTestEntity");

    public final NumberPath<Integer> correctAnswer = createNumber("correctAnswer", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.mock_test.QMockTestEntity mockTest;

    public final com.example.englishmaster_be.model.part.QPartEntity part;

    public final ComparablePath<java.util.UUID> resultMockTestId = createComparable("resultMockTestId", java.util.UUID.class);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QResultMockTestEntity(String variable) {
        this(ResultMockTestEntity.class, forVariable(variable), INITS);
    }

    public QResultMockTestEntity(Path<? extends ResultMockTestEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResultMockTestEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResultMockTestEntity(PathMetadata metadata, PathInits inits) {
        this(ResultMockTestEntity.class, metadata, inits);
    }

    public QResultMockTestEntity(Class<? extends ResultMockTestEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mockTest = inits.isInitialized("mockTest") ? new com.example.englishmaster_be.model.mock_test.QMockTestEntity(forProperty("mockTest"), inits.get("mockTest")) : null;
        this.part = inits.isInitialized("part") ? new com.example.englishmaster_be.model.part.QPartEntity(forProperty("part"), inits.get("part")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

