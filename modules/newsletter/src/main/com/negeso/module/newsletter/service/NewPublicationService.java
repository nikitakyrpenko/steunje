/*
 * @(#)Id: PublicationService.java, 22.02.2008 17:17:17, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Expression;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.menu.MenuItem;
import com.negeso.framework.page.Page;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.MailingState;
import com.negeso.module.newsletter.bo.Publ2Article;
import com.negeso.module.newsletter.bo.Publication;
import com.negeso.module.newsletter.bo.PublicationState;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.bo.SubscriberAttributeValue;
import com.negeso.module.newsletter.dao.MailingDao;
import com.negeso.module.newsletter.dao.MailingStateDao;
import com.negeso.module.newsletter.dao.Publ2ArticleDao;
import com.negeso.module.newsletter.dao.PublicationDao;
import com.negeso.module.newsletter.dao.PublicationStateDao;
import com.negeso.module.newsletter.dao.SubscriberAttributeTypeDao;
import com.negeso.wcms.page.service.PageComponentService;
import com.negeso.wcms.page.service.PageService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
@Transactional
public class NewPublicationService {

	private static final Logger logger = Logger.getLogger(NewPublicationService.class);

	private static final String NEW_ARTICLE_CLASS = "newsletter_article";
	
	private PublicationDao publicationDao;
	private PublicationStateDao publicationStateDao;
	private MailingDao mailingDao;
	private MailingStateDao mailingStateDao;
	private Publ2ArticleDao publ2ArticleDao;
	private SubscriberAttributeTypeDao subscriberAttributeTypeDao;
	private PageService pageService;
	
	private Connection connection;
	
	public PageService getPageService() {
		return pageService;
	}

	public void setPageService(PageService pageService) {
		this.pageService = pageService;
	}
	
	public void setPublicationDao(PublicationDao publicationDao) {
		this.publicationDao = publicationDao;
	}
	
	public void deletePublication(Publication publication){
		publicationDao.delete(publication);
	}
	
	public List<Publication> listPublicationsByCategoryId(Long categoryId){
		return publicationDao.listBySubscriptionCategory(categoryId);
	}
	
	public Publication getPublicationById(Long publicationId){
		return publicationDao.read(publicationId);
	}
	
	public String getPublicationPageLink(Publication publication, Long langId){
		try {
			Publ2Article p2A = publ2ArticleDao.findByPublicationIdLangId(publication.getId(), langId);
			if (p2A != null) {
				Page page = Page.findById(p2A.getPageId());
				
				String hostName = Env.getHostName();
				if (hostName == null)
					hostName = Configuration.getNotificationHostName();
				
				return hostName + page.getFilename() + publication.getAccessCode();
			}
		} catch (ObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
		}		
		return null;
	}
	
	public List<Publication> getSentPublications(Long langId){
		PublicationState state = publicationStateDao.
			findPublicationStateByName(Configuration.PUBLICATION_STATUS_SENT);
		
		if (state != null){
			List<Publication> publications = publicationDao.listPublication(state.getId());
			for (Publication p : publications){
				p.setLangId(langId);
			}
			return publications;
		}
		return null;
	}
	
	public void save(Publication publication){
		publicationDao.evict(publication);
		publicationDao.createOrUpdate(publication);
	}
	
	private Timestamp getMinimalMonthDate(){
		GregorianCalendar date = new GregorianCalendar();
		date.set(Calendar.DAY_OF_MONTH, 1);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 1);
		return new Timestamp(date.getTimeInMillis());
	}
	
	private Timestamp getMaximumMonthDate(){
		GregorianCalendar date = new GregorianCalendar();
		date.set(Calendar.DAY_OF_MONTH, date.getMaximum(Calendar.DAY_OF_MONTH));
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 59);
		return new Timestamp(date.getTimeInMillis());
	}
	
	public int countSentMailByCurrentMonth(Long publicationId){
		
		MailingState state = mailingStateDao.
			findByName(Configuration.PUBLICATION_STATUS_SENT);
		
		return mailingDao.countMailByPeriod(publicationId, state.getId(), 
				getMinimalMonthDate(), getMaximumMonthDate());
	}
	
	public PublicationState getPublicationState(Long id){
		return publicationStateDao.read(id);
	}
	
	public List<PublicationState> getPublicationStates(){
		return publicationStateDao.readAll();
	}
	
	public PublicationState getPublicationStateByName(String name){
		return publicationStateDao.findPublicationStateByName(name);
	}

	public PublicationStateDao getPublicationStateDao() {
		return publicationStateDao;
	}

	public void setPublicationStateDao(PublicationStateDao publicationStateDao) {
		this.publicationStateDao = publicationStateDao;
	}
	
	public boolean isUnique(Publication publication){
		Publication p = publicationDao.findByTitle(publication.getTitle().trim(), 
				publication.getLangId());
		if (p != null && !p.getId().equals(publication.getId()))
			return false;
		publicationDao.evict(p);
		return true;
	}
	
	public PublicationDao getPublicationDao(){
		return publicationDao;
	}
	
	public String getUpdatedForSubscriberText(String publicationText, 
			Set<SubscriberAttributeValue> attrs){
		String text = publicationText;
		for (SubscriberAttributeValue value : attrs){
			String regExpress = "%" + value.getSubscriberAttributeType().getKey() + "%";
			if (publicationText.contains(new StringBuffer(regExpress))){
				text = text.replaceAll(regExpress, value.getValue());
			}
		}
		return text;
	}

	public List<Publication> listPublicationByScheduledDate() {
		logger.debug("+");

		PublicationState scheduledState = publicationStateDao.
			findPublicationStateByName(Configuration.PUBLICATION_STATUS_SCHEDULED); 
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		List<Publication> publications = publicationDao.readByCriteria(
				Expression.gt("publishDate", currentDate),
				Expression.eq("publicationState", scheduledState)
		);
		
		if (logger.isDebugEnabled()) {
			logger.debug(format("Found %s publications to schedule", 
					publications.size()));
		}
		
		logger.debug("-");
		return publications;
	}

	public MailingDao getMailingDao() {
		return mailingDao;
	}

	public void setMailingDao(MailingDao mailingDao) {
		this.mailingDao = mailingDao;
	}
	
	public void unSchedule(Long publicationId){
		logger.debug("+");
		Publication publication = publicationDao.read(publicationId);
		
		PublicationState state = publicationStateDao.
			findPublicationStateByName(Configuration.PUBLICATION_STATUS_SUSPENDED);
		
		publication.setPublicationState(state);
		
		logger.debug("-");
	}
	
	public void addPageLink(Publication publication, Long langId){
		logger.debug("+");

		Article article = createArticle(publication, langId);
		createPageForPublication(publication, article, langId);
		
		logger.debug("-");
	}
	
	public void updatePageLink(Publication publication, Long langId){
		logger.debug("+");
		
		Publ2Article p2A = publ2ArticleDao.findByPublicationIdLangId(
				publication.getId(), langId);
		
		if (p2A == null)
			return;
		
		try {
			Article a = Article.findById(p2A.getArticleId());
			a.setText(publication.getText());
			a.update();
		} catch (CriticalException e1) {
			logger.error(e1.getMessage(), e1);
		} catch (ObjectNotFoundException e1) {
			logger.error(e1.getMessage(), e1);
		}
		try{
			Page page = Page.findById(p2A.getPageId());
			page.setTitle(publication.getTitle());
			page.setPublishDate(publication.getPublishDate());
			page.update();
			
			MenuItem menuItem = MenuItem.findByLink(page.getFilename());
			menuItem.setTitle(publication.getTitle());
			menuItem.update();
		} catch (ObjectNotFoundException e){
			logger.error("-error", e);
		}
		logger.debug("-");
	}

	private Page createPageForPublication(Publication publication, Article article, Long langId) {

		pageService.setConnection(this.getConnection()); 
		
		String fileName = generatePageFileName();
		logger.debug("fileName:" + fileName);
		while (pageService.isPageExistsWithFilename(fileName)) {
			fileName = generatePageFileName();
			logger.debug("fileName:" + fileName);
		}
		
		Page page = pageService.createSpecialPage(
			fileName, 
			publication.getTitle(), 
			publication.getLangId()
		);
		
		this.createArticleLink(page, publication, article, langId);

		PageComponentService componentService = new PageComponentService();
		componentService.setConnection(this.getConnection());
		
		componentService.createArticleComponent(page.getId(), article.getId());
		componentService.createMenuComponent(page.getId());
		
		return page;
	}

	public Article createArticle(Publication publication, long langId) {
		Article article = new Article();
	    article.setClass_(NEW_ARTICLE_CLASS);
	    article.setText(publication.getText());
	    article.setLangId(langId);
	    article.insert(getConnection());
	    return article;
	}

	private Publ2Article createArticleLink(Page page, Publication publication, Article article, long langId) {
		Publ2Article publ2article = new Publ2Article();
		publ2article.setId(0L);
        publ2article.setArticleId(article.getId());
        publ2article.setPublication(publication);
        publ2article.setLangId(langId);
        publ2article.setPageId(page.getId());
        publ2ArticleDao.createOrUpdate(publ2article);
        return publ2article;
	}

	
	public String generatePageFileName() {
		return String.format(
			Configuration.getSpecialPageNamePattern(), 
			RandomStringUtils.randomNumeric(4)
		);
	}
	
	public String generateAccessCode() {
		return String.format("?%s=1", RandomStringUtils.randomAlphanumeric(12));
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setMailingStateDao(MailingStateDao mailingStateDao) {
		this.mailingStateDao = mailingStateDao;
	}

	public void setPubl2ArticleDao(Publ2ArticleDao publ2ArticleDao) {
		this.publ2ArticleDao = publ2ArticleDao;
	}

	public SubscriberAttributeTypeDao getSubscriberAttributeTypeDao() {
		return subscriberAttributeTypeDao;
	}

	public void setSubscriberAttributeTypeDao( SubscriberAttributeTypeDao dao) {
		this.subscriberAttributeTypeDao = dao;
	}
	
}
