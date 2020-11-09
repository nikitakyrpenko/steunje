package com.negeso.module.google_shop.service;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.google_shop.bo.GoogleMerchant;
import com.negeso.module.google_shop.dao.MerchantDao;

public class MerchantService {
	
	private MerchantDao merchantDao;
	
	public List<GoogleMerchant> list() {
		return merchantDao.readAll();
	}

	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

	public GoogleMerchant findById(Long id) {
		return merchantDao.read(id);
	}
	
	@Transactional
	public void createOrUpdate(GoogleMerchant merchant) {
		merchantDao.createOrUpdate(merchant);
	}
	
	@Transactional
	public void delete(GoogleMerchant merchant) {
		merchantDao.delete(merchant);
	}

	public boolean isUnique(GoogleMerchant merchant) {
		List<GoogleMerchant> list = merchantDao.readByCriteria(Restrictions.eq("hostName", merchant.getHostName()), 
				Restrictions.eq("langCode", merchant.getLangCode()), 
				Restrictions.eq("countryCode", merchant.getCountryCode()));
		return list.isEmpty();
	}

	public List<GoogleMerchant> listEnabled() {
		return merchantDao.readByCriteria(Restrictions.eq("enabled", true)); 
	}
}
