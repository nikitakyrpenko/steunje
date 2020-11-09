/*
 * @(#)$Id: InquiryComponent.java,v 1.9, 2006-06-01 13:46:54Z, Anatoliy Pererva$
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.Xquery;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.inquiry.domain.Questionnaire;
import com.negeso.module.inquiry.domain.User;

/**
 * This component is responsible for all "Inquiry module" interactions with
 * respondents answer quizes. 
 *
 * @version     $Revision: 10$
 * @author      Stanislav Demchenko
 */
public class InquiryComponent implements PageComponent {
    
    private static Logger logger = Logger.getLogger(InquiryComponent.class);
    
    private Inquiry inquiry = Inquiry.getInstance();
    
    private Element model = null;

    private RequestContext req;
    
    /**
     * /negeso:inquiry/@status =
     *  questionnaire_list  ok: user has not yet selected a questionnaire
     *  incomplete          error: user missed some obligatory question
     *  complete            ok: user has answered all questions
     *  storage_error       error: duplicate submission or a db error
     *  blank               
     *  
     * /negeso:inquiry/@blocked = absent or "true"
     * 
     */
    public Element getElement(Document doc, RequestContext req, Map params) {
        logger.debug("+");
        this.req = req;
        model = Xbuilder.createEl(doc, "inquiry", null);
        int quizId = req.getIntValue("inquiry_questionnaire_id");
        if((quizId == 0)) {
            Xbuilder.setAttr(model, "status", "questionnaire_list");
            List<Questionnaire> list =
                inquiry.getActiveQuestionnaires(req.getSession().getLanguage());
            for(Questionnaire q : list) XHelper.addXmlQuestionnaire(model, q);
            logger.debug("- return list of questionnaires");
            return model;
        }
        if( req.getParameter("inquiry_email") != null) {
        	User u = inquiry.loadUser(
        			req.getParameter("inquiry_email"),
        			req.getParameter("inquiry_password"),
        			quizId);
        	if (u == null)  Xbuilder.setAttr(model, "status", "login_error");
        	else setSessionUser(u);
        }
        Xbuilder.setAttr(model, "status", "blank");
        Questionnaire quiz = inquiry.loadQuestionnaire(quizId);
        if (getSessionUser() != null && !quiz.isPublic()) {
            XHelper.addXmlUser(model, getSessionUser());
        }
        if (!authorized(quiz)) {
            model.setAttribute("status", "access_denied");
            model.setAttribute("quiz_id", ""+quizId);
            logger.debug("- access denied");
            return model;
        }
        if (new RespondentController(model, quiz, req).execute()) {
            persistModel();
        }
        logger.debug("-");
        return model;
    }
    
    private boolean authorized(Questionnaire quiz) {
        logger.debug("+");
        if (quiz.isPublic()) {
            logger.debug("- authorization not needed");
            return true;
        }
        if (getSessionUser() == null) {
            logger.debug("- no user in session");
            return false;
        }
        logger.debug("- check to which quiz the user belongs");
        return getSessionUser().getQuestionnaireId() == quiz.getId();
    }

    private void persistModel() {
        logger.debug("+");
        Xquery.elem(model, ".//negeso:questionnaire")
            .setAttribute("address", req.getRemoteAddr());
        try {
            inquiry.storeRespondent(model);
            Xbuilder.setAttr(model, "status", "complete");
        } catch (DuplicateRespondentException e) {
            Xbuilder.setAttr(model, "status", "storage_error");
            Xbuilder.setAttr(model, "blocked", "true");
        } catch (Exception e) {
            logger.error("Exception", e);
            Xbuilder.setAttr(model, "status", "storage_error");
        }
        logger.debug("-");
    }

	
    private User getSessionUser() {
        return (User)req.getSession().getAttribute("inq_user");
    }
    
    private void setSessionUser(User user) {
        req.getSession().setAttribute("inq_user", user);
    }

}