/*
 * @(#)$Id: XHelper.java,v 1.7, 2006-06-01 13:46:56Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Article;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.inquiry.domain.Option;
import com.negeso.module.inquiry.domain.Question;
import com.negeso.module.inquiry.domain.Questionnaire;
import com.negeso.module.inquiry.domain.Respondent;
import com.negeso.module.inquiry.domain.User;
import com.negeso.framework.list.generators.XmlHelper;

/**
 * 
 *
 * @version     $Revision: 8$
 * @author      Stanislav Demchenko
 */
public class XHelper {
    
    private static Logger logger = Logger.getLogger(XHelper.class);
    
    public static Element addXmlQuestionnaire(Element parent, Questionnaire q) {
        logger.debug("+ -");
        Element elQ = Xbuilder.addEl(parent, "questionnaire", null);
		Xbuilder.setAttrForce(elQ, "id", "" + q.getId());
        Xbuilder.setAttrForce(elQ, "langId", "" + q.getLangId());
        Xbuilder.setAttrForce(elQ, "title", q.getTitle());
        XmlHelper.setTimeStampAttribute(elQ, "publish", q.getPublish());
        XmlHelper.setTimeStampAttribute(elQ, "expired", q.getExpired());
		Xbuilder.setAttr(elQ, "public", ""+q.isPublic() );
		Xbuilder.setAttr(elQ, "multipage", ""+q.isMultipage() );
		Xbuilder.setAttr(elQ, "rfHeight", ""+q.getRfHeight());
		Xbuilder.setAttr(elQ, "rfWidth", ""+q.getRfWidth());
		Xbuilder.setAttr(elQ, "rfMultiline", ""+q.isRfMultiline());
		Xbuilder.setAttr(elQ, "showAnswers", ""+q.isShowAnswers());
		try {
			Article art = Article.findById(new Long(q.getIntroId()));
			Element el = Xbuilder.addEl(elQ, "intro", art.getText());
			Xbuilder.setAttr(el, "id", art.getId());
			
			art = Article.findById(new Long(q.getConclusionId()));
			el = Xbuilder.addEl(elQ, "conclusion", art.getText());
			Xbuilder.setAttr(el, "id", art.getId());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
        return elQ;
    }
    
    public static Element addXmlQuestion(Element parent, Question q) {
        logger.debug("+ -");
        Element elQ = Xbuilder.addEl(parent, "question", null);
        Xbuilder.setAttrForce(elQ, "id", "" + q.getId());
        Xbuilder.setAttrForce(elQ, "parentId", "" + q.getParentId());
        Xbuilder.setAttrForce(elQ, "title", q.getTitle());
        Xbuilder.setAttrForce(elQ, "type", q.getType().getName());
        Xbuilder.setAttrForce(elQ, "required", "" + q.getRequired());
        Xbuilder.setAttrForce(elQ, "allowRemark", "" + q.getAllowRemark());
        Xbuilder.setAttrForce(elQ, "alternative", q.getAlternative());
		Xbuilder.setAttrForce(elQ, "explanation", q.getExplanation());
		Xbuilder.setAttrForce(elQ, "options_layout", q.getOptionsLayout());
		Xbuilder.setAttrForce(elQ, "position", "" + q.getPosition());
		elQ.setAttribute("aoHeight", ""+q.getAoHeight());
		elQ.setAttribute("aoWidth", ""+q.getAoWidth());
		elQ.setAttribute("aoMultiline", ""+q.isAoMultiline());
        return elQ;
    }
    
    public static Element addXmlOption(Element parent, Option opt) {
        logger.debug("+ -");
        Element elOpt = Xbuilder.addEl(parent, "option", null);
        Xbuilder.setAttrForce(elOpt, "id", "" + opt.getId());
        Xbuilder.setAttrForce(elOpt, "parentId", "" + opt.getParentId());
        Xbuilder.setAttrForce(elOpt, "title", opt.getTitle());
        return elOpt;
    }

    public static Element addXmlRespondent(Element parent, Respondent resp) {
        logger.debug("+ -");
        Element elResp = Xbuilder.addEl(parent, "respondent", null);
        Xbuilder.setAttrForce(elResp, "id", "" + resp.getId());
        Xbuilder.setAttrForce(elResp, "parentId", "" + resp.getQuestionnaireId());
        Xbuilder.setAttrForce(elResp, "userId", "" + resp.getUserId());
        Xbuilder.setAttrForce(elResp, "email", resp.getEmail());
        Xbuilder.setAttrForce(elResp, "address", resp.getIpAddress());
        Xbuilder.setAttrForce(elResp, "submitTime", resp.getSubmitTime());
        return elResp;
    }
    
    public static Element addXmlUser(Element parent, User user) {
        logger.debug("+ -");
        Element elUser = Xbuilder.addEl(parent, "inquiry_user", null);
        elUser.setAttribute("id", "" + user.getId());
        elUser.setAttribute("email", user.getEmail());
        elUser.setAttribute("password", user.getPassword());
        int quizId = user.getQuestionnaireId();
		elUser.setAttribute("questionnaire_id", quizId == 0 ? "" : "" + quizId);
        return elUser;
    }
    
    public static Element addXmlArticle(
            Element parent, String childElementName, Article article) {
        logger.debug("+ -");
        Element elArticle = Xbuilder.addEl(parent, childElementName, null);
        Xbuilder.setAttrForce(elArticle, "id", "" + article.getId());
        Xbuilder.setAttrForce(elArticle, "head", "" + article.getHead());
        Xbuilder.setAttrForce(elArticle, "text", article.getText());
        return elArticle;
    }
    
}