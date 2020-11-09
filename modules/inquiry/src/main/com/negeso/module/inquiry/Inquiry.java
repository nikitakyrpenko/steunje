/*
 * @(#)$Id: Inquiry.java,v 1.6, 2006-06-01 13:46:54Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.inquiry.command.InquiryController;
import com.negeso.module.inquiry.dao.OptionDao;
import com.negeso.module.inquiry.dao.QuestionDao;
import com.negeso.module.inquiry.dao.QuestionnaireDao;
import com.negeso.module.inquiry.dao.RespondentDao;
import com.negeso.module.inquiry.dao.UserDao;
import com.negeso.module.inquiry.domain.Option;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.Questionnaire;
import com.negeso.module.inquiry.domain.Respondent;
import com.negeso.module.inquiry.domain.User;

/**
 * Inquiry Module business facade.
 *
 * @version     $Revision: 7$
 * @author      Stanislav Demchenko
 */
public class Inquiry {

    private static final QuestionnaireDao questionnaireDao = new QuestionnaireDao();
    private static final QuestionDao questionDao = new QuestionDao();
    private static final OptionDao optionDao = new OptionDao();
    private static final RespondentDao respondentDao = new RespondentDao();
	private static final UserDao userDao = new UserDao();
    
    private static final Inquiry INSTANCE = new Inquiry();
    
    private static Logger logger = Logger.getLogger(InquiryController.class);

    public List<Questionnaire> getQuestionnaireList(Language lang) {
        return questionnaireDao.getQuestionnaireList(lang);
    }

    public void storeQuestionnaire(Questionnaire questionnaire) {
        if(questionnaire.getId() > 0)
            questionnaireDao.updateQuestionnaire(questionnaire);
        else
            questionnaireDao.insertQuestionnaire(questionnaire);
    }

    /** Returns an existing Questionnaire or null */
    public Questionnaire loadQuestionnaire(int id) {
        return questionnaireDao.loadQuestionnaire(id);
    }
    
    
    public List<Questionnaire> getActiveQuestionnaires(Language lang) {
        return questionnaireDao.getActiveQuestionnaires(lang);
    }

    public void deleteQuestionnaire(int id) {
        questionnaireDao.deleteQuestionnaire(id);
    }
    
    /** Returns an existing Question or null */
    public Question loadQuestion(int id) {
        return questionDao.loadQuestion(id);
    }

    public void deleteQuestion(int id) {
        questionDao.deleteQuestion(id);
    }

    public void storeQuestion(Question question) {
        if(question.getId() > 0)
            questionDao.updateQuestion(question);
        else
            questionDao.insertQuestion(question);
    }

    public static Inquiry getInstance() {
        return INSTANCE;
    }

    public List<Question> getQuestionsByQuestionnaireId(int id) {
        return questionDao.getQuestionsByQuestionnaireId(id);
    }

    public List<Option> getOptionsByQuestionId(int id) {
        return optionDao.getOptionsByQuestionId(id);
    }

    public Option loadOption(int id) {
        return optionDao.loadOption(id);
    }

    public void deleteOption(int id) {
        optionDao.deleteOption(id);
    }

    public void storeOption(Option option) {
        if(option.getId() > 0)
            optionDao.updateOption(option);
        else
            optionDao.insertOption(option);
    }

    public void storeRespondent(Element respondent) {
        respondentDao.storeRespondent(respondent);
    }

    public List<Respondent> getRespondentsByQuestionnaireId(int id) {
        return respondentDao.getRespondentsByQuestionnaireId(id);
    }

    public void buildXmlRespondentWithAnswers(Element parent, int respondentId){
        respondentDao.modelRespondentWithAnswers(parent, respondentId);
    }

    public Respondent loadRespondent(int respId) {
        return respondentDao.loadRespondent(respId);
    }

    public void deleteRespondent(int respId) {
        respondentDao.deleteRespondent(respId);
    }

    public void buildXmlStatistics(Element parent, int quizId) {
        logger.debug("+ -");
        new StatisticsXmlBuilder().buildXmlStatistics(parent, quizId);
    }

    public void switchOptionPositions(int id1, int id2) {
        optionDao.switchOptionPositions(id1, id2);
    }

    public void switchQuestionPositions(int id1, int id2) {
        questionDao.switchQuestionPositions(id1, id2);
    }

	public List<User> getUsersByQuestionnaireId(int id) {
		return userDao.getUsersByQuestionnaireId(id);
	}

	public User loadUser(int id) {
		return userDao.loadUser(id);
	}
	
	public void storeUser(User user) {
        if(user.getId() > 0)
        	userDao.updateUser(user);
        else
        	userDao.insertUser(user);
	}
	
	public void deleteUser(int id) {
		userDao.deleteUser(id);
	}

	public User loadUser(String login, String password, int quizId) {
		return userDao.loadUser(login, password, quizId);
	}

	public List<User> getLazyUsers(int questionnaireId) {
		return userDao.getLazyUsers(questionnaireId);
	}
	
	/** @return Article of the inquiry password reminder; never null */
	public Article getPasswordReminder(Language language) {
        return findOrCreateArticle(
                "inquiry_module_password_reminder",
                language,
                "Password reminder",
                "Here is your login and password to fill a questionnaire");
	}

    /** @return Article inviting to fill a quiz; never null */
    public Article getInquiryReminder(Language language) {
        return findOrCreateArticle(
                "inquiry_module_questionnaire_reminder",
                language,
                "Questionnaire reminder",
                "You are invited to answer a questionnaire");
    }

    public Article getInquiryInvitation(Language language) {
        return findOrCreateArticle(
                "inquiry_module_questionnaire_invitation",
                language,
                "Invitation to answer a questionnaire",
                "You are invited to answer a questionnaire");
    }
    
    private Article findOrCreateArticle(
        String clazz, Language lang, String defaultHead, String defaultText) {
        synchronized (this) {
            try {
                return Article.findByClass(clazz, lang.getCode());
            } catch (ObjectNotFoundException onfe) {
                logger.info("not found: " + lang + ", class: " + clazz, onfe);
                try {
                    Article article = new Article();
                    article.setHead(defaultHead);
                    article.setText(defaultText);
                    article.setLangId(lang.getId());
                    article.setClass_(clazz);
                    article.insert();
                    return article;
                } catch (Exception e) {
                    throw new RuntimeException("Cannot create reminder text", e);
                }
            }
        }
    }
    
    public void copyQuestionnaire(int questionnaireId, String title) {
    	questionnaireDao.copyQuestionnaire(questionnaireId, title);
    }

    public void copyQuestion(int questionId, String title) {
    	questionDao.copyQuestion(questionId, title);
    }

}