/*
* @(#)$Id: FieldConfigurationCache.java,v 1.3, 2007-02-02 11:43:04Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.wcms.field_configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.negeso.framework.domain.DBHelper;

/**
 *
 * TODO
 * 
 * @version                $Revision: 4$
 * @author                 sbondar
 * 
 */
public class FieldConfigurationCache {
	
	private static Logger logger = Logger.getLogger(FieldConfigurationCache.class);
	
	private static FieldConfigurationCache instance;
	
	Map<Long, List<FieldConfiguration>> fieldsConfiguration = new HashMap<Long, List<FieldConfiguration>>();
	
	private FieldConfigurationCache() {
		// do nothing
	}

	/**
	 * Returns an instance of singleton.
	 * @return the FieldConfigurationCache instance.
	 */
	public static synchronized FieldConfigurationCache getInstance() {
		if (instance == null) {
			instance = new FieldConfigurationCache();
		}
		return instance;
	}

	
	public List<FieldConfiguration> getFieldsConfiguration(Long fieldSetId, HibernateTemplate hiberTemplate) {
		logger.debug("+");
		if ( !fieldsConfiguration.containsKey( fieldSetId ) ) {			
			if (hiberTemplate == null) {
				hiberTemplate = DBHelper.getHibernateTemplate();
			} 
			List<FieldConfiguration> fieldConfList = (List<FieldConfiguration>) hiberTemplate.find(
						"from FieldConfiguration fc " +
						//TODO SB:!!!
//					" where fc.id in (select field_id from core_field2sets where field_set_id = " + fieldSetId +
//					") 
						"	order by fc.orderNumber "
				);
			fieldsConfiguration.put(fieldSetId, fieldConfList);
		}
		logger.debug("-");
		return fieldsConfiguration.get(fieldSetId);
		
	}
}
