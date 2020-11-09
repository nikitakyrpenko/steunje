package com.negeso.module.thr.dao;

import java.util.List;

import com.negeso.framework.dao.GenericDao;
import com.negeso.module.thr.bo.ThrOrder;

public interface ThrOrderDao extends GenericDao<ThrOrder, Long> {
	List<String> listLogins();
}
