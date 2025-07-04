package com.example.englishmaster_be.domain.flash_card.word.repository;

import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import com.example.englishmaster_be.util.CriteriaUtil;
import com.example.englishmaster_be.util.PageUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;

@Repository
public class FlashCardWordSpecRepository {

    FlashCardWordRepository flashCardWordRepository;

    public FlashCardWordSpecRepository(FlashCardWordRepository flashCardWordRepository) {
        this.flashCardWordRepository = flashCardWordRepository;
    }

    @Transactional(readOnly = true)
    public Page<FlashCardWordEntity> findPageFlashCardWordSpec(PageOptionsReq optionsReq){
        String flashCard = "flashCard";
        String createBy = "createBy";
        Map<String, JoinType> joinHints = Map.of(
                flashCard, JoinType.INNER,
                createBy, JoinType.LEFT
        );
        Specification<FlashCardWordEntity> spec = (root, query, cb) -> {
            if(query.getResultType().equals(Long.class)){
                root.join(flashCard, joinHints.get(flashCard));
                root.join(createBy, joinHints.get(createBy));
            }
            else{
                root.fetch(flashCard, joinHints.get(flashCard));
                root.fetch(createBy, joinHints.get(createBy));
                Sort sort = optionsReq.getPageable().getSort();
                query.orderBy(CriteriaUtil.buildOrders(sort, cb, root));
            }
            Predicate predicate = cb.conjunction();
            String filter = optionsReq.getFilter();
            if(filter != null && !filter.isEmpty() && !filter.isBlank()){
                Predicate predicateFromFilter = CriteriaUtil.toPredicate(filter, root, query, cb);
                if(predicateFromFilter != null)
                    predicate = cb.and(predicate, predicateFromFilter);
            }
            return predicate;
        };
        Pageable unSortPageable = PageUtil.unSortPageable(optionsReq.getPageable());
        return flashCardWordRepository.findAll(spec, unSortPageable);
    }

    public Page<FlashCardWordEntity> findPageFlashCardWordByFlashCardId(UUID flashCardId, PageOptionsReq optionsReq){
        String flashCard = "flashCard";
        String createBy = "createBy";
        Map<String, JoinType> joinHints = Map.of(
                flashCard, JoinType.INNER,
                createBy, JoinType.LEFT
        );
        Specification<FlashCardWordEntity> spec = (root, query, cb) -> {
            if(query.getResultType().equals(Long.class)){
                root.join(flashCard, joinHints.get(flashCard));
                root.join(createBy, joinHints.get(createBy));
                query.groupBy(root.get(flashCard).get("id"));
            }
            else{
                root.fetch(flashCard, joinHints.get(flashCard));
                root.fetch(createBy, joinHints.get(createBy));
                Sort sort = optionsReq.getPageable().getSort();
                query.orderBy(CriteriaUtil.buildOrders(sort, cb, root));
            }
            Predicate predicate = cb.equal(root.get(flashCard).get("id"), flashCardId);
            String filter = optionsReq.getFilter();
            if(filter != null && !filter.isEmpty() && !filter.isBlank()){
                Predicate predicateFromFilter = CriteriaUtil.toPredicate(filter, root, query, cb);
                if(predicateFromFilter != null)
                    predicate = cb.and(predicate, predicateFromFilter);
            }
            return predicate;
        };
        Pageable unSortPageable = PageUtil.unSortPageable(optionsReq.getPageable());
        return flashCardWordRepository.findAll(spec, unSortPageable);
    }

}
