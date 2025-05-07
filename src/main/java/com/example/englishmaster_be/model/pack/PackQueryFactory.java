package com.example.englishmaster_be.model.pack;

import com.example.englishmaster_be.model.part.QPartEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackQueryFactory {

    JPAQueryFactory jpaQueryFactory;

    private JPAQuery<PackEntity> selectFromPackEntity() {

        return jpaQueryFactory.selectFrom(QPackEntity.packEntity);
    }

    public Optional<PackEntity> findPackByName(String packName) {

        BooleanExpression conditionQueryPattern = QPackEntity.packEntity.packName.equalsIgnoreCase(packName);

        PackEntity packResult = selectFromPackEntity().where(conditionQueryPattern).fetchOne();

        return Optional.ofNullable(packResult);
    }

}
