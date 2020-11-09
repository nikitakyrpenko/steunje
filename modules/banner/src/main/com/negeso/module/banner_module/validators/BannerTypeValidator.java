/*
 * @(#)Id: BannerTypeValidator.java, 19.12.2007 19:50:03, Dmitry Fedotov
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
import org.springframework.validation.Validator;

import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.service.BannerService;
import com.negeso.module.banner_module.service.BannerTypeService;

/**
 * 
 * @TODO
 * 
 * @version Revision:
 * @author Dmitry Fedotov
 * 
 */
public class BannerTypeValidator implements Validator {

	Logger logger = Logger.getLogger(BannerTypeValidator.class);

	BannerTypeService bannerTypeService;
	BannerService bannerService;

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return clazz.equals(BannerType.class);
	}

	public void setBannerTypeService(BannerTypeService bannerTypeService) {
		this.bannerTypeService = bannerTypeService;
	}

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}

	public void validate(Object obj, Errors errors) {
		logger.debug("+");

		BannerType bannerType = (BannerType) obj;
		if (bannerType == null) {
			logger.error("- validation error: bannerType is null");
			return;
		}

		try {
			
			if (bannerType.getTitle() == null
					|| bannerType.getTitle().trim().length() == 0) {
				logger.debug("bannertype title is empty");
				errors.rejectValue("title", "BM.TYPE_TITLE_EMPTY");
			}

			if (bannerType.getWidth() < 1) {
				logger.debug("bannertype width is not correct");
				errors.rejectValue("width", "BM.TYPE_WIDTH_WRONG");
			}
			if (bannerType.getWidth() > 2000) {
				logger.debug("bannertype width is too big");
				errors.rejectValue("width", "BM.TYPE_WIDTH_BIG");
			}

			if (bannerType.getHeight() < 1) {
				logger.debug("bannertype height is not correct");
				errors.rejectValue("height", "BM.TYPE_HEIGHT_WRONG");
			}
			if (bannerType.getHeight() > 2000) {
				logger.debug("bannertype height is too big");
				errors.rejectValue("height", "BM.TYPE_HEIGHT_BIG");
			}

			/*
			 * banner type is already in system
			 */
			if (bannerType.getId() > 0) {
				BannerType bType = bannerTypeService.findById(bannerType
						.getId());
				if (bType == null) {
					throw new Exception("cannot find existing banner type in db");
				}
				
				bannerTypeService.evictType(bType);
				
				if (bType.getWidth() != bannerType.getWidth()
						&& bannerService.findBannersByTypeId(bannerType.getId()).size() > 0) {
					logger.debug("Width cannot be changed");
					errors.rejectValue("width", "BM.TYPE_WIDTH_NOCHANGE");
				}
				if (bType.getHeight() != bannerType.getHeight()
						&& bannerService.findBannersByTypeId(bannerType.getId()).size() > 0) {
					logger.debug("Height cannot be changed");
					errors.rejectValue("height", "BM.TYPE.HEIGHT_NOCHANGE");
				}

			}
			if (bannerType.getTitle() != null
					&& bannerType.getTitle().trim().length() > 0) {
				BannerType bT = bannerTypeService.findByTitle(bannerType.getTitle());
				if (bT != null && !bT.getId().equals(bannerType.getId())) {
					logger.debug("Dublicate title");
					errors.rejectValue("title", "BM.TYPE_TITLE_DUPLICATE");
				}
			}

		} catch (Exception e) {
			logger.error("- validation error: " + e.getMessage());
			return;
		}

		logger.debug("-");
	}
}
