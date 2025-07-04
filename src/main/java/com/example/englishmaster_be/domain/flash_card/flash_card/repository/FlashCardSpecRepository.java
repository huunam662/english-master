package com.example.englishmaster_be.domain.flash_card.flash_card.repository;

import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.util.CriteriaUtil;
import com.example.englishmaster_be.util.PageUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class FlashCardSpecRepository {

    private final FlashCardRepository flashCardRepository;

    public FlashCardSpecRepository(FlashCardRepository flashCardRepository) {
        this.flashCardRepository = flashCardRepository;
    }

    public Page<FlashCardEntity> findPageFlashCardSpec(PageOptionsReq optionsReq){
        String createBy = "createBy";
        Map<String, JoinType> joinHints = Map.of(
                createBy, JoinType.INNER
        );
        Specification<FlashCardEntity> spec = (root, query, cb) -> {
            if(query.getResultType().equals(Long.class)){
                root.join(createBy, joinHints.get(createBy));
            }
            else{
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
        return flashCardRepository.findAll(spec, unSortPageable);
    }

}
