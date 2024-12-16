package com.example.englishmaster_be.model.mock_test;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMockTestEntity is a Querydsl query type for MockTestEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMockTestEntity extends EntityPathBase<MockTestEntity> {

    private static final long serialVersionUID = -1191303400L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMockTestEntity mockTestEntity = new QMockTestEntity("mockTestEntity");

    public final NumberPath<Integer> correctAnswers = createNumber("correctAnswers", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final ListPath<com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity, com.example.englishmaster_be.model.detail_mock_test.QDetailMockTestEntity> detailMockTests = this.<com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity, com.example.englishmaster_be.model.detail_mock_test.QDetailMockTestEntity>createList("detailMockTests", com.example.englishmaster_be.model.detail_mock_test.DetailMockTestEntity.class, com.example.englishmaster_be.model.detail_mock_test.QDetailMockTestEntity.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> mockTestId = createComparable("mockTestId", java.util.UUID.class);

    public final ListPath<com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity, com.example.englishmaster_be.model.result_mock_test.QResultMockTestEntity> resultMockTests = this.<com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity, com.example.englishmaster_be.model.result_mock_test.QResultMockTestEntity>createList("resultMockTests", com.example.englishmaster_be.model.result_mock_test.ResultMockTestEntity.class, com.example.englishmaster_be.model.result_mock_test.QResultMockTestEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final TimePath<java.sql.Time> time = createTime("time", java.sql.Time.class);

    public final com.example.englishmaster_be.model.topic.QTopicEntity topic;

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QMockTestEntity(String variable) {
        this(MockTestEntity.class, forVariable(variable), INITS);
    }

    public QMockTestEntity(Path<? extends MockTestEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMockTestEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMockTestEntity(PathMetadata metadata, PathInits inits) {
        this(MockTestEntity.class, metadata, inits);
    }

    public QMockTestEntity(Class<? extends MockTestEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.topic = inits.isInitialized("topic") ? new com.example.englishmaster_be.model.topic.QTopicEntity(forProperty("topic"), inits.get("topic")) : null;
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

