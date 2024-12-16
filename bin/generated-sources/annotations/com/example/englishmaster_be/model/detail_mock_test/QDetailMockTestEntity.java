package com.example.englishmaster_be.model.detail_mock_test;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDetailMockTestEntity is a Querydsl query type for DetailMockTestEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDetailMockTestEntity extends EntityPathBase<DetailMockTestEntity> {

    private static final long serialVersionUID = 1348154195L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDetailMockTestEntity detailMockTestEntity = new QDetailMockTestEntity("detailMockTestEntity");

    public final com.example.englishmaster_be.model.answer.QAnswerEntity answer;

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> detailMockTestId = createComparable("detailMockTestId", java.util.UUID.class);

    public final com.example.englishmaster_be.model.mock_test.QMockTestEntity mockTest;

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QDetailMockTestEntity(String variable) {
        this(DetailMockTestEntity.class, forVariable(variable), INITS);
    }

    public QDetailMockTestEntity(Path<? extends DetailMockTestEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDetailMockTestEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDetailMockTestEntity(PathMetadata metadata, PathInits inits) {
        this(DetailMockTestEntity.class, metadata, inits);
    }

    public QDetailMockTestEntity(Class<? extends DetailMockTestEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.answer = inits.isInitialized("answer") ? new com.example.englishmaster_be.model.answer.QAnswerEntity(forProperty("answer"), inits.get("answer")) : null;
        this.mockTest = inits.isInitialized("mockTest") ? new com.example.englishmaster_be.model.mock_test.QMockTestEntity(forProperty("mockTest"), inits.get("mockTest")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

