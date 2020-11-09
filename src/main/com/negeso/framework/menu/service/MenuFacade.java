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
package com.negeso.framework.menu.service;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.menu.MenuComponent;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class MenuFacade {
	
	private static IMenuService menuService = MenuService.getSpringInstance();
	
	public static Document getMenuXml(
			Language language, 
			String curPage, 
			User user, 
			boolean truncate, 
			Long curItemId) {
		try {
			Document doc = menuService.getMenuXml(language, user);
			Long selectedMenuId = menuService.determinateSelectedMenuId(curPage, curItemId);
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer tx   = tfactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			DOMResult result = new DOMResult();
			tx.transform(source,result);
			doc = (Document)result.getNode();
			Xbuilder.setAttr((Element)doc.getDocumentElement().getFirstChild(), MenuComponent.ATTR_SELECTED_MENU_ID, selectedMenuId);
			Xbuilder.setAttr((Element)doc.getDocumentElement().getFirstChild(), MenuComponent.ATTR_TRUNCATE, truncate);
			return doc;
		} catch (Exception e) {
			throw new CriticalException(e);
		}
	}
}

