package com.negeso.module.baten.web.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.module.baten.dao.ArticleInfoDao;
import com.negeso.module.baten.entity.ArticleInfo;
import com.negeso.module.baten.web.ResponseJsonWrapper;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderController extends AbstractJsonController {

	private ArticleInfoDao articleInfoDao;

	public CreateOrderController() {
	}

	private List<ArticleInfo> findArticlesById(long[] ids) {
		ArrayList<ArticleInfo> s = new ArrayList<ArticleInfo>(ids.length);
		for (Long id : ids) {
			ArticleInfo articleInfo = articleInfoDao.read(id);
			if (articleInfo != null)
				s.add(articleInfo);
		}
		return s;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = SessionData.getSessionData(request).getUser();
		if (user == null)
			user = super.checkCredentials(request);
		if (user == null) {
			super.writeToResponse(response, HttpStatus.SC_UNAUTHORIZED, new ArrayList<ArticleInfo>(0));
			return null;
		}

		long[] fldProductNumbers = ServletRequestUtils.getLongParameters(request, "fld_product_number[]");
		long[] fldProductCount = ServletRequestUtils.getLongParameters(request, "fld_product_count[]");
		StringBuilder sb = new StringBuilder();
		String error = null;
		for (Long fldProductNumber : fldProductNumbers) {
			ArticleInfo article = this.articleInfoDao.read(fldProductNumber);
			if (article == null)
				error = "Niet gevonden";
			else if (!article.isAvailable())
				error = "Niet meer bestelbaar";
			sb
					.append("- ")
					.append(error != null ? "ERROR " + fldProductNumber + " " + error : "OK " + fldProductNumber + " ")
					;
		}

		super.writeToResponse(response, new ResponseJsonWrapper<ArticleInfo>(new ArrayList<ArticleInfo>(0))
				.addMessage(sb.toString())
				.addStatus(2)
		);

		return null;
	}

	public void setArticleInfoDao(ArticleInfoDao articleInfoDao) {
		this.articleInfoDao = articleInfoDao;
	}
}
