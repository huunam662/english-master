package com.example.englishmaster_be.util;


import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.*;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.util.function.Function;

@Slf4j(topic = "DSL-UTIL")
public class DslUtil {

    private static List<ConstantImpl<?>> collectValuesCondition(
            @NonNull List<Expression<?>> expressions
    ){
        List<ConstantImpl<?>> results = new ArrayList<>();
        for (Expression<?> e : expressions) {
            if (e instanceof PredicateOperation || e instanceof BooleanOperation) {
                Operation<?> o = (Operation<?>) e;
                results.addAll(collectValuesCondition(o.getArgs()));
            }
            else if (e instanceof ConstantImpl<?> constant) {
                results.add(constant);
            }
        }
        return results;
    }

    public static <T> Page<T> fetchPage(
            @NonNull EntityManager em,
            @NonNull JPAQuery<T> query,
            @NonNull Map<String, NumberExpression<?>> expressionsHavingMap,
            @NonNull PageOptionsReq optionsReq,
            @NonNull Map<String, String> propertyPathMap
    ){
        EntityPathBase<?> root = (EntityPathBase<?>) query.getMetadata().getJoins().get(0).getTarget();
        String rootName = root.toString();
        String filter = optionsReq.getFilter();
        if(filter != null && !filter.trim().isEmpty()){
            Predicate filterPredicate = DslUtil.toPredicateWhere(filter, root, propertyPathMap);
            Predicate whereCurrent = query.getMetadata().getWhere();
            if(whereCurrent != null){
                query.getMetadata().clearWhere();
                query.where(ExpressionUtils.and(whereCurrent, filterPredicate));
            }
            else query.where(filterPredicate);
        }
        String having = optionsReq.getHaving();
        if(having != null && !having.trim().isEmpty() && !expressionsHavingMap.isEmpty()){
            Predicate havingPredicate = DslUtil.toPredicateHaving(having, expressionsHavingMap);
            Predicate havingCurrent = query.getMetadata().getHaving();
            if(havingCurrent != null){
                query.having(ExpressionUtils.and(havingCurrent, havingPredicate));
            }
            else query.having(havingPredicate);
        }
        Pageable pageable = PageUtil.reBuildSortPageable(optionsReq.getPageable(), property -> {
            if(property.startsWith(rootName + ".")) return property.replaceFirst(rootName + "\\.", "");
            return property;
        });
        if(!pageable.getSort().isEmpty()){
            query.orderBy(DslUtil.buildOrder(root, expressionsHavingMap, pageable).toArray(OrderSpecifier[]::new));
        }
        List<T> pageViews = query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        long totalElements = DslUtil.fetchCount(em, query);
        return new PageImpl<>(pageViews, pageable, totalElements);
    }

    public static <T> Page<T> fetchPage(
            @NonNull EntityManager em,
            @NonNull JPAQuery<T> query,
            @NonNull Map<String, NumberExpression<?>> expressionsHavingMap,
            @NonNull PageOptionsReq optionsReq
    ){
        return DslUtil.fetchPage(em, query, expressionsHavingMap, optionsReq, Map.of());
    }

    public static <T> Page<T> fetchPage(
            @NonNull EntityManager em,
            @NonNull JPAQuery<T> query,
            @NonNull PageOptionsReq optionsReq,
            @NonNull Map<String, String> propertyPathMap
    ){
        return DslUtil.fetchPage(em, query, Map.of(), optionsReq, propertyPathMap);
    }

    public static <T> Page<T> fetchPage(
            @NonNull EntityManager em,
            @NonNull JPAQuery<T> query,
            @NonNull PageOptionsReq optionsReq
    ){
        return DslUtil.fetchPage(em, query, Map.of(), optionsReq, Map.of());
    }

    public static long fetchCount(
            @NonNull EntityManager em,
            @NonNull JPAQuery<?> query
    ){
        String jpql = query.toString().replaceAll("fetch", "");
        String orderByKeyWord = "order by";
        if(jpql.contains(orderByKeyWord)){
            jpql = jpql.split(orderByKeyWord)[0].trim();
        }
        List<Expression<?>> expressions = new ArrayList<>();
        PredicateOperation where = (PredicateOperation) query.getMetadata().getWhere();
        if(where != null) expressions.addAll(where.getArgs());
        PredicateOperation having = (PredicateOperation) query.getMetadata().getHaving();
        if(having != null) expressions.addAll(having.getArgs());
        String countQuerySql = "SELECT COUNT(*) FROM (" + jpql + ") AS subQuery";
        TypedQuery<Long> typedQuery = em.createQuery(countQuerySql, Long.class);
        List<ConstantImpl<?>> valuesCondition = collectValuesCondition(expressions);
        for(int i = 0; i < valuesCondition.size(); i++){
            ConstantImpl<?> value = valuesCondition.get(i);
            typedQuery.setParameter(i + 1, value.getConstant());
        }
        Long count = typedQuery.getSingleResult();
        return Optional.ofNullable(count).orElse(0L);
    }

    public static Predicate toPredicateHaving(
            @NonNull String having,
            @NonNull Map<String, NumberExpression<?>> numberExpressionMap
    ) {
        if (having.trim().isEmpty()) {
            return null;
        }
        Node rootNode = new RSQLParser().parse(having);
        return rootNode.accept(new DslUtil.QueryDSLExpressionVisitor(alias -> {
            NumberExpression<?> expr = numberExpressionMap.getOrDefault(alias, null);
            if (expr == null) throw new ApplicationException(HttpStatus.BAD_REQUEST, "Unknown alias: " + alias);
            return expr;
        }));
    }

    public static Predicate toPredicateWhere(
            @NonNull String filter,
            @NonNull EntityPathBase<?> root,
            @NonNull Map<String, String> propertyPathMap
    ){
        if (filter.trim().isEmpty()) {
            return null;
        }
        String rootName = root.toString();
        if(filter.contains(rootName + "."))
            filter = filter.replaceAll(rootName + "\\.", "");
        return RSQLQueryDslSupport.toPredicate(filter, root, propertyPathMap);
    }

    public static List<OrderSpecifier<?>> buildOrder(
            @NonNull EntityPathBase<?> root,
            @NonNull Map<String, NumberExpression<?>> numberExpressions,
            @NonNull Pageable pageable
    ){
        if(pageable.getSort().isEmpty()) return new ArrayList<>();
        return pageable.getSort().stream().map(o -> {
            String sortBy = o.getProperty();
            if(sortBy.trim().isEmpty())
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Property for sort by is not empty.");
            Order direction = o.isAscending() ? Order.ASC : Order.DESC;
            if(numberExpressions.containsKey(sortBy)){
                NumberExpression<?> numberExpression = numberExpressions.get(sortBy);
                return new OrderSpecifier<>(direction, numberExpression);
            }
            else{
                try{
                    PathBuilder<?> path = new PathBuilder<>(root.getType(), root.getMetadata());
                    String[] sortBys = sortBy.split("\\.");
                    for(String by : sortBys){
                        path = path.get(by);
                    }
                    return new OrderSpecifier<>(direction, (Expression<? extends Comparable>) path);
                }
                catch (Exception e){
                    throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid sort by: " + sortBy);
                }
            }
        }).toList();
    }

    public static class QueryDSLExpressionVisitor implements RSQLVisitor<Predicate, Void>{

        private final Function<String, NumberExpression<?>> resolver;

        public QueryDSLExpressionVisitor(Function<String, NumberExpression<?>> resolver) {
            this.resolver = resolver;
        }

        @Override
        public Predicate visit(AndNode node, Void param) {
            return node.getChildren().stream()
                    .map(child -> child.accept(this, param))
                    .reduce(ExpressionUtils::and)
                    .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "Syntax AND must be at ( ; )"));
        }

        @Override
        public Predicate visit(OrNode node, Void param) {
            return node.getChildren().stream()
                    .map(child -> child.accept(this, param))
                    .reduce(ExpressionUtils::or)
                    .orElseThrow(() -> new ApplicationException(HttpStatus.BAD_REQUEST, "Syntax OR must be at ( , )"));
        }

        @Override
        public Predicate visit(ComparisonNode node, Void param) {
            String selector = node.getSelector();
            String operator = node.getOperator().getSymbol();
            List<String> args = node.getArguments();
            NumberExpression<?> expr = resolver.apply(selector);
            if (expr == null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Unknown property: " + selector);
            }
            return buildPredicate(expr, operator, args);
        }

        private Predicate buildPredicate(NumberExpression<?> expr, String op, List<String> args) {
            Double value = Double.valueOf(args.get(0));
            return switch (op) {
                case "==" -> expr.eq(ConstantImpl.create(value));
                case "!=" -> expr.ne(ConstantImpl.create(value));
                case ">", "=gt="  -> expr.gt(value);
                case ">=", "=ge=" -> expr.goe(value);
                case "<", "=lt="  -> expr.lt(value);
                case "<=", "=le=" -> expr.loe(value);
                default   -> throw new ApplicationException(HttpStatus.BAD_REQUEST, "Unsupported operator: " + op);
            };
        }
    }
}
