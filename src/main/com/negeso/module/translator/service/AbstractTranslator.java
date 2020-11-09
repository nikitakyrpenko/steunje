/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.translator.service;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.*;
import com.negeso.module.core.domain.ArticleRevision;
import com.negeso.module.form_manager.domain.Forms;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public abstract class AbstractTranslator {
	
	public enum TranslationStrategyType {
		all, newAdded, allExceptSpecial 
	}
	
	protected ITranslateService translateService;
	
	protected String progressIndication = StringUtils.EMPTY;
	
	protected Map<Long, Forms> oldFormIdToNewFormObjectMap = new HashMap<Long, Forms>();
	
	public abstract void copyAndTranslate(Connection con, Language from, Language to);
	
	public abstract void copyAndTranslate(SessionFactory factory);
	
	public abstract void clean(Connection con, Language to);
	
	public abstract void clean(SessionFactory factory);
	
	public void update(Connection con, Language from, Language to){}
	
	public void translate(Connection con, Language from, Language to) {
		clean(con, to);
		copyAndTranslate(con, from, to);
	}
	
	public AbstractTranslator(ITranslateService translateService) {
		this.translateService = translateService;
	}
	
	public Long copyAndTranslateArticle(Connection con, Long articleId, Language from, Language to, ITranslateService translateService) throws CriticalException, ObjectNotFoundException {
		if (articleId != null) {
			Article article = copyAndTranslateArticle(translateService, con, articleId, from, to);
			if (article != null) {
				return article.getId();
			}
		}
		return null;
	}
	
	public Article copyAndTranslateArticle(ITranslateService translateService, Connection con, Long articleId, Language from, Language to) throws CriticalException, ObjectNotFoundException {
		if (articleId != null) {
			Article article = Article.findById(con, articleId);
			article.setLangId(to.getId());
			String text = article.getText();
			if (StringUtils.isNotBlank(text)) {
				text = doReplace(text);
				text = translateService.translateHtml(text, from.getCode(), to.getCode());
				text = doReplaceBack(text);
			}
			article.setText(text);
			article.insert();
			createRevision(article, con);
			return article;
		}
		return null;
	}
	
	private static void createRevision(Article article, Connection con) throws CriticalException, ObjectNotFoundException {
		ArticleRevision articleRevision = new ArticleRevision(article.getId(), article.getText());
		User user = (User) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute(SessionData.USER_ATTR_NAME);
		if ( user != null ) {
			articleRevision.setAuthor(user.isSuperUser() ? user.getSuperuserLogin() : user.getLogin());
		}
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement( "INSERT INTO article_revision (article_id, date, text, author) values (?,?,?,?)" );
            stmt.setLong(1, articleRevision.getArticleId());
            stmt.setTimestamp(2, articleRevision.getDate());
            stmt.setString(3, articleRevision.getText());
            stmt.setString(4, articleRevision.getAuthor());
            stmt.execute();
        }
        catch (Throwable ex) {
            //ignore
        }
        finally {
            DBHelper.close(stmt);
        }
	}
	
	private static final String FORM_REPLECER = "9frm%sfrm9";
	
	private String doReplace(String text) {
		for (Entry<Long, Forms> idToForm : oldFormIdToNewFormObjectMap.entrySet()) {
			String findRegexp = String.format("(?i)<FORM.*[^>]form_id=\"%s\".*?</FORM>", idToForm.getKey());
			text = Pattern.compile(findRegexp,Pattern.DOTALL).matcher(text).replaceAll(String.format(FORM_REPLECER, idToForm.getKey()));
		}
		return text;
	}
	
	private String doReplaceBack(String text) {
		for (Entry<Long, Forms> idToForm : oldFormIdToNewFormObjectMap.entrySet()) {
			Forms form = idToForm.getValue();
			String formText = String.format(HTML_FORM, form.getId(), form.getName(), form.getEx(), form.getId(), 
					form.getArticle() != null ? form.getArticle().getText() : StringUtils.EMPTY);
			text = text.replace(String.format(FORM_REPLECER, idToForm.getKey()), formText);
		}
		return text;
	}
	
	private static final String HTML_FORM = "<FORM class=contact " +
    "onsubmit=\"return validate(this);\" " +
    "method=post encType=multipart/form-data form_id=\"%s\">" +
    " <INPUT form_name='%s'  form_ex='%s' type=hidden value=%s name=mailToFlag>" + 
    "<input type='hidden' name='first_obligatory_email_field' value=''>%s</FORM>";
	
	public String getProgressIndication() {
		return progressIndication;
	}

	public void setProgressIndication(String progressIndication) {
		this.progressIndication = progressIndication;
	}

	public ITranslateService getTranslateService() {
		return translateService;
	}

	public Map<Long, Forms> getOldFormIdToNewFormObjectMap() {
		return oldFormIdToNewFormObjectMap;
	}

	public void setOldFormIdToNewFormObjectMap(
			Map<Long, Forms> oldFormIdToNewFormObjectMap) {
		this.oldFormIdToNewFormObjectMap = oldFormIdToNewFormObjectMap;
	}
}

