package com.example.englishmaster_be.model.flash_card_word;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFlashCardWordEntity is a Querydsl query type for FlashCardWordEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFlashCardWordEntity extends EntityPathBase<FlashCardWordEntity> {

    private static final long serialVersionUID = -1022278473L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFlashCardWordEntity flashCardWordEntity = new QFlashCardWordEntity("flashCardWordEntity");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final StringPath define = createString("define");

    public final StringPath example = createString("example");

    public final com.example.englishmaster_be.model.flash_card.QFlashCardEntity flashCard;

    public final StringPath image = createString("image");

    public final StringPath note = createString("note");

    public final StringPath spelling = createString("spelling");

    public final StringPath type = createString("type");

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final com.example.englishmaster_be.model.user.QUserEntity userCreate;

    public final com.example.englishmaster_be.model.user.QUserEntity userUpdate;

    public final StringPath word = createString("word");

    public final ComparablePath<java.util.UUID> wordId = createComparable("wordId", java.util.UUID.class);

    public QFlashCardWordEntity(String variable) {
        this(FlashCardWordEntity.class, forVariable(variable), INITS);
    }

    public QFlashCardWordEntity(Path<? extends FlashCardWordEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFlashCardWordEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFlashCardWordEntity(PathMetadata metadata, PathInits inits) {
        this(FlashCardWordEntity.class, metadata, inits);
    }

    public QFlashCardWordEntity(Class<? extends FlashCardWordEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.flashCard = inits.isInitialized("flashCard") ? new com.example.englishmaster_be.model.flash_card.QFlashCardEntity(forProperty("flashCard"), inits.get("flashCard")) : null;
        this.userCreate = inits.isInitialized("userCreate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userCreate"), inits.get("userCreate")) : null;
        this.userUpdate = inits.isInitialized("userUpdate") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("userUpdate"), inits.get("userUpdate")) : null;
    }

}

