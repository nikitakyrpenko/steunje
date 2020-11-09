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
package com.negeso.framework.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface GenericDao<T extends Entity, PK extends Serializable> {
	
	public PK create(T newInstance);

	public T read(PK id);
	
	public List<T> readAll();
	
	public List<T> readByCriteria(Criterion... criterion);
	
	public List<T> readByCriteriaSorted(Order order, Criterion... criterion);

	public void update(T transientObject);

	public void delete(T persistentObject);
	
	public void evict(T persistentObject);
	
	public void createOrUpdate(T newInstance);
	public void saveOrUpdate(T newInstance);

	public void updateAll(List<T> listInstances);

	public void deleteAll(List<T> listInstances);
	
	public void flush();
	
	public Criteria getCriteria();

}
