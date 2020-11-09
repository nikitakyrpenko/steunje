package com.negeso.module.baten.web.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.module.baten.dao.ArticleInfoDao;
import com.negeso.module.baten.entity.ArticleInfo;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticleInfoController extends AbstractJsonController {

	private List<ArticleInfo> loginObject;
	private ArticleInfoDao articleInfoDao;

	public ArticleInfoController() {
		loginObject = new ArrayList<ArticleInfo>();
		loginObject.add(new ArticleInfo(true, 5555555555555L, "R", "/image/asd.png", "desc", BigDecimal.valueOf(12.5), BigDecimal.valueOf(14), 1, 1, false));
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
		if (ServletRequestUtils.getBooleanParameter(request, "invalidateSession", false)) {
			return invalidateSession(request, response);
		}
		User user = SessionData.getSessionData(request).getUser();
		if (user == null)
			user = super.checkCredentials(request);
		if (user == null) {
			super.writeToResponse(response, HttpStatus.SC_UNAUTHORIZED, this.loginObject);
			return null;
		} else if (SessionData.getSessionData(request).getUser() == null) {
			super.writeToResponse(response, HttpStatus.SC_OK, loginObject);
			SessionData.getSessionData(request).setSuperuser(user);
			return null;
		}

		long[] fld_product_numbers = ServletRequestUtils.getLongParameters(request, "fld_product_number[]");
		super.writeToResponse(response, HttpStatus.SC_OK, fld_product_numbers.length == 0 ? new ArrayList<ArticleInfo>(this.articleInfoDao.readAll()) : this.findArticlesById(fld_product_numbers));

		return null;
	}

	public void setArticleInfoDao(ArticleInfoDao articleInfoDao) {
		this.articleInfoDao = articleInfoDao;
	}
}
