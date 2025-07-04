package com.example.englishmaster_be.util;

import io.github.perplexhub.rsql.RSQLJPASupport;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Objects;

public class CriteriaUtil {

    public static <T, R> Predicate toPredicate(String filter, Root<T> root, CriteriaQuery<R> cQuery, CriteriaBuilder cBuilder){
        Logger log = LoggerFactory.getLogger(CriteriaUtil.class);
        try{
            return RSQLJPASupport.<T>toSpecification(filter).toPredicate(root, cQuery, cBuilder);
        }
        catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<Order> buildOrders(Sort sort, CriteriaBuilder cBuilder, Root<T> root){
        Logger log = LoggerFactory.getLogger(CriteriaUtil.class);
        return sort.stream().map(
                order -> {
                    try {
                        String orderField = order.getProperty();
                        Path<?> pathOrder = root;
                        String[] fields = orderField.split("\\.");
                        for(String field : fields){
                            pathOrder = pathOrder.get(field.trim());
                        }
                        return order.isAscending() ? cBuilder.asc(pathOrder) : cBuilder.desc(pathOrder);
                    }
                    catch (Exception e){
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }
        ).filter(Objects::nonNull).toList();
    }

}
