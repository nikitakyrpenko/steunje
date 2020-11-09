/*
 * @(#)$Id: InquiryController.java,v 1.16, 2007-01-09 18:46:56Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry.command;

import static com.negeso.module.dictionary.DictionaryUtil.findEntry;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.strip;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.inquiry.DuplicateQuestionException;
import com.negeso.module.inquiry.DuplicateQuestionnaireException;
import com.negeso.module.inquiry.EmailNotUniqueException;
import com.negeso.module.inquiry.EmailNotValidException;
import com.negeso.module.inquiry.Inquiry;
import com.negeso.module.inquiry.XHelper;
import com.negeso.module.inquiry.domain.Option;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.QuestionType;
import com.negeso.module.inquiry.domain.Questionnaire;
import com.negeso.module.inquiry.domain.Respondent;
import com.negeso.module.inquiry.domain.User;

/**
 * Controller of administrator's UI of Inquiry module.
 *
 * @version     $Revision: 17$
 * @author      Stanislav Demchenko
 */
public class InquiryController extends AbstractCommand {
	
    private static Logger logger = Logger.getLogger(InquiryController.class);
    
    private Inquiry inquiry = Inquiry.getInstance();
    
    private Element model = Xbuilder.createTopEl("model");
    
    private Collection<String> errors = new LinkedList<String>();
    
    private final static String VIEW_QUESTIONNAIRE_LIST =
            "VIEW_QUESTIONNAIRE_LIST";
    
    private final static String VIEW_QUESTIONNAIRE = "VIEW_QUESTIONNAIRE";
    
    private final static String VIEW_QUESTIONS = "VIEW_QUESTIONS";
    
    private final static String VIEW_RESPONDENTS = "VIEW_RESPONDENTS";
    
    private final static String VIEW_RESPONDENT = "VIEW_RESPONDENT";
    
    private final static String VIEW_STATISTICS = "VIEW_STATISTICS";
    
    private final static String FORM_QUESTIONNAIRE = "FORM_QUESTIONNAIRE";

    private static final String FORM_QUESTION = "FORM_QUESTION";
    
    private static final String FORM_USER = "FORM_USER";
    
	private static final String VIEW_USERS = "VIEW_USERS";

	private static final String PREVIEW_QUESTIONNAIRE = "PREVIEW_QUESTIONNAIRE";
	
    private final static Session session;

    private static InternetAddress FROM;
    
    static {
        Properties props = new Properties();
        props.put("mail.smtp.host", Env.getValidSmtpHost());
        session = Session.getInstance(props);
        try {
            FROM = new InternetAddress(Env.getProperty("inquiry_module.from"));
        } catch (AddressException e) {
            logger.error(e);
        }
    }
    
    private String view;
    
    private String i18nLangCode;
    
    private RequestContext req;

	private Language pageLanguage;

    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_FAILURE);
        req = getRequestContext();
        if(!SecurityGuard.isContributor(req.getSession().getUser())) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- not a contributor");
            return response;
        }
        i18nLangCode = req.getSession().getInterfaceLanguageCode();
        pageLanguage = req.getSession().getLanguage();
        act();
        Xbuilder.setAttr(model, "view", view);
        for (String error : errors) {
            if(error != null && !error.equals("")) {
                Xbuilder.addEl(model, "error", error);
            }
        }
        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, model.getOwnerDocument());
        logger.debug("-");
        return response;
    }
    
    private void act() {
        String action = req.getString("action", null);
        try {
            if ("viewQuestionnaire".equals(action)) {
				XHelper.addXmlQuestionnaire(
                        model, inquiry.loadQuestionnaire(req.getIntValue("id")));
                view = VIEW_QUESTIONNAIRE;
            } else if ("viewQuestions".equals(action)) {
                viewQuestions(inquiry.loadQuestionnaire(req.getIntValue("id")));
            } else if ("viewRespondents".equals(action)) {
                viewRespondents(req.getIntValue("id"));
            } else if ("viewRespondent".equals(action)) {
                viewRespondent(req.getIntValue("id"));
            } else if ("deleteRespondent".equals(action)) {
                onDeleteRespondent(req.getIntValue("id"));
            } else if ("viewStatistics".equals(action)) {
                viewStatistics(req.getIntValue("id"));
            } else if ("storeQuestionnaire".equals(action)) {
                onStoreQuestionnaire();
            } else if ("editQuestionnaire".equals(action)) {
                formQuestionnaire(null);
            } else if ("deleteQuestionnaire".equals(action)) {
                onDeleteQuestionnaire();
            } else if ("copyQuestionnaire".equals(action)) {
                onCopyQuestionnaire();
            } else if ("storeQuestion".equals(action)) {
                onStoreQuestion();
            } else if ("editQuestion".equals(action)) {
                formQuestion(null);
            } else if ("deleteQuestion".equals(action)) {
                onDeleteQuestion();
            } else if ("moveQuestion".equals(action)) {
                onMoveQuestion();
            } else if ("copyQuestion".equals(action)) {
                onCopyQuestion();
            } else if ("storeOption".equals(action)) {
                onStoreOption();
            } else if ("deleteOption".equals(action)) {
                onDeleteOption();
            } else if ("moveOption".equals(action)) {
                onMoveOption(); 
			} else if ("viewUsers".equals(action)) {
				viewUsers(inquiry.loadQuestionnaire(req.getIntValue("id")));
			} else if ("storeUser".equals(action)) {
	            onStoreUser();
            } else if ("editUser".equals(action)) {
                formUser(null);
			} else if ("deleteUser".equals(action)) {
	            onDeleteUser();	
			} else if ("importUsers".equals(action)) {
	            onImportUsers();	
			} else if ("storeInvitationSubject".equals(action)) {
	            onStoreInvitationSubject();
			} else if ("storeQuestionnaireReminderSubject".equals(action)) {
	            onStoreQuestionnaireReminderSubject();
			} else if ("storePasswordReminderSubject".equals(action)) {
	            onStorePasswordReminderSubject();
			} else if ("sendPasswordReminder".equals(action)) {
	            onSendPasswordReminder();
			} else if ("sendQuestionaireReminder".equals(action)) {
	            onSendQuestionaireReminder();
			} else if ("redirectQuestion".equals(action)) {
	            redirectQuestion();
			} else if ("previewQuestionnaire".equals(action)) {
	            previewQuestionnaire();
			} else {
                viewQuestionnaireList();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            addException(e);
            viewQuestionnaireList();
        }
    }

	private void onMoveQuestion() {
        logger.debug("+");
        Question q1 = inquiry.loadQuestion(req.getIntValue("id1"));
        Question q2 = inquiry.loadQuestion(req.getIntValue("id2"));
        inquiry.switchQuestionPositions(q1.getId(), q2.getId());
        viewQuestions(inquiry.loadQuestionnaire(q1.getParentId()));
        logger.debug("-");
    }

    private void onMoveOption() {
        logger.debug("+");
        Option o1 = inquiry.loadOption(req.getIntValue("id1"));
        Option o2 = inquiry.loadOption(req.getIntValue("id2"));
        inquiry.switchOptionPositions(o1.getId(), o2.getId());
        Question q = initQuestion(req);
        formQuestion(q);
        logger.debug("-");
    }

    private void viewStatistics(int quizId) {
        logger.debug("+ -");
        inquiry.buildXmlStatistics(model, quizId);
        view = VIEW_STATISTICS;
    }

    private void onDeleteRespondent(int respId) {
        logger.debug("+");
        Respondent resp = null;
        try {
            resp = inquiry.loadRespondent(respId);
            inquiry.deleteRespondent(respId);
        } catch (Exception e) {
            logger.error("Exception for id " + respId, e);
            addLocalizedError("CANNOT_DELETE_RESPONDENT");
        }
        viewRespondents(resp.getQuestionnaireId());
        logger.debug("-");
    }

    private void viewRespondent(int id) {
        logger.debug("+");
        inquiry.buildXmlRespondentWithAnswers(model, id);
        view = VIEW_RESPONDENT;
        logger.debug("-");
        
    }

    private void viewRespondents(int quizId) {
        logger.debug("+");
        Element qEl =
            XHelper.addXmlQuestionnaire(model, inquiry.loadQuestionnaire(quizId));
        for (Respondent r : inquiry.getRespondentsByQuestionnaireId(quizId))
            XHelper.addXmlRespondent(qEl, r);
        view = VIEW_RESPONDENTS;
        logger.debug("-");
    }

    private void onStoreOption() {
        logger.debug("+");
        Question q = initQuestion(req);
        boolean reset = resetQuestion(q);
        if (reset) {
        	int editableId = req.getIntValue("editableOptionId");
        	Option option = null;
        	String title = null;
        	if (editableId > 0) {
        		option = inquiry.loadOption(editableId);
        		title = req.getParameter("newOptionTitle");
        	} else {
        		option = new Option();
        		title = req.getParameter("optionTitle");
        	}
        	int parentId = req.getIntValue("id");
	        option.setParentId(parentId);
	        option.setTitle(title);
	        try {
	        	inquiry.storeOption(option);
				formQuestion(q);
		        logger.debug("-");
				return;
	        }
	        catch (Exception e) {
	    		logger.error("Exception for " + option, e);
	    		addException(e);
	    	}
        }
        formQuestion(q);
        logger.debug("-");
	}

    private Question initQuestion(RequestContext req) {
        int id = req.getIntValue("id");
		return initQuestion(id, req);
    }

    private Question initQuestion(int id, RequestContext req) {
        Question q = id > 0 ? inquiry.loadQuestion(id) : new Question();
        q.setParentId(req.getIntValue("parentId"));
        q.setTitle(req.getParameter("title"));
        q.setType(QuestionType.getTypeByName(req.getParameter("type")));
        q.setRequired( "on".equals(req.getParameter("required")) );
        q.setAllowRemark( "on".equals(req.getParameter("allowRemark")) );
        q.setAlternative(req.getParameter("alternative"));
		q.setExplanation(req.getParameter("explanation"));
		q.setOptionsLayout(req.getParameter("options_layout"));
		q.setAoHeight(req.getIntValue("ao_height"));
		q.setAoWidth(req.getIntValue("ao_width"));
		q.setAoMultiline( "on".equals(req.getParameter("ao_multiline")) );
		return q;
    }
    
    private void previewQuestionnaire() {
        logger.debug("+");
        Question q = initQuestion(req);
        resetQuestion(q);
        formQuestion(q);
        view = PREVIEW_QUESTIONNAIRE;
        logger.debug("-");
    }
    
    private boolean resetQuestion(Question q) {
        logger.debug("+");
        if (!validateQuestion(q)) {
            logger.warn("- validation failed for " + q);
            return false;
        }
        try {
            inquiry.storeQuestion(q);
            logger.debug("-");
            return true;
        } catch (DuplicateQuestionException e) {
        	addLocalizedError("QUESTION_TITLE_NOT_UNIQUE");
        } catch (Exception e) {
            logger.error("Exception for " + q, e);
            addException(e);
        }
        logger.warn("- error of saving");
        return false;
    }
        
    private void onStoreQuestion() {
        logger.debug("+");
        Question q = initQuestion(req);
        boolean reset = resetQuestion(q);
        if (reset) {
        	String optionTitle = req.getParameter("optionTitle");
			if (optionTitle == null && req.getIntValue("id") == 0) {
				formQuestion(q);
			} else {
				viewQuestions(inquiry.loadQuestionnaire(q.getParentId()));
			}
        } else {
			formQuestion(q);
        }
        logger.debug("-");
    }

    private boolean validateQuestion(Question q) {
        logger.debug("+");
        boolean ok = true;
        if(isEmpty(q.getTitle())) {
            ok = false;
            addLocalizedError("TITLE_IS_NOT_SET");
        }
        if(q.getParentId() < 1) {
            ok = false;
            addLocalizedError("QUESTIONNARE_IS_NOT_SPECIFIED");
        }
        logger.debug("-");
        return ok;
    }

    private void onStoreQuestionnaire() {
        logger.debug("+");
        int id = req.getIntValue("id");
        Questionnaire q =
        	id > 0 ? inquiry.loadQuestionnaire(id) : new Questionnaire();
        q.setLangId(req.getIntValue("langId"));
        q.setTitle(req.getString("title", null));
        q.setPublish(req.getSimpleRoundTimestamp("publish"));
        q.setExpired(req.getSimpleRoundTimestamp("expired"));
		q.setPublic("on".equals(req.getParameter("public")));
        q.setMultipage("on".equals(req.getParameter("multipage")));
        q.setRfMultiline("on".equals(req.getParameter("rf_multiline")));
		q.setRfHeight(req.getIntValue("rf_height"));
		q.setRfWidth(req.getIntValue("rf_width"));
        q.setShowAnswers("on".equals(req.getParameter("showAnswers")));
        if(!validateQuestionnaire(q)) {
            formQuestionnaire(q);
            logger.warn("- validation failed for " + q);
            return;
        }
        try {
            inquiry.storeQuestionnaire(q);
            //# XHelper.addXmlQuestionnaire(model, q);
            //# view = VIEW_QUESTIONNAIRE;
            viewQuestionnaireList();
            logger.debug("-");
            return;
        } catch (DuplicateQuestionnaireException e) {
        	addLocalizedError("QUESTIONNAIRE_TITLE_NOT_UNIQUE");
        } catch (Exception e) {
            logger.error("Exception for " + q, e);
            addException(e);
        }
        formQuestionnaire(q);
        logger.warn("- error of saving");
    }
    
    private Timestamp timestamp(String text) {
        logger.debug("+ -");
        if(text == null || text.equals("")) {
            logger.debug("- date not set");
            return null;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return new Timestamp(fmt.parse(text).getTime());
        } catch (ParseException e) {
        	addException(e);
            logger.error("- ParseException", e);
            return null;
        }
    }

    private boolean validateQuestionnaire(Questionnaire q) {
        logger.debug("+");
        boolean ok = true;
        if(isEmpty(q.getTitle())) {
            ok = false;
            addLocalizedError("TITLE_IS_NOT_SET");
        }
        if(q.getLangId() < 1) {
            ok = false;
            addLocalizedError("LANGUAGE_NOT_SPECIFIED");
        }
        logger.debug("-");
        return ok;
    }

    private void onDeleteOption() {
        logger.debug("+");
        int id = req.getIntValue("optionId");
        Option option = inquiry.loadOption(id);
        if(option == null) {
            viewQuestionnaireList();
            addLocalizedError("CANNOT_DELETE_OPTION");
            logger.debug("- Cannot delete option # " + id);
            return;
        }
        try {
            inquiry.deleteOption(id);
        } catch (Exception e) {
            logger.error("Exception for id " + id, e);
            addLocalizedError("CANNOT_DELETE_OPTION");
        }
        Question q = initQuestion(req);
        formQuestion(q);
        logger.debug("-");        
    }

    private void onDeleteQuestion() {
        logger.debug("+");
        int id = req.getIntValue("id");
        Question question = inquiry.loadQuestion(id);
        if(question == null) {
            viewQuestionnaireList();
            addLocalizedError("CANNOT_DELETE_QUESTION");
            logger.debug("- Cannot delete question # " + id);
            return;
        }
        int parentId = question.getParentId();
        try {
            inquiry.deleteQuestion(id);
        } catch (Exception e) {
            logger.error("Exception for id " + id, e);
            addLocalizedError("CANNOT_DELETE_QUESTION");
        }
        viewQuestions(inquiry.loadQuestionnaire(parentId));
        logger.debug("-");
    }

    private void onDeleteQuestionnaire() {
        logger.debug("+");
        int id = req.getIntValue("id");
        try {
            inquiry.deleteQuestionnaire(id);
        } catch (Exception e) {
            logger.error("Exception for id " + id, e);
            addLocalizedError("CANNOT_DELETE_QUESTIONNAIRE");
        }
        viewQuestionnaireList();
        logger.debug("-");
    }
    
    /** View questionnaire list as "{questionnaire*}" */
    private void viewQuestionnaireList() {
        logger.debug("+ -");
		List<Questionnaire> questionnaires =
            inquiry.getQuestionnaireList(pageLanguage);
        for (Questionnaire questionnaire : questionnaires)
            XHelper.addXmlQuestionnaire(model, questionnaire);
        int limit = Env.getIntProperty("inquiry_module.questionnaire_limit", 10);
		model.setAttribute("qLimit", "" + (questionnaires.size() >= limit));
        model.setAttribute("qMaxLimit", "" + limit);
        XHelper.addXmlArticle(
            model, "invitation", inquiry.getInquiryInvitation(pageLanguage));
        XHelper.addXmlArticle(
            model, "qreminder", inquiry.getInquiryReminder(pageLanguage));
        XHelper.addXmlArticle(
            model, "preminder", inquiry.getPasswordReminder(pageLanguage));
        view = VIEW_QUESTIONNAIRE_LIST;
    }
	
    private void onCopyQuestionnaire() {
        logger.debug("+");
		int id = req.getIntValue("id");
		String title = req.getParameter("title");
		logger.debug("id=" + id + ", title=" + title);
    	try {
        	inquiry.copyQuestionnaire(id, title);
    	} catch (Exception e) {
            logger.error("Exception copying of questionnaire", e);
            addException(e);
    	}
    	viewQuestionnaireList();
        logger.debug("-");
    }

    private void onCopyQuestion() {
        logger.debug("+");
		int id = req.getIntValue("id");
		String title = req.getParameter("title");
		logger.debug("id=" + id + ", title=" + title);
    	try {
        	inquiry.copyQuestion(id, title);
    	} catch (Exception e) {
            logger.error("Exception copying of question", e);
            addException(e);
    	}
    	Question question = inquiry.loadQuestion(id);
    	Questionnaire questionnaire = inquiry.loadQuestionnaire(question.getParentId());
    	viewQuestions(questionnaire);
        logger.debug("-");
    }
    
    private void viewQuestions(Questionnaire q) {
        logger.debug("+ -");
        Element qEl = XHelper.addXmlQuestionnaire(model, q);
        List<Question> questions =
            inquiry.getQuestionsByQuestionnaireId(q.getId());
        for (Question question : questions)
            XHelper.addXmlQuestion(qEl, question);
        view = VIEW_QUESTIONS;
    }
    
    private void formQuestionnaire(Questionnaire q) {
        logger.debug("+");
        if(q == null) {
            if(req.getIntValue("id") > 0) {
                q = inquiry.loadQuestionnaire(req.getIntValue("id"));
            } else {
            	q = new Questionnaire();
            	q.setLangId(pageLanguage.getId().intValue());
            	inquiry.storeQuestionnaire(q);
			}
        }
        XHelper.addXmlQuestionnaire(model, q);
        view = FORM_QUESTIONNAIRE;
        logger.debug("-");
    }
    
    private void formQuestion(Question question) {
        logger.debug("+ -");
        if(question == null) {
            if(req.getIntValue("id") > 0) {
                question = inquiry.loadQuestion(req.getIntValue("id"));
            } else {
                question = new Question();
                question.setParentId(req.getIntValue("parentId"));
            }
        }
        Questionnaire quiz = inquiry.loadQuestionnaire(question.getParentId());
        Element elQuiz = XHelper.addXmlQuestionnaire(model, quiz);
        Element elQuestion = XHelper.addXmlQuestion(elQuiz, question);
        List<Option> options = inquiry.getOptionsByQuestionId(question.getId());
        for (Option option : options) XHelper.addXmlOption(elQuestion, option);
		try {
	        String lang = pageLanguage.getCode();
	        String url = PageService.getInstance().findByClass("inquiry", pageLanguage.getId()).getFilename();
			Xbuilder.setAttrForce(elQuiz, "previewUrl", url);
			Xbuilder.setAttrForce(elQuiz, "inquiry_question_id", question.getId());
		} catch (Exception e) {
			logger.error("Exception by getting of filename from database", e);
		}
        view = FORM_QUESTION;
    }
    
    private void redirectQuestion() {
        logger.debug("+");
        Question q = initQuestion(req);
    	formQuestion(q);
        logger.debug("-");
    }
    
    private void viewUsers(Questionnaire q) {
        logger.debug("+ -");
        Element qEl = XHelper.addXmlQuestionnaire(model, q);
        for (User user : inquiry.getUsersByQuestionnaireId(q.getId())) {
            XHelper.addXmlUser(qEl, user);
        }
        view = VIEW_USERS;
    }

    private void onStoreUser() {
        logger.debug("+");
        int id = req.getIntValue("id");
        User user = id > 0 ? inquiry.loadUser(id) : new User();
		user.setEmail(strip(req.getString("user_email", null)));
		user.setPassword(strip(req.getString("user_password", null)));
        int quizId = req.getIntValue("questionnaire_id");
		user.setQuestionnaireId(quizId);
		try {
			inquiry.storeUser(user);
            if ("on".equals(req.getParameter("shouldInvite"))) {
                String lang = pageLanguage.getCode();
                String page = PageService.getInstance().findByClass("inquiry", pageLanguage.getId()).getFilename();
                Article invitation = inquiry.getInquiryInvitation(pageLanguage);
                Questionnaire quiz = inquiry.loadQuestionnaire(quizId);
                send(quiz, user, invitation, page);
            }
			viewUsers(inquiry.loadQuestionnaire(user.getQuestionnaireId()));
			logger.debug("-");
			return;
		} catch (EmailNotValidException e) {
			addLocalizedError("EMAIL_IS_NOT_VALID_");
		} catch (EmailNotUniqueException e) {
			addLocalizedError("USER_NOT_UNIQUE");
		} catch (Exception e) {
			logger.error("Exception for " + user, e);
			addException(e);
		}
		formUser(user);
		logger.warn("- error of saving");
    }
	
    private void formUser(User user) {
        logger.debug("+");
        if(user == null) {
            if(req.getIntValue("id") > 0) {
            	user = inquiry.loadUser(req.getIntValue("id"));
            } else {
            	user = new User();
            	user.setQuestionnaireId(req.getIntValue("questionnaire_id"));
            }
        }
        Questionnaire quiz = inquiry.loadQuestionnaire(user.getQuestionnaireId());
        Element elQuiz = XHelper.addXmlQuestionnaire(model, quiz);
        XHelper.addXmlUser(elQuiz, user);
        view = FORM_USER;
        logger.debug("-");
	}

	private void onDeleteUser() {
        logger.debug("+");
        int id = req.getIntValue("id");
        User user = inquiry.loadUser(id);
        if(user == null) {
            viewQuestionnaireList();
            addLocalizedError("CANNOT_DELETE_USER");
            logger.debug("- Cannot delete option # " + id);
            return;
        }
        int quizId = user.getQuestionnaireId();
        try {
            inquiry.deleteUser(id);
        } catch (Exception e) {
            logger.error("Exception for id " + id, e);
            addLocalizedError("CANNOT_DELETE_USER");
        }
        viewUsers(inquiry.loadQuestionnaire(quizId));
        logger.debug("-");        
    }
    
    private void onImportUsers() throws Exception {
    	logger.debug("+");
		int quizId = req.getIntValue("questionnaire_id");
        Questionnaire quiz = inquiry.loadQuestionnaire(quizId);
        String page =
        	PageService.getInstance().findByClass("inquiry", pageLanguage.getId()).getFilename();
		InputStream in = null;
		try {
			in = req.getFile("import_file").getInputStream();
			String[] emails = split(IOUtils.toString(in, "ISO8859_1"), ";\r\n");
            Article invitation = inquiry.getInquiryInvitation(pageLanguage);
			for (String email : emails) {
                email = strip(email);
				try {
					User user = new User();
					user.setEmail(email);
					user.setQuestionnaireId(quizId);
					inquiry.storeUser(user);
                    if (!"on".equals(req.getParameter("shouldInvite"))) {
                        continue;
                    }
                    send(quiz, user, invitation, page);
				} catch (EmailNotValidException e) {
                    addLocalizedError("EMAIL_IS_NOT_VALID_", email);
				} catch (EmailNotUniqueException e) {
					addLocalizedError("USER_NOT_UNIQUE", email);
				} catch (MessagingException e) {
                    addLocalizedError("CANNOT_SEND_EMAIL", email);
                }
			}
		} finally {
			IOUtils.closeQuietly(in);
		}
        viewUsers(quiz);
    	logger.debug("-");
    }
	
	private void onStoreInvitationSubject() throws Exception {
		logger.debug("+");
        Article article = inquiry.getInquiryInvitation(pageLanguage);
        article.setHead(req.getParameter("emailSubject"));
        article.update();
		viewQuestionnaireList();
		logger.debug("-");
	}
	
	private void onStoreQuestionnaireReminderSubject() throws Exception {
		logger.debug("+");
        Article article = inquiry.getInquiryReminder(pageLanguage);
        article.setHead(req.getParameter("emailSubject"));
        article.update();
		viewQuestionnaireList();
		logger.debug("-");
	}
	
	private void onStorePasswordReminderSubject() throws Exception {
		logger.debug("+");
        Article article = inquiry.getPasswordReminder(pageLanguage);
        article.setHead(req.getParameter("emailSubject"));
        article.update();
		viewQuestionnaireList();
		logger.debug("-");
	}
	
	private void onSendPasswordReminder() throws Exception {
		logger.debug("+");
        String[] ids = split(req.getParameter("remind_ids"), ",");
        Article reminder = inquiry.getPasswordReminder(pageLanguage);
        String page =
        	PageService.getInstance().findByClass("inquiry", pageLanguage.getId()).getFilename();
		int quizId = req.getIntValue("questionnaire_id");
        Questionnaire quiz = inquiry.loadQuestionnaire(quizId);
        for (String id: ids) {
            User user = inquiry.loadUser(NumberUtils.stringToInt(id));
            if (user == null) {
                addLocalizedError("FAILED_USER_ID", id);
                continue;
            }
            try {
                send(quiz, user, reminder, page);
            } catch (MessagingException e) {
                logger.error("Exception", e);
                addLocalizedError("CANNOT_SEND_EMAIL", user.getEmail());
            }
        }
        viewUsers(quiz);
		logger.debug("-");
	}
	
	private void onSendQuestionaireReminder() throws Exception{
		logger.debug("+");
		int quizId = req.getIntValue("id");
        Questionnaire quiz = inquiry.loadQuestionnaire(quizId);
        Article reminder = inquiry.getInquiryReminder(pageLanguage);
        String page =
        	PageService.getInstance().findByClass("inquiry", pageLanguage.getId()).getFilename();
        List<User> users = inquiry.getLazyUsers(quizId);
        for (User user : users) {
            try {
                send(quiz, user, reminder, page);
            } catch (MessagingException e) {
                logger.error("Exception", e);
                addLocalizedError("CANNOT_SEND_EMAIL", user.getEmail());
            }
        }
		viewQuestionnaireList();
		logger.debug("-");
	}

    private void send(Questionnaire quiz, User u, Article article, String page)
    throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setRecipients(Message.RecipientType.TO, u.getEmail());
        msg.setFrom(FROM);
        msg.setSubject(article.getHead(), "UTF-8");
        String link =
            Env.getHostName() + page + "?inquiry_questionnaire_id=" + 11111111111111L/*quiz.getId()*/;
        String body =
            article.getText() + 
            "<br>" + findEntry("QUESTIONNAIRE", i18nLangCode) + ": " +
            "<a href='" + link + "'>" + quiz.getTitle() + "</a>" +
            "<br>" + findEntry("EMAIL", i18nLangCode) + ": " + u.getEmail() +
            "<br>" + findEntry("PASSWORD", i18nLangCode) + ": " + u.getPassword();
        msg.setContent(body,"text/html; charset=UTF-8");
        Transport.send(msg);
    }

    private void addLocalizedError(String messageKey) {
        addLocalizedError(messageKey, null);
    }

    private void addLocalizedError(String messageKey, String extraInfo) {
        String msg = findEntry(messageKey, i18nLangCode);
        errors.add(
                (msg == null ? messageKey : msg) +
                (extraInfo == null ? "" : ": " + extraInfo) );
    }
	
	private void addException(Exception e) {
		errors.add(e.getMessage());
		if(e.getCause() != null) errors.add(e.getCause().getMessage());
	}

}
