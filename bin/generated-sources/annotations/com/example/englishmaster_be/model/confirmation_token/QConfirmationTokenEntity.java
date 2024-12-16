package com.example.englishmaster_be.model.confirmation_token;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConfirmationTokenEntity is a Querydsl query type for ConfirmationTokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConfirmationTokenEntity extends EntityPathBase<ConfirmationTokenEntity> {

    private static final long serialVersionUID = -480720966L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConfirmationTokenEntity confirmationTokenEntity = new QConfirmationTokenEntity("confirmationTokenEntity");

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final EnumPath<com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum> type = createEnum("type", com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum.class);

    public final com.example.englishmaster_be.model.user.QUserEntity user;

    public final ComparablePath<java.util.UUID> userConfirmTokenId = createComparable("userConfirmTokenId", java.util.UUID.class);

    public QConfirmationTokenEntity(String variable) {
        this(ConfirmationTokenEntity.class, forVariable(variable), INITS);
    }

    public QConfirmationTokenEntity(Path<? extends ConfirmationTokenEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConfirmationTokenEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConfirmationTokenEntity(PathMetadata metadata, PathInits inits) {
        this(ConfirmationTokenEntity.class, metadata, inits);
    }

    public QConfirmationTokenEntity(Class<? extends ConfirmationTokenEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.englishmaster_be.model.user.QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

