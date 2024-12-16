package com.example.englishmaster_be.model.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 520633695L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final StringPath address = createString("address");

    public final StringPath avatar = createString("avatar");

    public final CollectionPath<com.example.englishmaster_be.model.comment.CommentEntity, com.example.englishmaster_be.model.comment.QCommentEntity> comments = this.<com.example.englishmaster_be.model.comment.CommentEntity, com.example.englishmaster_be.model.comment.QCommentEntity>createCollection("comments", com.example.englishmaster_be.model.comment.CommentEntity.class, com.example.englishmaster_be.model.comment.QCommentEntity.class, PathInits.DIRECT2);

    public final ListPath<com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity, com.example.englishmaster_be.model.confirmation_token.QConfirmationTokenEntity> confirmToken = this.<com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity, com.example.englishmaster_be.model.confirmation_token.QConfirmationTokenEntity>createList("confirmToken", com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity.class, com.example.englishmaster_be.model.confirmation_token.QConfirmationTokenEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final ListPath<com.example.englishmaster_be.model.flash_card.FlashCardEntity, com.example.englishmaster_be.model.flash_card.QFlashCardEntity> flashCards = this.<com.example.englishmaster_be.model.flash_card.FlashCardEntity, com.example.englishmaster_be.model.flash_card.QFlashCardEntity>createList("flashCards", com.example.englishmaster_be.model.flash_card.FlashCardEntity.class, com.example.englishmaster_be.model.flash_card.QFlashCardEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> lastLogin = createDateTime("lastLogin", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final ListPath<com.example.englishmaster_be.model.post.PostEntity, com.example.englishmaster_be.model.post.QPostEntity> posts = this.<com.example.englishmaster_be.model.post.PostEntity, com.example.englishmaster_be.model.post.QPostEntity>createList("posts", com.example.englishmaster_be.model.post.PostEntity.class, com.example.englishmaster_be.model.post.QPostEntity.class, PathInits.DIRECT2);

    public final com.example.englishmaster_be.model.role.QRoleEntity role;

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QUserEntity(String variable) {
        this(UserEntity.class, forVariable(variable), INITS);
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserEntity(PathMetadata metadata, PathInits inits) {
        this(UserEntity.class, metadata, inits);
    }

    public QUserEntity(Class<? extends UserEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.role = inits.isInitialized("role") ? new com.example.englishmaster_be.model.role.QRoleEntity(forProperty("role")) : null;
    }

}

