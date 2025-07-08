package com.example.englishmaster_be.domain.flash_card.feedback.repository;

import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import com.example.englishmaster_be.util.CriteriaUtil;
import com.example.englishmaster_be.util.PageUtil;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class FlashCardFeedbackSpecRepository {

    private final FlashCardFeedbackRepository flashCardFeedbackRepository;

    public FlashCardFeedbackSpecRepository(FlashCardFeedbackRepository flashCardFeedbackRepository) {
        this.flashCardFeedbackRepository = flashCardFeedbackRepository;
    }

    public Page<FlashCardFeedbackEntity> findPageFlashCardFeedback(PageOptionsReq optionsReq){
        String flashCard = "flashCard";
        String userFeedback = "userFeedback";
        Map<String, JoinType> joinHints = Map.of(
                flashCard, JoinType.LEFT,
                userFeedback, JoinType.LEFT
        );
        Specification<FlashCardFeedbackEntity> specification = (root, query, cb) -> {
            if(query.getResultType().equals(Long.class)){
                root.join(flashCard, joinHints.get(flashCard));
                root.join(userFeedback, joinHints.get(userFeedback));
            }
            else{
                root.fetch(flashCard, joinHints.get(flashCard));
                root.fetch(userFeedback, joinHints.get(userFeedback));
                Sort sort = optionsReq.getPageable().getSort();
                query.orderBy(CriteriaUtil.buildOrders(sort, cb, root));
            }
            Predicate predicate = cb.conjunction();
            String filter = optionsReq.getFilter();
            if(filter != null && !filter.isEmpty() && !filter.isBlank()){
                Predicate predicateFilter = CriteriaUtil.toPredicate(filter, root, query, cb);
                if(predicateFilter != null)
                    predicate = cb.and(predicate, predicateFilter);
            }
            return predicate;
        };
        Pageable unSortPageable = PageUtil.unSortPageable(optionsReq.getPageable());
        return flashCardFeedbackRepository.findAll(specification, unSortPageable);
    }

    public Page<FlashCardFeedbackEntity> findPageFlashCardFeedbackByFlashCardId(UUID flashCardId, PageOptionsReq optionsReq){
        String flashCard = "flashCard";
        String userFeedback = "userFeedback";
        Map<String, JoinType> joinHints = Map.of(
                flashCard, JoinType.INNER,
                userFeedback, JoinType.LEFT
        );
        Specification<FlashCardFeedbackEntity> specification = (root, query, cb) -> {
            Predicate predicate;
            if(query.getResultType().equals(Long.class)){
                root.join(flashCard, joinHints.get(flashCard));
                root.join(userFeedback, joinHints.get(userFeedback));
                predicate = cb.equal(root.get(flashCard).get("id"), flashCardId);
            }
            else{
                root.fetch(userFeedback, joinHints.get(userFeedback));
                predicate = cb.equal(root.get("flashCardId"), flashCardId);
                Sort sort = optionsReq.getPageable().getSort();
                query.orderBy(CriteriaUtil.buildOrders(sort, cb, root));
            }
            String filter = optionsReq.getFilter();
            if(filter != null && !filter.isEmpty() && !filter.isBlank()){
                Predicate predicateFilter = CriteriaUtil.toPredicate(filter, root, query, cb);
                if(predicateFilter != null){
                    predicate = cb.and(predicate, predicateFilter);
                }
            }
            return predicate;
        };
        Pageable unSortPageable = PageUtil.unSortPageable(optionsReq.getPageable());
        return flashCardFeedbackRepository.findAll(specification, unSortPageable);
    }
}
