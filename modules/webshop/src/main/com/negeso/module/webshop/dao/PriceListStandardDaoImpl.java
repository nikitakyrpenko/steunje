package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.PriceList;
import com.negeso.module.webshop.entity.PriceListStandard;
import com.negeso.module.webshop.entity.ProductGroup;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceListStandardDaoImpl extends AbstractHibernateDao<PriceListStandard, String> implements PriceListStandardDao {

	public PriceListStandardDaoImpl(){
		super(PriceListStandard.class);
	}

	@Override
	public PriceListStandard findOneByPriceListAndProductGroup(PriceList priceList, ProductGroup productGroup) {
		return (PriceListStandard) super.createCriteria()
				.add(Restrictions.eq("priceGroup", priceList))
				.add(Restrictions.eq("productGroup", productGroup))
				.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PriceListStandard> findAllByPriceListAndProductGroup(PriceList priceList, ProductGroup productGroup) {
		return (List<PriceListStandard>) super.createCriteria()
				.add(Restrictions.eq("priceGroup", priceList))
				.add(Restrictions.eq("productGroup", productGroup))
				.addOrder(Order.desc("sinceCount"))
				.list();
	}

}
