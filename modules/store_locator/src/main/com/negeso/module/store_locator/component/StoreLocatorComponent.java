/*
 * @(#)$Id: StoreLocatorComponent.java,v 1.10, 2006-06-22 21:13:04Z, Olexiy Strashko$
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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PreparedPageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 11$
 *
 */
public class StoreLocatorComponent extends PreparedPageComponent {
	private static Logger logger = Logger.getLogger( StoreLocatorComponent.class );

	@Override
	public String getName() {
		return "StoreLocatorComponent";
	}

	@Override
	public void buildXml() {
		
		try{
			PreparedStatement stmt = this.getConnection().prepareStatement(
				" SELECT sl_region.id, sl_region.title as region, sl_company.title as company, " +
				" sl_company.link, sl_company.image, sl_company.id as company_id " +
				" FROM sl_region " +
				" LEFT JOIN sl_company ON sl_region.id=sl_company.region_id " +
				" WHERE sl_company.id is not NULL " + 
				" ORDER BY sl_region.title, sl_company.title " 
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
					Xbuilder.setAttr(region, "title", rs.getString("region"));
				}
				if (region != null) {
					Element country = Xbuilder.addEl(region, "Company", null);
					Xbuilder.setAttr(country, "title", rs.getString("company"));
					Xbuilder.setAttr(country, "image", rs.getString("image"));
					Xbuilder.setAttr(country, "link", rs.getString("link"));
					Long companyId = rs.getLong("company_id");
					PreparedStatement stmt2 = this.getConnection().prepareStatement(
						" SELECT country, link " +
						" FROM sl_shop " +
						" WHERE company_id = ?" + 
						" ORDER BY country " 
					);
					stmt2.setLong(1, companyId);
					ResultSet rs2 = stmt2.executeQuery();
					while (rs2.next()) {
						Element shop= Xbuilder.addEl(country, "Shop", null);
						Xbuilder.setAttr(shop, "country", rs2.getString("country"));
						Xbuilder.setAttr(shop, "link", rs2.getString("link"));
					}
				}
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}
	}
}
