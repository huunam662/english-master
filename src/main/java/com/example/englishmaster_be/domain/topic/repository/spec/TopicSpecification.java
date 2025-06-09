package com.example.englishmaster_be.domain.topic.repository.spec;

import com.example.englishmaster_be.common.constant.sort.TopicSortBy;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.UUID;

public class TopicSpecification {

    public static Specification<TopicEntity> isEnable(Boolean isEnable){

        return (root, query, cb) -> isEnable != null ? cb.equal(root.get("enable"), isEnable) : cb.conjunction();
    }

    public static Specification<TopicEntity> innerJoinPackExam(UUID packExamId){

        return (root, query, cb) -> {

            query.distinct(true);

            JoinType joinType = JoinType.INNER;

            if(query.getResultType() != Long.class) {
                root.fetch("pack", joinType);
                if(packExamId != null) {
                    Join<TopicEntity, PackEntity> joinPack = root.join("pack", joinType);
                    return cb.equal(joinPack.get("packId"), packExamId);
                }
            }
            else{
                Join<TopicEntity, PackEntity> joinPack = root.join("pack", joinType);
                if(packExamId != null)
                    return cb.equal(joinPack.get("packId"), packExamId);
            }

            return cb.conjunction();
        };
    }

    public static Specification<TopicEntity> innerJoinTopicType(UUID topicTypeId){

        return (root, query, cb) -> {

            query.distinct(true);

            JoinType joinType = JoinType.INNER;

            if(query.getResultType() != Long.class) {
                root.fetch("topicType", joinType);
                if(topicTypeId != null) {
                    Join<TopicEntity, TopicTypeEntity> joinPack = root.join("topicType", joinType);
                    return cb.equal(joinPack.get("topicTypeId"), topicTypeId);
                }
            }
            else{
                Join<TopicEntity, PackEntity> joinPack = root.join("topicType", joinType);
                if(topicTypeId != null)
                    return cb.equal(joinPack.get("topicTypeId"), topicTypeId);
            }

            return cb.conjunction();
        };
    }

    public static Specification<TopicEntity> leftJoinParts(){

        return (root, query, cb) -> {

            query.distinct(true);

            JoinType joinType = JoinType.LEFT;

            if(!query.getResultType().equals(Long.class))
                root.fetch("parts", joinType);
            else root.join("parts", joinType);

            return cb.conjunction();
        };
    }

    public static Specification<TopicEntity> likeName(String name){

        return (root, query, cb) -> {

            if(name == null) return cb.conjunction();

            return cb.like(cb.lower(root.get("topicName")),"%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<TopicEntity> equalsType(String type){

        return (root, query, cb) -> cb.equal(cb.lower(root.get("topicType")), type.toLowerCase());
    }

    public static Specification<TopicEntity> orderBy(TopicSortBy byField, Sort.Direction direction){

        return (root, query, cb) -> {

            Path<?> field = root.get(byField.getName());

            Order order = direction.isAscending() ? cb.asc(field) : cb.desc(field);

            query.orderBy(order);

            return cb.conjunction();
        };
    }

    public static Specification<TopicEntity> filterTopics(TopicFilterRequest filter){

        return innerJoinPackExam(filter.getPackId())
                .and(leftJoinParts())
                .and(isEnable(filter.getEnabled()))
                .and(likeName(filter.getSearch()))
                .and(orderBy(filter.getSortBy(), filter.getSortDirection()));
    }

}
