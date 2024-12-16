package com.example.englishmaster_be.model.invalid_token;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInvalidTokenEntity is a Querydsl query type for InvalidTokenEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvalidTokenEntity extends EntityPathBase<InvalidTokenEntity> {

    private static final long serialVersionUID = -1567024556L;

    public static final QInvalidTokenEntity invalidTokenEntity = new QInvalidTokenEntity("invalidTokenEntity");

    public final DateTimePath<java.time.LocalDateTime> expireTime = createDateTime("expireTime", java.time.LocalDateTime.class);

    public final StringPath token = createString("token");

    public QInvalidTokenEntity(String variable) {
        super(InvalidTokenEntity.class, forVariable(variable));
    }

    public QInvalidTokenEntity(Path<? extends InvalidTokenEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInvalidTokenEntity(PathMetadata metadata) {
        super(InvalidTokenEntity.class, metadata);
    }

}

