package com.example.englishmaster_be.model.otp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtpEntity is a Querydsl query type for OtpEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOtpEntity extends EntityPathBase<OtpEntity> {

    private static final long serialVersionUID = -877583623L;

    public static final QOtpEntity otpEntity = new QOtpEntity("otpEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expirationTime = createDateTime("expirationTime", java.time.LocalDateTime.class);

    public final StringPath otp = createString("otp");

    public final StringPath status = createString("status");

    public QOtpEntity(String variable) {
        super(OtpEntity.class, forVariable(variable));
    }

    public QOtpEntity(Path<? extends OtpEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtpEntity(PathMetadata metadata) {
        super(OtpEntity.class, metadata);
    }

}

