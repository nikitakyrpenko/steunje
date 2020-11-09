/*
 * @(#)$Id: StoreLocatorController.java,v 1.10, 2006-06-21 14:38:14Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.store_locator.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.HistoryStack;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.store_locator.StoreLocatorService;
import com.negeso.module.store_locator.domain.Company;
import com.negeso.module.store_locator.domain.Region;
import com.negeso.module.store_locator.domain.Shop;

/**
 * 
 * @TODO
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 11$
 *
 */
public class StoreLocatorController extends AbstractController {
	
    private static Logger logger = Logger.getLogger(StoreLocatorController.class);
    
    private StoreLocatorService storeLocatorService;
    
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse arg1) throws Exception {
        logger.debug("+");    
    
		String action = request.getParameter( "act" );
		if ( logger.isInfoEnabled() ){
			logger.info("act: " + action);
		}

		ModelAndView resultModel = null; 
		String id = request.getParameter("id");
		
		//region
		if ("region_details".equals(action)) {
			resultModel = new ModelAndView("sl_region_details", "region", storeLocatorService.get(Region.class, Long.valueOf(id)));
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regionDetails.link", 
	        		"/admin/store_locator?act=region_details&id=" + id));
			
		} else if ("remove_region".equals(action)) {						
			storeLocatorService.delete(storeLocatorService.get(Region.class, Long.valueOf(id)));
			resultModel = new ModelAndView("sl_regions", "regions", storeLocatorService.getRegions());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regions.link", 
	        		"/admin/store_locator?act=region_list"));
			
		} else if ("save_region".equals(action)) {			
			Region region = (Region) storeLocatorService.get(Region.class, Long.valueOf(id));			
			region.setTitle(request.getParameter("title"));
			storeLocatorService.update(region);
			resultModel = new ModelAndView("sl_regions", "regions", storeLocatorService.getRegions());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regions.link", 
	        		"/admin/store_locator?act=region_list"));

			
		} else if ("add_region".equals(action)) {			
			Region region = new Region();
			region.setTitle(request.getParameter("title"));
			storeLocatorService.add(region);
			resultModel = new ModelAndView("sl_regions", "regions", storeLocatorService.getRegions());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regions.link", 
	        		"/admin/store_locator?act=region_list"));

	        
		} else if ("region_list".equals(action)) {
			resultModel = new ModelAndView("sl_regions", "regions", storeLocatorService.getRegions());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regions.link", 
	        		"/admin/store_locator?act=region_list"));			
			
		//company	
	    } else  if ("company_details".equals(action)) {	    	
	    	if (id != null) {
	    		Company company =  (Company) storeLocatorService.get(Company.class, Long.valueOf(id));
	    		request.setAttribute("shops", company.getShops());
	    		resultModel = new ModelAndView("sl_company_details", "company", company);
		        RequestUtil.getHistoryStack(request).push(
		        		new Link("companyDetails.linkEdit", 
		        		"/admin/store_locator?act=company_details&id=" + id));

	    	} else {
	    		resultModel = new ModelAndView("sl_company_details");
		        RequestUtil.getHistoryStack(request).push(
		        		new Link("companyDetails.linkAdd", 
		        		"/admin/store_locator?act=company_details&regionId=" + request.getParameter("regionId")));
	    	}

		} else if ("remove_company".equals(action)) {						
	    	Company company =  (Company) storeLocatorService.get(Company.class, Long.valueOf(id));
	    	storeLocatorService.delete(company);

	    	Region region = (Region) storeLocatorService.get(Region.class, company.getRegionId());
	    	request.setAttribute("region", region);	    	
			resultModel = new ModelAndView("sl_companies", "companies", region.getCompanies());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companies.link", 
	        		"/admin/store_locator?act=company_list&id=" + company.getRegionId()));			
			
 
		} else if ("save_company".equals(action)) {			
	    	Company company =  (Company) storeLocatorService.get(Company.class, Long.valueOf(id));			
	    	company.setTitle(request.getParameter("title"));
	    	if (!"unavailable".equals(request.getParameter("image"))) {	    		
	    		company.setImage(request.getParameter("image"));
	    	}
	    	if ("on".equals(request.getParameter("clear_image"))) {
	    		//delete image
	    		company.setImage("");
	    	}
	    	company.setLink(request.getParameter("link"));
			storeLocatorService.update(company);

			Region region = (Region) storeLocatorService.get(Region.class, company.getRegionId());
	    	request.setAttribute("region", region);			
			resultModel = new ModelAndView("sl_companies", "companies", region.getCompanies());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companies.link", 
	        		"/admin/store_locator?act=company_list&id=" + company.getRegionId()));			
			
			
		} else if ("add_company".equals(action)) {			
			Company company = new Company();
	    	company.setTitle(request.getParameter("title"));
	    	if (!"unavailable".equals(request.getParameter("image"))) {	    		
	    		company.setImage(request.getParameter("image"));
	    	}
	    	if ("on".equals(request.getParameter("clear_image"))) {
	    		//delete image
	    		company.setImage("");
	    	}
	    	company.setLink(request.getParameter("link"));
	    	company.setRegionId(Long.valueOf(request.getParameter("regionId")));
			storeLocatorService.add(company);
			
			Region region = (Region) storeLocatorService.get(Region.class, company.getRegionId());
	    	request.setAttribute("region", region);			
			resultModel = new ModelAndView("sl_companies", "companies", region.getCompanies());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companies.link", 
	        		"/admin/store_locator?act=company_list&id=" + company.getRegionId()));			
			

		} else if ("company_list".equals(action)) {
			Region region = (Region) storeLocatorService.get(Region.class, Long.valueOf(id));
	    	request.setAttribute("region", region);   
			resultModel = new ModelAndView("sl_companies", "companies", region.getCompanies());
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companies.link", 
	        		"/admin/store_locator?act=company_list&id=" + id));			


	    //shop    
	    } else  if ("shop_details".equals(action)) {
	    	if (id != null) {
	    		resultModel = new ModelAndView("sl_shop_details", "shop", storeLocatorService.get(Shop.class, Long.valueOf(id)));
		        RequestUtil.getHistoryStack(request).push(
		        		new Link("shopDetails.link", 
		        		"/admin/store_locator?act=shop_details&id=" + id));
	    		
	    	} else {
	    		resultModel = new ModelAndView("sl_shop_details");
		        RequestUtil.getHistoryStack(request).push(
		        		new Link("shopDetails.link", 
		        		"/admin/store_locator?act=shop_details&companyId=" + request.getParameter("companyId")));
	    	}


	    } else if ("remove_shop".equals(action)) {		
	    	Shop shop = (Shop) storeLocatorService.get(Shop.class, Long.valueOf(id));
	    	storeLocatorService.delete(shop);
	    	
    		Company company =  (Company) storeLocatorService.get(Company.class, shop.getCompanyId());
    		request.setAttribute("shops", company.getShops());
    		resultModel = new ModelAndView("sl_company_details", "company", company);
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companyDetails.link", 
	        		"/admin/store_locator?act=company_details&id=" + shop.getCompanyId()));    		    		

		} else if ("add_shop".equals(action)) {			
			Shop shop = new Shop();
			shop.setCompanyId(Long.valueOf(request.getParameter("companyId")));
			shop.setCountry(request.getParameter("country"));
			shop.setLink(request.getParameter("link"));
			storeLocatorService.add(shop);
			
    		Company company =  (Company) storeLocatorService.get(Company.class, shop.getCompanyId());
    		request.setAttribute("shops", company.getShops());
    		resultModel = new ModelAndView("sl_company_details", "company", company);				        	    		
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companyDetails.link", 
	        		"/admin/store_locator?act=company_details&id=" + shop.getCompanyId()));

		} else if ("save_shop".equals(action)) {			
	    	Shop shop = (Shop) storeLocatorService.get(Shop.class, Long.valueOf(id));			
			shop.setCountry(request.getParameter("country"));
			shop.setLink(request.getParameter("link"));
			storeLocatorService.update(shop);

    		Company company =  (Company) storeLocatorService.get(Company.class, shop.getCompanyId());
    		request.setAttribute("shops", company.getShops());
    		resultModel = new ModelAndView("sl_company_details", "company", company);				        	    		
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("companyDetails.link", 
	        		"/admin/store_locator?act=company_details&id=" + shop.getCompanyId()));
		
		}
		

		
		//TODO SB:
		HistoryStack hSt = RequestUtil.getHistoryStack(request);
		if (hSt.getSize() == 0 || action == null) {
			logger.debug("PUT HST");
			RequestUtil.setHistoryStack(request, 
					new  HistoryStack( new Link("regions.link",  "/admin/store_locator")
					));
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("fake.link", "/admin/store_locator"));			
	        
	        RequestUtil.getHistoryStack(request).push(
	        		new Link("regions.link", 
	        		"/admin/store_locator?act=region_list"));			
			
		}
		
		
		
//		logger.debug(" HSTACK: " + Arrays.toString(RequestUtil.getHistoryStack(request).getLinks().toArray()));                        
		logger.debug("-");                        
        return resultModel == null ? new ModelAndView("sl_regions", "regions", storeLocatorService.getRegions()) : resultModel;            
            


	}
	public StoreLocatorService getStoreLocatorService() {
		return storeLocatorService;
	}
	public void setStoreLocatorService(StoreLocatorService storeLocatorService) {
		this.storeLocatorService = storeLocatorService;
	}

}
