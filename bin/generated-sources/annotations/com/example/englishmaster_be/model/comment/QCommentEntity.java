package com.example.englishmaster_be.model.comment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentEntity is a Querydsl query type for CommentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentEntity extends EntityPathBase<CommentEntity> {

    private static final long serialVersionUID = 1679096545L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentEntity commentEntity = new QCommentEntity("commentEntity");

    public final ListPath<CommentEntity, QCommentEntity> commentChildren = this.<CommentEntity, QCommentEntity>createList("commentChildren", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> commentId = createComparable("commentId", java.util.UUID.class);

    public final QCommentEntity commentParent;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.post.QPostEntity post;

    public final com.example.englishmaster_be.model.topic.QTopicEntity topic;

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userComment;

    public QCommentEntity(String variable) {
        this(CommentEntity.class, forVariable(variable), INITS);
    }

    public QCommentEntity(Path<? extends CommentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentEntity(PathMetadata metadata, PathInits inits) {
        this(CommentEntity.class, metadata, inits);
    }

    public QCommentEntity(Class<? extends CommentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.commentParent = inits.isInitialized("commentParent") ? new QCommentEntity(forProperty("commentParent"), inits.get("commentParent")) : null;
        this.post = inits.isInitialized("post") ? new com.example.englishmaster_be.model.post.QPostEntity(forProperty("post"), inits.get("post")) : null;
        this.topic = inits.isInitialized("topic") ? new com.example.englishmaster_be.model.topic.QTopicEntity(forProperty("topic"), inits.get("topic")) : null;
        this.userComment = inits.isInitialized("userComment") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userComment"), inits.get("userComment")) : null;
    }

}

