package com.negeso.module.baten.web.controller;

import com.negeso.module.baten.entity.ArticleInfo;
import com.negeso.module.baten.service.ArticleInfoService;
import com.negeso.module.core.PreparedModelAndView;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class WebshopController extends MultiActionController {
	private ArticleInfoService articleInfoService;

	public ModelAndView list(HttpServletRequest req, HttpServletResponse res) throws Exception {
		List<ArticleInfo> products = articleInfoService.readAll();
		return new PreparedModelAndView("webshop")
				.addToModel("products", products)
				.addToModel("imageWidth", "100px")
				.addToModel("imageHeight", "100px")
				.get();
	}

	public ModelAndView add(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ArticleInfo product = new ArticleInfo();
		return new PreparedModelAndView("webshop_product_details")
				.addToModel("product", product)
				.addToModel("newInstance", true)
				.get();
	}

	public ModelAndView edit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long productId = ServletRequestUtils.getLongParameter(req, "id");
		ArticleInfo product = articleInfoService.read(productId);

		return new PreparedModelAndView("webshop_product_details")
				.addToModel("product", product)
				.addToModel("newInstance", false)
				.get();
	}

	public ModelAndView save(HttpServletRequest req, HttpServletResponse res, ArticleInfo product) throws Exception {
		if (!product.isValid())
			return new PreparedModelAndView("redirect:/admin/webshop_products.html?action=add")
					.addToModel("product", product)
					.get();

		articleInfoService.saveOrUpdate(product);
		return new PreparedModelAndView("redirect:/admin/webshop_products.html")
				.get();
	}

	public void delete(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Long productId = ServletRequestUtils.getLongParameter(req, "id");
		ArticleInfo product = articleInfoService.read(productId);
		articleInfoService.delete(product);
		res.getOutputStream().print("success");
	}

	public void setArticleInfoService(ArticleInfoService articleInfoService) {
		this.articleInfoService = articleInfoService;
	}
}
