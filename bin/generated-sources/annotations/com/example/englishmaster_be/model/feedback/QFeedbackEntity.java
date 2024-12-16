package com.example.englishmaster_be.model.feedback;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFeedbackEntity is a Querydsl query type for FeedbackEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeedbackEntity extends EntityPathBase<FeedbackEntity> {

    private static final long serialVersionUID = -1108998369L;

    public static final QFeedbackEntity feedbackEntity = new QFeedbackEntity("feedbackEntity");

    public final StringPath avatar = createString("avatar");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final BooleanPath enable = createBoolean("enable");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public QFeedbackEntity(String variable) {
        super(FeedbackEntity.class, forVariable(variable));
    }

    public QFeedbackEntity(Path<? extends FeedbackEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFeedbackEntity(PathMetadata metadata) {
        super(FeedbackEntity.class, metadata);
    }

}

