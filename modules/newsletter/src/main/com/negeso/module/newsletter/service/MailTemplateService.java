/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.newsletter.bo.MailTemplate;
import com.negeso.module.newsletter.bo.MailTemplateI18nFields;
import com.negeso.module.newsletter.dao.MailTemplateDao;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
@Transactional
public class MailTemplateService {

	private static final Logger logger = Logger.getLogger(MailTemplateService.class);
	
	private MailTemplateDao mailTemplateDao;
	
	public MailTemplateDao getMailTemplateDao() {
		return mailTemplateDao;
	}

	public void setMailTemplateDao(MailTemplateDao mailTemplateDao) {
		this.mailTemplateDao = mailTemplateDao;
	}

	public List<MailTemplate> findAllMailTemplates(Long langId) {
		List<MailTemplate> mailTemplates = mailTemplateDao.listAllMailTemplates(); 
		for (MailTemplate mailTemplate : mailTemplates){
			mailTemplate.setCurrentLangId(langId);
		}
		return mailTemplates;
	}
	
	public MailTemplate findConfirmationText(Long langId) {
		MailTemplate mailTemplate = mailTemplateDao.findConfirmationText(); 
		mailTemplate.setCurrentLangId(langId);
		return mailTemplate;
	}

	public MailTemplate getMailTemplate(Long id) {
		return mailTemplateDao.read(id);
	}
	
	public void saveMailTemplate(MailTemplate mailTemplate) {
		mailTemplateDao.createOrUpdate(mailTemplate);
	}
	
	public void updateConfirmationText(String confText, Long lang) {
		MailTemplate confTemplate = findConfirmationText(lang);
		confTemplate.setCurrentLangId(lang);
		confTemplate.setText(confText);
		saveMailTemplate(confTemplate);
	}

	public String  deleteMailTemplate(Long mailTemplateId) {
		if (isMailTemplateLinkedToPublication(mailTemplateId)){
		MailTemplate mailTemplate = mailTemplateDao.read(mailTemplateId);
		mailTemplateDao.delete(mailTemplate);
			return "Template removed";
		}
		return "Can't remove template, because it have got publications.";
	}
	
	private boolean isMailTemplateLinkedToPublication(Long mailTemplateId) {
		int count=0;
		try {
			count = mailTemplateDao.countLinkedPublication(mailTemplateId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(count>0)
			return false;			
		else
			return true;
	}
	
	public boolean isUniqueMailTemplateTitle(MailTemplate mailTemplate) {
		return false;
	}

	public void copyMailTemplateToAllLanguages(MailTemplate mailTemplate) {
		logger.debug("+");
		MailTemplateI18nFields fields = mailTemplate.getField();
		Long currentLangId = mailTemplate.getCurrentLangId();
		if (logger.isDebugEnabled()) {
			logger.debug("current language id=" + currentLangId);
		}
		for (Map.Entry<Long, MailTemplateI18nFields> fieldEntry :
			mailTemplate.getCustomFields().entrySet()) {
			if (!fieldEntry.getKey().equals(currentLangId)) {
				fieldEntry.getValue().setTitle(fields.getTitle());
				fieldEntry.getValue().setText(fields.getText());
			}
		}
		logger.debug("-");
	}
	
	public boolean isUnique(MailTemplate mailTemplate){
		
		MailTemplate m = 
			mailTemplateDao.findByTitle(mailTemplate.getTitle().trim(), 
					mailTemplate.getCurrentLangId());
		
		if (m != null && !m.getId().equals(mailTemplate.getId()))
			return false;
		
		mailTemplateDao.evict(m);
		
		return true;
	}
	
}
