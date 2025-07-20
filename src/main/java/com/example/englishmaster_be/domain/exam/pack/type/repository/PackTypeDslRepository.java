package com.example.englishmaster_be.domain.exam.pack.type.repository;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.model.QPackEntity;
import com.example.englishmaster_be.domain.exam.pack.type.dto.view.IPackTypePageView;
import com.example.englishmaster_be.domain.exam.pack.type.model.QPackTypeEntity;
import com.example.englishmaster_be.domain.user.user.model.QUserEntity;
import com.example.englishmaster_be.util.DslUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class PackTypeDslRepository {

    @PersistenceContext
    private final EntityManager em;

    public PackTypeDslRepository(EntityManager em) {
        this.em = em;
    }

    public Page<IPackTypePageView> findPagePackType(PageOptionsReq optionsReq){
        QPackTypeEntity packType = new QPackTypeEntity("packType");
        QUserEntity createdBy = new QUserEntity("createdBy");
        QUserEntity updatedBy = new QUserEntity("updatedBy");
        QPackEntity pack = QPackEntity.packEntity;
        String countPacks = "countPacks";
        Map<String, NumberExpression<?>> numberExpressionMap = Map.of(
                countPacks, pack.countDistinct()
        );
        JPAQuery<IPackTypePageView> query = new JPAQuery<>(em);
        query.select(Projections.bean(
                IPackTypePageView.PackTypePageView.class,
                packType.as(packType),
                numberExpressionMap.get(countPacks).as(countPacks)
        ))
        .from(packType)
        .leftJoin(packType.createdBy, createdBy).fetchJoin()
        .leftJoin(packType.updatedBy, updatedBy).fetchJoin()
        .leftJoin(packType.packs, pack)
        .groupBy(packType, createdBy, updatedBy);
        return DslUtil.fetchPage(em, query, numberExpressionMap, optionsReq);
    }
}
