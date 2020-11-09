/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.social.service;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.social.SocialNetworkException;
import com.negeso.module.social.bo.SocialNetwork;
import com.negeso.module.social.dao.SocialNetworkDao;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SocialNetworkService {
	
	private SocialNetworkDao socialNetworkDao = null;
	
	private static SocialNetworkService instance = null;
	
	public static SocialNetworkService getInstance() {
		if (instance == null) {
			instance = (SocialNetworkService)DispatchersContainer.getInstance().getBean("core", "socialNetworkService");
		}
		return instance;
	}
	
	private Map<String, SocialNetworkPublisher> publishers = null;
	
	public SocialNetwork findByTitle(String title) {
		List<SocialNetwork> list = socialNetworkDao.readByCriteria(Restrictions.eq("title", title));
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public void update(SocialNetwork socialNetwork) {
		socialNetworkDao.createOrUpdate(socialNetwork);
	}
	
	public List<SocialNetwork> list() {
		return socialNetworkDao.readByCriteria();
	}
	
	public Element getSocialNetworksElement(Element parent, Iterable<SocialNetwork> iterable) {
		Element el = Xbuilder.createEl(parent, "socialNetworks", null);
		for (SocialNetwork socialNetwork : list()) {
			Element socialNetworkEl = Xbuilder.addBeanJAXB(el, socialNetwork);
			for (SocialNetwork socialNetwork2 : iterable) {
				if (socialNetwork.getId().equals(socialNetwork2.getId())) {
					Xbuilder.setAttr(socialNetworkEl, "checked", true);
				}
			}
		}
		return el;
	}

	public SocialNetworkDao getSocialNetworkDao() {
		return socialNetworkDao;
	}

	public void setSocialNetworkDao(SocialNetworkDao socialNetworkDao) {
		this.socialNetworkDao = socialNetworkDao;
	}

	public void publish(String title, String text, String url, String imageUrl, SocialNetwork socialNetwork) throws SocialNetworkException {
		publishers.get(socialNetwork.getTitle()).publish(title, text, url, imageUrl, socialNetwork);
	}

	public Map<String, SocialNetworkPublisher> getPublishers() {
		return publishers;
	}

	public void setPublishers(Map<String, SocialNetworkPublisher> publishers) {
		this.publishers = publishers;
	}
}

