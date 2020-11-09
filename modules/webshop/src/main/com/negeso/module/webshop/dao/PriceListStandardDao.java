package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.PriceList;
import com.negeso.module.webshop.entity.PriceListStandard;
import com.negeso.module.webshop.entity.ProductGroup;

import java.util.List;

public interface PriceListStandardDao extends HibernateDao<PriceListStandard, String>{
	@Deprecated
	PriceListStandard findOneByPriceListAndProductGroup(PriceList priceList, ProductGroup productGroup);
	List<PriceListStandard> findAllByPriceListAndProductGroup(PriceList priceList, ProductGroup productGroup);
}
