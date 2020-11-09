package com.negeso.module.google_shop.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.maxmind.geoip.LookupService;
import com.negeso.framework.Env;
import com.negeso.framework.cache.Cachable;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.site.service.SiteUrlService;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.google_shop.bo.GoogleMerchant;
import com.negeso.module.google_shop.service.MerchantService;

public class MerchantController extends MultiActionController {
	
	private static Logger logger = Logger.getLogger(MerchantController.class);
	
	private MerchantService merchantService;
	private List<String> defaultConditions;
	private List<String> defaultAvailabilities;
	
	public ModelAndView browse(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestUtil.getHistoryStack(request).push( new Link("GM_GOOGLE_MERCHANTS", 
				"/admin/google_shop.html", true ,-1));
		return new PreparedModelAndView("gm_metchants_list")
				.addToModel("metchants", merchantService.list())
				.get();
	}
	
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		if (id > 0) {
			GoogleMerchant merchant = merchantService.findById(id);
			if (merchant != null) {
				if ("save".equals(ServletRequestUtils.getStringParameter(request, "todo"))) {
					save(request, merchant);
					if (ServletRequestUtils.getBooleanParameter(request, "close", false)) {
						return new ModelAndView("redirect:/admin/google_shop.html");
					}
				}
				RequestUtil.getHistoryStack(request).push( new Link("GM_EDIT_MERCHANT", 
						"/admin/gm_edit_merchant.html" , true));
				return getEditModelAndView(merchant);
			}
		}
		return browse(request, response);
	}
	private ModelAndView getEditModelAndView(GoogleMerchant merchant) {
		return getEditModelAndView(merchant, null);
	}
	private ModelAndView getEditModelAndView(GoogleMerchant merchant, String errorMessage) {
		return new PreparedModelAndView("gm_edit_merchant")
		.addToModel("merchant", merchant)
		.addToModel("hostNames", getSiteUrlService().listBySiteId(Env.getSiteId()))
		.addToModel("languages", Language.getItems())
		.addToModel("countries", LookupService.getCountries())
		.addToModel("defaultConditions", defaultConditions)
		.addToModel("defaultAvailabilities", defaultAvailabilities)
		.addToModel("errorMessage", errorMessage)
		.get();
	}
	
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if ("save".equals(ServletRequestUtils.getStringParameter(request, "todo"))) {
			GoogleMerchant merchant = new GoogleMerchant();  
			try {
				save(request, merchant);
			} catch (Exception e) {
				logger.error("Error: ", e);
				return getEditModelAndView(merchant, e.getMessage());
			}
			if (ServletRequestUtils.getBooleanParameter(request, "close", false)) {
				return new ModelAndView("redirect:/admin/google_shop.html");
			} else {
				RequestUtil.getHistoryStack(request).go(0);
				return new ModelAndView("redirect:/admin/gm_edit_merchant.html?action=edit&id=" + merchant.getId());
			}
		}
		RequestUtil.getHistoryStack(request).push( new Link("GM_ADD_MERCHANT", 
				"/admin/gm_edit_merchant.html" , true ));
		return getEditModelAndView(new GoogleMerchant());
			
	}
	
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			Long id = NegesoRequestUtils.getId(request, 0L);
			if (id > 0) {
				GoogleMerchant merchant = merchantService.findById(id);
				if (merchant != null) {
					merchantService.delete(merchant);
					((Cachable)DispatchersContainer.getInstance().getBean("google_shop", "generateFeedService")).resetCache();
				}
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			writeToResponse(response, e.getMessage());
		}
		return null;
	}
	
	private void save(HttpServletRequest request, GoogleMerchant merchant) {
		if (merchant.getId() != null && merchant.getId() > 0) {
			merchant.setEnabled(false);
			NegesoRequestUtils.bind(merchant, request);
		} else {
			NegesoRequestUtils.bind(merchant, request);
			if (!merchantService.isUnique(merchant)) {
				throw new CriticalException(DatabaseResourceBundle.getTranslation("google_shop", Env.getInterfaceLanguageCode(request), "GM_MERCHANT_IS_NOT_UNIQUE"));
			}
		}
		merchantService.createOrUpdate(merchant);
		((Cachable)DispatchersContainer.getInstance().getBean("google_shop", "generateFeedService")).resetCache();
	}
	
	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
		response.setStatus(401);
        PrintWriter out = response.getWriter();
        out.print(meassage);
        out.close();
	}

	public void setMerchantService(MerchantService merchantService) {
		this.merchantService = merchantService;
	}
	
	private SiteUrlService getSiteUrlService() {
		return (SiteUrlService)DispatchersContainer.getInstance().getBean("core", "siteUrlService");
	}

	public MerchantService getMerchantService() {
		return merchantService;
	}

	public List<String> getDefaultConditions() {
		return defaultConditions;
	}

	public void setDefaultConditions(List<String> defaultConditions) {
		this.defaultConditions = defaultConditions;
	}

	public List<String> getDefaultAvailabilities() {
		return defaultAvailabilities;
	}

	public void setDefaultAvailabilities(List<String> defaultAvailabilities) {
		this.defaultAvailabilities = defaultAvailabilities;
	}

}
