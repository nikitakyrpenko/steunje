package com.negeso.framework.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.Serializable;
import java.util.List;

public class AbstractHibernateDao<T extends Serializable, PK extends Serializable>{
	private Class<T> clazz;

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;
	
	public AbstractHibernateDao(Class<T> type){
			this.clazz = type;
	}
	public AbstractHibernateDao(){
		
	}

	public void setClazz( final Class<T> clazzToSet ){
		clazz = clazzToSet;
	}

	@SuppressWarnings("unchecked")
	public T findOne(final PK id){
		return (T) getCurrentSession().get(clazz, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAll(){
		return getCurrentSession()
				.createQuery("from " + clazz.getName()).list();
	}

	public void save(final T entity){
		getCurrentSession().persist(entity);
	}

	@SuppressWarnings("unchecked")
	public T update(final T entity){
		return (T) getCurrentSession().merge(entity);
	}

	public void delete(final T entity){
		getCurrentSession().delete(entity);
	}
	
	public void deleteByPrimaryKey(final PK id){
		final T entity = findOne(id);
		delete(entity);
	}

	public void saveOrUpdate(final T entity){
		getCurrentSession().saveOrUpdate(entity);
	}

	protected final Session getCurrentSession(){
		return sessionFactory.getCurrentSession();
	}

	protected Criteria createCriteria(){
		return this.getCurrentSession().createCriteria(clazz);
	}

	public Session createSession(){
		return sessionFactory.openSession();
	}

	public PK saveNow(final T entity){
		return (PK) getCurrentSession().save(entity);
	}
}
