package com.example.englishmaster_be.domain.topic.repository;

import com.example.englishmaster_be.common.constant.sort.TopicSortBy;
import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterReq;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.util.CriteriaUtil;
import com.example.englishmaster_be.util.PageUtil;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.java.Log;

@Slf4j(topic = "TOPIC-SPEC-REPOSITORY")
@Repository
public class TopicSpecRepository {

    private final TopicRepository topicRepository;

    public TopicSpecRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
   
    public Page<TopicEntity> findPageTopic(PageOptionsReq optionsReq){
        String pack = "pack";
        String parts = "parts";
        Map<String, JoinType> joinHints = Map.of(
            pack, JoinType.INNER,
            parts, JoinType.LEFT
        );
        Specification<TopicEntity> spec = (root, query, cb) -> {
            if(query.getResultType().equals(Long.class)){
                root.join(pack, joinHints.get(pack));
            }
            else{
                root.fetch(pack, joinHints.get(pack));
                root.fetch(parts, joinHints.get(parts));
                Sort sort = optionsReq.getPageable().getSort();
                query.orderBy(CriteriaUtil.buildOrders(sort, cb, root));
            }
            Predicate predicate = cb.conjunction();
            String filter = optionsReq.getFilter();
            if(filter != null && !filter.trim().isEmpty()){
                Predicate filterPredicate = CriteriaUtil.toPredicate(filter, root, query, cb);
                if(filterPredicate != null)
                    return filterPredicate;
            }
            return predicate;
        };
        return topicRepository.findAll(spec, PageUtil.unSortPageable(optionsReq.getPageable()))
    }

}
