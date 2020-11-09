package com.negeso.framework.dao;

public abstract class GenericService<T extends Entity> {
	public abstract T findById(Long id);
	public abstract void createOrUpdate(T entity);
}
