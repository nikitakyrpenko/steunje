/*
 * Created on 24.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.form_manager.command;

import static com.negeso.module.dictionary.DictionaryUtil.findEntry;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.form_manager.FormManagerException;
import com.negeso.module.form_manager.FormXmlBuilder;
import com.negeso.module.form_manager.UniqueNameViolationException;
import com.negeso.module.form_manager.domain.Forms;
import com.negeso.module.form_manager.service.FormService;
/**
 * @author Shkabi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */ 

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageFormsCommand extends AbstractCommand{
    
	private static Logger logger = Logger.getLogger(ManageFormsCommand.class);
    
    public static final String OUTPUT_DOCUMENT = "xml";
   
    @SuppressWarnings("unchecked")
	@ActiveModuleRequired
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
       
        try {
            if (!SecurityGuard.canManage(request.getSession().getUser(),null)) {
                response.setResultName(RESULT_ACCESS_DENIED);
                logger.debug("- Access denied");
                return response;
            }
            Element parentElement = Xbuilder.createTopEl("form_manager");
            String formLanguage = request.getParameter("form_language");
            if ( formLanguage == null )
            {
                formLanguage = request.getSession().getLanguageCode();  
            }
            parentElement.setAttribute("lang", formLanguage);
            Connection conn = null;
            try {
                conn = DBHelper.getConnection();
                Statement statement = conn.createStatement();
                
                if ( request.getParameter("todo")!=null )
                {
                    if ( request.getParameter("todo").equals("delete")
                            && request.getParameter("form_id")!= null)
                    {   /*Deletes the specified form and shows the form list*/
                        //logger.error("**todo=delete");
                        deleteForm(getRequestContext().getParameter("form_id"));
                        showForms(parentElement, statement);
                    }
                    else if ( request.getParameter("todo").equals("edit")
                            && request.getParameter("form_id")!= null )
                    {   /*Editing the specified form*/
                        //logger.error("**todo=edit");
                        editForm(parentElement, conn, statement,
                                request.getParameter("form_id"));
                        parentElement.setAttribute("mode", "edit_form");
                        parentElement.setAttribute("back_link", 
                            "?command=manage-forms&form_language=" + formLanguage);
                    }
                    else if ( request.getParameter("todo").equals("save")
                            && request.getParameter("form_id")!= null )
                    {   /*Saving and editing the specified form*/
                        //logger.error("**todo=save");
                    	try {
                    	    updateForm();
                    	} catch (UniqueNameViolationException e) {
                    		Xbuilder.addEl(parentElement, "error",
                  				       getLocalizedError("UNIQUE_FORM_NAME_CONSTRAINT_VIOLATION", request));
                    	}
                        Article.UpdateArticlesWithForm(
                                conn, 
                                new Long(request.getParameter("article_id")));
                        editForm(parentElement, conn, statement,
                                request.getParameter("form_id"));
                        
                        if (request.getParameter("close").equals("close")){
                        	parentElement.setAttribute("mode", "show_form");
                        	showForms(parentElement, statement);
                        }
                        else{
                        	parentElement.setAttribute("mode", "edit_form");           
                        	parentElement.setAttribute("back_link", 
                            		"?command=manage-forms&form_language=" + formLanguage);
                        }                        
                    }
                    else if ( request.getParameter("todo").equals("add") )
                    {   /*Adding and editing the form*/
                        //logger.error("**todo=add");
                         editForm(parentElement, conn, statement, 
                        		 addForm(conn, statement, formLanguage));
                        parentElement.setAttribute("mode", "edit_form");
                        parentElement.setAttribute("back_link", 
                           "?command=manage-forms&form_language=" + formLanguage);
                    }
                    else/*Shows the form list*/
                    {
                        //logger.error("**todo!=null");
                        showForms(parentElement, statement);  
                    }
                }
                else/*Shows the form list*/
                {
                    //logger.error("**todo=null");
                    showForms(parentElement, statement);
                }
                statement.close();
            } catch (Exception e) {
                logger.error("Exception", e);
            } finally {
                DBHelper.close(conn);
            }
            
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, parentElement.getOwnerDocument());
            response.setResultName(RESULT_SUCCESS);
            RequestUtil.getHistoryStack((HttpServletRequest)request.getIOParameters()
                    .get(WebFrontController.HTTP_SERVLET_REQUEST)).push(new Link("Form manager",
    				"/admin/?command=manage-forms&form_language=" + formLanguage
    				, false));
            logger.debug("-");
            
        }
        catch (Exception e) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", e);
        }
        return response;
    }
    
    private String getLocalizedError(String errorMsg,
    		                         RequestContext req) {
        return findEntry(errorMsg, req.getSession().getInterfaceLanguageCode());
	}

    public void showForms(
            Element parentElement, 
            Statement statement)
        throws SQLException
    {
        FormXmlBuilder.buildLanguages(parentElement, statement, 
                getRequestContext());
        FormXmlBuilder.buildForms(parentElement, statement, 
                getRequestContext());
        parentElement.setAttribute("mode", "show_form");
    }
    
    public void deleteForm(String formId)
        throws FormManagerException, CriticalException, 
               ObjectNotFoundException, SQLException
    {
    	getFormService().deleteForm(new Long(formId));
    }
    
    public void updateForm()
        throws SQLException, NumberFormatException, 
               ObjectNotFoundException, 
               FormManagerException, UniqueNameViolationException
    {
    	RequestContext request = getRequestContext();
    	Forms form = getFormService().findById(new Long(request.getParameter("form_id")));            
	    form.setName(request.getParameter("name"));
	    form.setEmail(request.getParameter("email"));
		form.setEx(request.getParameter("ex"));
	    form.setDescription(request.getParameter("description"));
	    form.setFormId(request.getParameter("form_id"));
	    form.setPageId(request.getLong("page_id"));
	    getFormService().updateForm(form);
    }       
    
    public String addForm(
            Connection connection, 
            Statement statement, 
            String formLanguage)
        throws SQLException, FormManagerException, CriticalException, ObjectNotFoundException
    { 
    	return getFormService().addForm(formLanguage);
    }
    
    
    
    public void editForm(
            Element parentElement,
            Connection connection,
            Statement statement,
            String ID)
        throws SQLException, CriticalException, ObjectNotFoundException, 
            FormManagerException
    {
        FormXmlBuilder.buildForm(
            parentElement, connection, statement, getRequestContext(), ID);
    }
    
    @Override
	public String getModuleName() {
		return ModuleConstants.FORM_MANAGER;
	}
    
    public FormService getFormService(){
    	return (FormService) DispatchersContainer
		.getInstance().getBean("form_manager", "formService");

    }
}


