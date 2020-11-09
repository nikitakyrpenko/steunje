/*
* @(#)$Id: StoreLocatorService.java,v 1.4, 2007-01-09 19:00:04Z, Anatoliy Pererva$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.store_locator;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.module.store_locator.domain.Region;


/**
 *
 * TODO
 * 
 * @version                $Revision: 5$
 * @author                 Svetlana Bondar
 * 
 */
public class StoreLocatorService extends HibernateDaoSupport {
	
	    private static Logger logger = Logger.getLogger(StoreLocatorService.class);

	    public void add(Object obj) {
	        logger.debug("Add :" + obj);
	        this.getHibernateTemplate().save(obj);
	    }

	    public void update(Object obj) {
			logger.debug("Update:" + obj);
			this.getHibernateTemplate().update(obj);  
	    }

	    public void delete(Object obj) {
	        logger.debug("+- Delete:" + obj);        
	        this.getHibernateTemplate().delete(obj);
	    }

	    public Object get(Class clazz, Long id) {
	        logger.debug("+- get: id = " + id + "Class = " + clazz);                
	        return this.getHibernateTemplate().load( clazz, id );
	    }
	    
/*	    public Company getCompany(Long id) {
	        logger.debug("+- get Company: id " + id);                
	        return (Company) this.getHibernateTemplate().load( Company.class, id );
	    }
	    public Shop getShop(Long id) {
	        logger.debug("+- get Region: id " + id);                
	        return (Shop) this.getHibernateTemplate().load( Shop.class, id );
	    }
	    
*/	    public List<Region> getRegions() {
	    	logger.debug("+-");    	
	    	return this.getHibernateTemplate().find( "from Region order by title" );    	
	    }

}
