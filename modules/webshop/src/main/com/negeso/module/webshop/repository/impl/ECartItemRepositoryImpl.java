package com.negeso.module.webshop.repository.impl;

import com.negeso.module.webshop.entity.modern.ECartItem;
import com.negeso.module.webshop.exception.DeleteException;
import com.negeso.module.webshop.exception.RetrieveException;
import com.negeso.module.webshop.repository.ECartItemRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;

@Repository
public class ECartItemRepositoryImpl implements ECartItemRepository {
    private static final String QUERY_FIND_ALL_CART_ITEMS_BY_CART_OWNER_ID = "FROM ECartItem WHERE cartOwnerId = :cartOwnerId";
    private static final String QUERY_DELETE_ALL_BY_IDS = "DELETE ECartItem ci WHERE id IN (:ids)";

    @Autowired @Qualifier("apiSessionFactory") private SessionFactory sessionFactory;

    @Override
    public Integer save(ECartItem cartItem) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            transaction.begin();

            session.save(cartItem);

            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
            throw new PersistenceException("Cannot persist ECartItem; "+ e.getCause());
        }finally {
            session.close();
        }
        return cartItem.getId();
    }

    @Override
    public List<ECartItem> findAllByCartOwnerId(String cartOwnerId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Query query = session.createQuery(QUERY_FIND_ALL_CART_ITEMS_BY_CART_OWNER_ID);
            query.setParameter("cartOwnerId", cartOwnerId);

            return query.list();
        }catch (Exception e){
            transaction.rollback();
            throw new RetrieveException("Cannot retrieve ECartItem by cartOwnerId: "+ e.getCause());
        }finally {
            session.close();
        }
    }

    @Override
    public void deleteAllByIds(Collection<Integer> ids) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try{
            transaction.begin();

            Query query = session.createQuery(QUERY_DELETE_ALL_BY_IDS);
            query.setParameterList("ids", ids);

            query.executeUpdate();

            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
            throw new DeleteException("Cannot delete ECartItem by ids "+e.getCause());
        }finally {
            session.close();
        }
    }
}
