/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.framework.dao.Entity;
import com.negeso.framework.dao.GenericDao;
import com.negeso.framework.dao.finder.FinderArgumentTypeFactory;
import com.negeso.framework.dao.finder.FinderExecutor;
import com.negeso.framework.dao.finder.FinderNamingStrategy;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class GenericDaoHibernateImpl<T extends Entity, PK extends Serializable>
extends HibernateDaoSupport implements GenericDao<T, PK>, FinderExecutor<T> {

	private static final Logger logger = Logger.getLogger(GenericDaoHibernateImpl.class);
	
	private FinderNamingStrategy namingStrategy;
	private FinderArgumentTypeFactory argumentTypeFactory;

	private Class<T> type;

	public GenericDaoHibernateImpl(Class<T> type) {
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	public PK create(T newInstance) {
		return (PK) getHibernateTemplate().save(newInstance);
	}
	
	public void updateAll(List<T> listInstances) {
		getHibernateTemplate().saveOrUpdateAll(listInstances);
	}
	
	public void deleteAll(List<T> listInstances) {
		getHibernateTemplate().deleteAll(listInstances);
	}

	public void delete(T persistentObject) {
		getHibernateTemplate().delete(persistentObject);
	}

	@SuppressWarnings("unchecked")
	public T read(PK id) {
		return (T) getHibernateTemplate().get(type, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> readByCriteria(Criterion... criterion) {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(type);
		for (Criterion c : criterion) {
			criteria.add(c);
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> readByCriteriaSorted(Order order, Criterion... criterion) {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(type);
		for (Criterion c : criterion) {
			criteria.add(c);
		}
		criteria.addOrder(order);
		return criteria.list();
	}
	
	public void update(T transientObject) {
		getHibernateTemplate().update(transientObject);
	}
	
	public void createOrUpdate(T instance) {
		if (instance.getId() == null || instance.getId() <= 0) {
			create(instance);
		} else {
			update(instance);
//			getHibernateTemplate().merge(instance);
		}
	}

	public void saveOrUpdate(T instance){
		getHibernateTemplate().saveOrUpdate(instance);
	}
	
	public List<T> readAll() {
		return readByCriteria();
	}
	
	public FinderNamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	public void setNamingStrategy(FinderNamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	public FinderArgumentTypeFactory getArgumentTypeFactory() {
		return argumentTypeFactory;
	}

	public void setArgumentTypeFactory(FinderArgumentTypeFactory argumentTypeFactory) {
		this.argumentTypeFactory = argumentTypeFactory;
	}
	
	private Query prepareQuery(Method method, Object[] queryArgs, int limit, int offset) {
		logger.debug("+");
		

		
		final String queryName = getNamingStrategy().queryNameFromMethod(type, method);
		try {
			final Query namedQuery = getHibernateTemplate().getSessionFactory().getCurrentSession().getNamedQuery(queryName);
			
			String[] namedParameters = namedQuery.getNamedParameters();
	
			logger.debug("queryName=" + queryName);
			logger.debug("namedQuery=" + namedQuery);
			logger.debug("queryArgs.length=" + ((queryArgs == null) ? null : queryArgs.length));
			logger.debug("namedParameters.length=" + ((namedParameters == null) ? null : namedParameters.length));
			
			if (namedParameters.length == 0) {
				setPositionalParams(queryArgs, namedQuery);
			} else {
				setNamedParams(namedParameters, queryArgs, namedQuery);
			}
			
			if (limit > 0) {
				logger.debug("limit=" + limit);
				namedQuery.setMaxResults(limit);
			}
			
			if (offset > 0) {
				logger.debug("offset=" + offset);
				namedQuery.setFirstResult(offset);
			}
			
			logger.debug("-");
			return namedQuery;
		} catch (HibernateException e) {
			logger.error("unexpected hibernate error", e);
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}
	}

	 private void setPositionalParams(Object[] queryArgs, Query namedQuery) {
		logger.debug("+");
		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				Object arg = queryArgs[i];
				logger.debug("arg=" + arg);
				Type argType = getArgumentTypeFactory().getArgumentType(arg);
				logger.debug("argType=" + argType);
				if (argType != null) {
					namedQuery.setParameter(i, arg, argType);
				} else {
					namedQuery.setParameter(i, arg);
				}
			}
		}
		logger.debug("-");
	}
	
	private void setNamedParams(String[] namedParameters, Object[] queryArgs, Query namedQuery) {
		logger.debug("+");
		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				Object arg = queryArgs[i];
				logger.debug("arg=" + arg);
				Type argType = getArgumentTypeFactory().getArgumentType(arg);
				logger.debug("argType=" + argType);
				if (argType != null) {
					namedQuery.setParameter(namedParameters[i], arg, argType);
				} else {
					if (arg instanceof Collection) {
						namedQuery.setParameterList(namedParameters[i], (Collection) arg);
					} else {
						namedQuery.setParameter(namedParameters[i], arg);
					}
				}
			}
		}
		logger.debug("-");
	}

	@SuppressWarnings("unchecked")
	public int count(Method method, Object[] queryArgs) {
		Query namedQuery = prepareQuery(method, queryArgs, 0, 0);
		Number result = (Number) namedQuery.uniqueResult();
        if (result == null)
			return 0;
		return result.intValue();
	}

	public T find(Method method, Object[] queryArgs) {
		Query namedQuery = prepareQuery(method, queryArgs, 0, 0);
		return (T) namedQuery.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> list(Method method, final Object[] args, int limit, int offset) {
		return prepareQuery(method, args, limit, offset).list();
	}

	@SuppressWarnings("unchecked")
	public List list(Method method, Object[] queryArgs) {
		Query namedQuery = prepareQuery(method, queryArgs, 0, 0);
		return namedQuery.list();
	}
	
	public void evict(T persistentObject) {
		getHibernateTemplate().evict(persistentObject);
	}
	
	public void flush() {
		getHibernateTemplate().flush();
	}

	@Override
	public Criteria getCriteria() {
		return getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(type);
	}
	
}
