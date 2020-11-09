package com.negeso.framework.dao.impl;

import org.hibernate.Session;

import java.util.List;

public interface HibernateDao<T, PK> {
	T findOne(final PK id);
	List<T> findAll();
	void save(final T entity);
	PK saveNow(final T entity);
	void saveOrUpdate(final T entity);
	T update(final T entity);
	void delete(final T entity);
	void deleteByPrimaryKey(final PK id);
	Session createSession();
}
