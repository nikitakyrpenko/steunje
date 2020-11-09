/*
 * @(#)Id: CategoryValidator.java, 14.12.2007 18:30:52, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.validators;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.negeso.module.banner_module.bo.BannerCategory;
import com.negeso.module.banner_module.service.BannerCategoryService;

/**
 * 
 * @TODO
 * 
 * @version Revision:
 * @author Dmitry Fedotov
 * 
 */
public class BannerCategoryValidator implements Validator {

	Logger logger = Logger.getLogger(BannerCategoryValidator.class);

	BannerCategoryService bannerCategoryService;

	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(BannerCategory.class);
	}

	public void validate(Object object, Errors errors) {
		logger.debug("+");
		try {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "BM.CATEGORY_TITLE_EMPTY");
			BannerCategory category = (BannerCategory) object;
			if (category == null) {
				logger.error("- category is null");
				return;
			}
			if (category.getId() != null) {
				BannerCategory c = bannerCategoryService.findCategoryById(category.getId());
				if (c != null && c.getIsLeaf() != category.getIsLeaf()) {
					if (c.getIsLeaf()
							&& bannerCategoryService.isContainBanners(category.getId())) {
						errors.rejectValue("isLeaf", "BM.CATEGORY_IS_LEAF_NO_DELETE");
					} else if (!c.getIsLeaf()
							&& bannerCategoryService.isContainCategories(category.getId())) {
						errors.rejectValue("isLeaf", "BM.CATEGORY_IS_LEAF_NO_DELETE");
					}
				}
				if (category.getTitle() != null
						&& category.getTitle().trim().length() > 0) {
					BannerCategory c1 = bannerCategoryService.findCategoryByTitleParentId(category.getTitle(),
									category.getParentId());
					logger.debug("c1=" + c1);
					logger.debug("category=" + category);
					if (c1 != null && !c1.getId().equals(category.getId())) {
						errors.rejectValue("title", "BM.CATEGORY_TITLE_DUPLICATE");
					}
				}
			}
		} catch (Throwable e) {
			logger.error("- category validation error:" + e.getMessage());
			return;
		}
		logger.debug("-");
	}

	public void setBannerCategoryService(
			BannerCategoryService bannerCategoryService) {
		this.bannerCategoryService = bannerCategoryService;
	}
}
