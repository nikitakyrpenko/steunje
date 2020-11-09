/*
 * @(#)Id: BannerXMLBuilder.java, 20.12.2007 16:18:53, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.component;

import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.bo.BannerType;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerXMLBuilder {
	
	public static void buildXML(Element element, Banner banner, BannerType bannerType){
		Element bannerElement = Xbuilder.addEl(element, "banner", null);
		bannerElement.setAttribute("id", banner.getId().toString());
		bannerElement.setAttribute("title", banner.getTitle());
		bannerElement.setAttribute("url", banner.getUrl());
		bannerElement.setAttribute("imageType", String.valueOf(banner.getImageType()));
		bannerElement.setAttribute("imageUrl", banner.getImageUrl());
		bannerElement.setAttribute("inNewWindow", String.valueOf(banner.getInNewWindow()));
		bannerElement.setAttribute("height", String.valueOf(bannerType.getHeight()));
		bannerElement.setAttribute("width", String.valueOf(bannerType.getWidth()));
		bannerElement.setAttribute("type", bannerType.getId().toString());
	}
}
