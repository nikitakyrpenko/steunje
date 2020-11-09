package com.negeso.module.webshop.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.HttpException;
import com.negeso.module.webshop.service.FriendlyUrlForWebshop;
import com.negeso.framework.util.json.JsonSupportForController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@SessionAttributes(SessionData.USER_ATTR_NAME)
public class WebshopFriendlyUrlController extends JsonSupportForController{

	private final FriendlyUrlForWebshop friendlyUrlForWebshopService;

	@Autowired
	public WebshopFriendlyUrlController(FriendlyUrlForWebshop friendlyUrlForWebshopService) {
		this.friendlyUrlForWebshopService = friendlyUrlForWebshopService;
	}

	@RequestMapping(value = "/webshop/api/friendly_url", method = RequestMethod.POST)
	public void recreateUrls(HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);

		friendlyUrlForWebshopService.insertAllCategoryUrls();
		friendlyUrlForWebshopService.deleteAllUrlsByType(UrlEntityType.PRODUCT_CATEGORY);
		friendlyUrlForWebshopService.insertAllProductUrls();
		friendlyUrlForWebshopService.deleteAllUrlsByType(UrlEntityType.PRODUCT);

		super.writeToResponse(res, "{\"status\": \"done\"}");
	}
}
