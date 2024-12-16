package com.example.englishmaster_be.model.flash_card;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFlashCardEntity is a Querydsl query type for FlashCardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFlashCardEntity extends EntityPathBase<FlashCardEntity> {

    private static final long serialVersionUID = -997006170L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFlashCardEntity flashCardEntity = new QFlashCardEntity("flashCardEntity");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath flashCardDescription = createString("flashCardDescription");

    public final ComparablePath<java.util.UUID> flashCardId = createComparable("flashCardId", java.util.UUID.class);

    public final StringPath flashCardImage = createString("flashCardImage");

    public final StringPath flashCardTitle = createString("flashCardTitle");

    public final ListPath<com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity, com.example.englishmaster_be.model.flash_card_word.QFlashCardWordEntity> flashCardWords = this.<com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity, com.example.englishmaster_be.model.flash_card_word.QFlashCardWordEntity>createList("flashCardWords", com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity.class, com.example.englishmaster_be.model.flash_card_word.QFlashCardWordEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public QFlashCardEntity(String variable) {
        this(FlashCardEntity.class, forVariable(variable), INITS);
    }

    public QFlashCardEntity(Path<? extends FlashCardEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFlashCardEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFlashCardEntity(PathMetadata metadata, PathInits inits) {
        this(FlashCardEntity.class, metadata, inits);
    }

    public QFlashCardEntity(Class<? extends FlashCardEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

