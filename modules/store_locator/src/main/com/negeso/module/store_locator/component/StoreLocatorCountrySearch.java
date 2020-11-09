/*
 * @(#)$Id: StoreLocatorCountrySearch.java,v 1.2, 2007-03-19 09:44:45Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.store_locator.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PreparedPageComponent;

/**
 * 
 * Country searching store locator component
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 3$
 *
 */
public class StoreLocatorCountrySearch extends PreparedPageComponent {
	private static Logger logger = Logger.getLogger( StoreLocatorCountrySearch.class );


	@Override
	public String getName() {
		return "StoreLocatorComponent";
	}

	@Override
	public void buildXml() {
		
		String region = this.getRequestContext().getNonblankParameter("region");
		String country = this.getRequestContext().getNonblankParameter("country");
		Xbuilder.setAttr(this.getModel(), "region", region);
		Xbuilder.setAttr(this.getModel(), "country", country);
		
		renderSelection();
		
		if (country != null) {
			this.renderSearchResults(country, region);
		}
	}

	
	private void renderSearchResults(String country, String region) {
		try{
			PreparedStatement stmt = this.getConnection().prepareStatement(
				" SELECT sl_company.title, sl_company.image, sl_shop.link  FROM sl_region " +
				" LEFT JOIN sl_company ON sl_region.id=sl_company.region_id " + 
				" LEFT JOIN sl_shop ON sl_company.id = sl_shop.company_id " + 
				" WHERE sl_shop.country ilike '" + country + "%' and sl_region.title ilike '" + region + "%' " +
				" ORDER by sl_region.title, sl_shop.country "
			);
			ResultSet rs = stmt.executeQuery();
			
			Element reg = Xbuilder.addEl(this.getModel(), "SearchResult", null);
			while (rs.next()) {
				Element cn = Xbuilder.addEl(reg, "Company", null);
				Xbuilder.setAttr(cn, "title", rs.getString("title"));
				Xbuilder.setAttr(cn, "image", rs.getString("image"));
				Xbuilder.setAttr(cn, "link", rs.getString("link"));
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}
	}

	public void renderSelection() {
		try{
			PreparedStatement stmt = this.getConnection().prepareStatement(
				" SELECT sl_region.id, sl_region.title, sl_shop.country FROM sl_region " +
				" LEFT JOIN sl_company ON sl_region.id=sl_company.region_id " + 
				" LEFT JOIN sl_shop ON sl_company.id = sl_shop.company_id " + 
				" WHERE sl_shop.country is not NULL ORDER by sl_region.title, sl_shop.country " 
			);
			ResultSet rs = stmt.executeQuery();
			
			Long regionId = null;
			Long tmpId = null;
			Element region = null;
			while (rs.next()) {
				tmpId = rs.getLong("id");
				if ( !tmpId.equals(regionId) ) {
					regionId = tmpId;
					region = Xbuilder.addEl(this.getModel(), "Region", null);
					Xbuilder.setAttr(region, "id", regionId);
					Xbuilder.setAttr(region, "title", rs.getString("title"));
				}
				if (region != null) {
					Element country = Xbuilder.addEl(region, "Country", null);
					Xbuilder.setAttr(country, "title", rs.getString("country"));
				}
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}
	}	
	
	
	
	
}
