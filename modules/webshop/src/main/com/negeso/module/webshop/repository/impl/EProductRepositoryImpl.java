package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.entity.modern.EProduct;
import com.negeso.module.webshop.exception.RetrieveException;
import com.negeso.module.webshop.repository.EProductRepository;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EProductRepositoryImpl implements EProductRepository {
    private static final String QUERY_FIND_PRODUCT_BY_PRODUCT_CODE = "from EProduct WHERE product_number = :productNumber";

    private static final String QUERY_FIND_ALL_PRODUCTS = "from EProduct";
    @Autowired
    @Qualifier("apiSessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public Optional<EProduct> findByProductNumberIfExists(String productCode) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        try{
            transaction.begin();
            Query query = session.createQuery(QUERY_FIND_PRODUCT_BY_PRODUCT_CODE);
            query.setParameter("productNumber", productCode);

            return Optional.ofNullable((EProduct)query.uniqueResult());
        }catch (Exception e){
            transaction.rollback();
            throw new RetrieveException("Cannot find EProduct by [productName]="+productCode);
        }

    }

    @Override
    public List<EProduct> findAll() {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        try{
            transaction.begin();
            Query query = session.createQuery(QUERY_FIND_ALL_PRODUCTS);

            return query.list();

        }catch (Exception e){
            transaction.rollback();
            throw new RetrieveException("Cannot find EProduct");
        }
    }
}
