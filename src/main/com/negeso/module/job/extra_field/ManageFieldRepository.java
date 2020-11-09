/*
 * @(#)$Id: ManageFieldRepository.java,v 1.3, 2005-06-06 13:04:48Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.extra_field;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.job.command.ManageJobs;
import com.negeso.module.job.domain.JobString;

/**
 *
 * Field repository
 * 
 * @version		$Revision: 4$
 * @author		Olexiy Strashko
 * 
 */
public class ManageFieldRepository extends AbstractCommand {
    private static Logger logger = Logger.getLogger( ManageJobs.class );
    private static final String RESULT_TYPE_CHOOSER = "field-type-chooser";
    private static final String RESULT_TYPE_LIST = "field-type-list";
    private static final String RESULT_TYPE_DETAILS = "field-type-details";
    
    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = this.getRequestContext();
        ResponseContext response = new ResponseContext();
        
        if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
            response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
            return response;
        }
        
        Timer timer = new Timer();
        timer.start();
        
        Connection con = null;
        String action = request.getNonblankParameter( "action" );
        logger.info("action: " + action);
        Element page = null;
        try{
            page = XmlHelper.createPageElement( request );
            con = DBHelper.getConnection();
            Long langId = request.getSession().getLanguage().getId();

            if ( "select_field".equals(action) ){
            	this.renderFields(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "select-field-type");
                response.setResultName( RESULT_TYPE_CHOOSER );
            }
            else if ( "field_details".equals(action) ){
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "edit-field-type");
                response.setResultName( RESULT_TYPE_DETAILS );
            }
            else if ( "add_field".equals(action) ){
            	this.doAddField(con, request, page, langId);
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "edit-field-type");
                response.setResultName( RESULT_TYPE_DETAILS );
            }
            else if ( "add_option".equals(action) ){
            	this.doAddOption(con, request, page, langId);
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "edit-field-type");
                response.setResultName( RESULT_TYPE_DETAILS );
            }
            else if ( "remove_option".equals(action) ){
            	this.doRemoveOption(con, request, page, langId);
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "edit-field-type");
                response.setResultName( RESULT_TYPE_DETAILS );
            }
            else if ( "delete_field".equals(action) ){
            	this.doRemoveField(con, request, page, langId);
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "type-list");
                response.setResultName(RESULT_TYPE_LIST);
            }
            else if ( "save_field".equals(action) ){
            	this.doSaveField(con, request, page, langId);
            	this.renderFieldDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "edit-field-type");
                response.setResultName(RESULT_TYPE_DETAILS);
            }
            else{
            	this.renderFields(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "type-list");
                response.setResultName(RESULT_TYPE_LIST);
            }
        }
        catch( SQLException e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
        }
        catch( CriticalException e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
        }
        finally{
            DBHelper.close(con);
        }
        
        if ( page != null ){
            response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
        }
        logger.info("time:" + timer.stop());
        
        logger.debug("-");
        return response;
    }

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void doSaveField(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
        Long fieldId = request.getLong("field_id");
        if ( fieldId == null ){
            logger.error("ERROR!!! fieldId is null");
            return;
        }

        Long fLangId = request.getLong("field_lang_id");
        
        String title = request.getNonblankParameter("title");

        FieldType type = FieldRepository.get().getType(con, fieldId);
        if ( type == null ){
            logger.error("ERROR!!! type not found by id:" + fieldId);
            return;
        }
        
        String optionTitle = null;
        Long defaultOptionId  = request.getLong("default");
        for (FieldOption option: type.getOptions(con)){
        	optionTitle = request.getNonblankParameter("option_" + option.getId());
        	option.setTitle(con, optionTitle, fLangId);
        	if ( option.isDefault() ){
        		if ( !option.getId().equals(defaultOptionId) ){
            		option.setDefault(false);
        		}
        	}
        	if ( option.getId().equals(defaultOptionId) ){
        		option.setDefault(true);
        	}
        	option.update(con);
        }
        
        type.setExpired(true);
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void doRemoveField(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
        Long fieldId = request.getLong("field_id");
        if ( fieldId == null ){
            logger.error("ERROR!!! fieldId is null");
            return;
        }
        
        FieldType type = FieldRepository.get().getType(con, fieldId);
        if ( type == null ){
            logger.error("ERROR!!! type not found by id:" + fieldId);
            return;
        }
        type.delete(con);
        type.setExpired(true);
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void doRemoveOption(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
        Long optionId = request.getLong("option_id");
        if ( optionId == null ){
            logger.error("ERROR!!! optionId is null");
            return;
        }
        
        FieldOption option = FieldOption.findById(con, optionId);  
        if ( option == null ){
            logger.error("ERROR!!! option not found by id:" + optionId);
            return;
        }
        
        FieldType type = FieldRepository.get().getType(con, option.getFieldTypeId());
        if ( type == null ){
            logger.error("ERROR!!! type not found by id:" + option.getFieldTypeId());
            return;
        }
        option.delete(con);
        type.setExpired(true);
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void doAddOption(
		Connection con, RequestContext request, Element page, Long langId)
		throws CriticalException 
	{
        Long typeId = request.getLong("type_id");
        if ( typeId == null ){
            logger.error("ERROR!!! typeId is null");
            return;
        }
        FieldType type = FieldRepository.get().getType(con, typeId);
        if ( type == null ){
            logger.error("ERROR!!! type not found by id:" + typeId);
            return;
        }
        
        FieldOption option = new FieldOption();
        option.setFieldTypeId(type.getId());
        
        if ( request.getNonblankParameter("is_default") != null ){
            option.setDefault(true);
        }


        Long titleId = JobString.getNextId(con);
        option.setTitleId(titleId);
        String title = request.getNonblankParameter("title");
        if ( title == null ){
        	logger.error("title is NULL!!!");
        	return;
        }
        JobString.createValues(con, titleId, title);
        type.setExpired(true);
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void doAddField(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
        FieldType fType = new FieldType();
        String type = request.getNonblankParameter("type");
        fType.setType(type);
        
        String title = request.getNonblankParameter("type");
        if ( title == null ){
        	logger.error("title is NULL!!!");
        	return;
        }

        Long titleId = JobString.getNextId(con);
        JobString.createValues(con, titleId, title);

        fType.insert(con);
        request.setParameter("type_id", fType.getId().toString());
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void renderFieldDetails(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
        Long typeId = request.getLong("type_id");
        if ( typeId == null ){
            logger.error("ERROR!!! typeId is null");
            return;
        }
        FieldType type = FieldRepository.get().getType(con, typeId);
        if ( type == null ){
            logger.error("ERROR!!! type not found by id:" + typeId);
            return;
        }

        Long fLangId = request.getLong("field_lang_id");

        TypeXmlBuilder.buildTypeXml(con, page, type, fLangId);

		Element langs = Language.getDomItems( page.getOwnerDocument(), fLangId );
		page.appendChild(langs);
	}

	/**
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException 
	 */
	private void renderFields(
		Connection con, RequestContext request, Element page, Long langId) 
		throws CriticalException 
	{
		TypeXmlBuilder.buildTypesXml(con, page, langId);
	}
}
