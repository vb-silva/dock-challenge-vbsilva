package com.dock.api.repositories;

import com.dock.api.models.TransactionModel;
import com.dock.api.models.TransactionPage;
import com.dock.api.models.TransactionSearchCriteria;
import jdk.jfr.Registered;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public TransactionCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<TransactionModel> getAccountExtractWithFilter(TransactionPage transactionPage,
                                                              TransactionSearchCriteria transactionSearchCriteria) {
        CriteriaQuery<TransactionModel> criteriaQuery = criteriaBuilder.createQuery(TransactionModel.class);
        Root<TransactionModel> transactionModelRoot = criteriaQuery.from(TransactionModel.class);
        Predicate predicate = getPredicate(transactionSearchCriteria, transactionModelRoot);
        criteriaQuery.where(predicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(transactionModelRoot.get(transactionPage.getSortBy())));

        TypedQuery<TransactionModel> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(transactionPage.getPageNumber() * transactionPage.getPageSize());
        typedQuery.setMaxResults(transactionPage.getPageSize());

        Pageable pageable = getPageable(transactionPage);

        long transactionCount = getTransactionCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, transactionCount);
    }
    private Predicate getPredicate(TransactionSearchCriteria transactionSearchCriteria,
                                   Root<TransactionModel> transactionModelRoot) {

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(
                criteriaBuilder.equal(
                        transactionModelRoot.get("accountId").as(Long.class),
                        transactionSearchCriteria.getAccountId())
        );

        if(Objects.nonNull(transactionSearchCriteria.getStartDate())){
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                            transactionModelRoot.get("transactionTimeUTC").as(LocalDateTime.class),
                            transactionSearchCriteria.getLocalDateTimeStartDate())
            );
        }

        if(Objects.nonNull(transactionSearchCriteria.getEndDate())){
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(
                            transactionModelRoot.get("transactionTimeUTC").as(LocalDateTime.class),
                            transactionSearchCriteria.getLocalDateTimeEndDate())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Pageable getPageable(TransactionPage transactionPage) {
        Sort sort = Sort.by(transactionPage.getSortDirection(), transactionPage.getSortBy());
        return PageRequest.of(transactionPage.getPageNumber(),transactionPage.getPageSize(), sort);
    }

    private long getTransactionCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<TransactionModel> countRoot = countQuery.from(TransactionModel.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}