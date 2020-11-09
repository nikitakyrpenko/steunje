package com.negeso.framework.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;

/**
 * 
 * Prepared xml controller. Contains:
 * - prepared xml page model
 * - request/response,
 * - JDBC connection 
 * - default autoTransaction support(overridable)
 * - default Contributor security check (overridable)
 * 	
 * @author Olexiy Strashko
 *
 */
abstract public class PreparedXmlController extends AbstractCommand {

	private static Logger logger = Logger.getLogger(PreparedXmlController.class);
	
	Element model = null;
    ResponseContext response = null;
    SessionData session = null;
    Connection connection = null;
    int langId = 0;
    
	public boolean isAutoTransaction(){
		return true;
	}

	public boolean hasContributorCheck(){
		return true;
	}

	abstract public void dispatch();

	public ResponseContext execute() {
        logger.debug("+");
        this.setResponse(new ResponseContext());
        this.setSession(this.getRequest().getSession());
        this.setLangId(this.getRequest().getSession().getLanguage().getId().intValue());
        if (this.hasContributorCheck()){
        	if (!SecurityGuard.isContributor(this.getSession().getUser())){
        		this.setView(RESULT_ACCESS_DENIED);
        		return this.getResponse();
        	}
        }

        this.setModel(XmlHelper.createPageElement( this.getRequest() ));
        try{
        	this.setConnection(DBHelper.getConnection());
        	if (this.isAutoTransaction()){
        		this.getConnection().setAutoCommit(false);
        	}
        	this.dispatch();
        	if (this.isAutoTransaction()){
        		this.getConnection().commit();
        	}
        }
        catch(SQLException e){
        	logger.error("-error");
        	if (this.isAutoTransaction()){
        		DBHelper.rollback(this.getConnection());
        	}
        }
        finally{
        	DBHelper.close(this.getConnection());
        }
        this.getResponse().getResultMap().put( 
        	OUTPUT_XML, this.getModel().getOwnerDocument() 
        );
        logger.debug("-");
		return this.getResponse();
	}


	public RequestContext getRequest(){
		return this.getRequestContext();
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getLangId() {
		return langId;
	}

	public void setLangId(int langId) {
		this.langId = langId;
	}

	public Element getModel() {
		return model;
	}

	public void setModel(Element model) {
		this.model = model;
	}

	public ResponseContext getResponse() {
		return response;
	}

	public void setResponse(ResponseContext response) {
		this.response = response;
	}

	public void setView(String viewName) {
		this.response.setResultName(viewName);
	}

	public SessionData getSession() {
		return session;
	}

	public void setSession(SessionData session) {
		this.session = session;
	}
	
	protected boolean validateRequest() {
		String name = this.getRequest().getNonblankParameter("name");
		String title = this.getRequest().getNonblankParameter("title");
		logger.error("Title: "+ title + "@@@@@@" + "Name: " + name);

		//return name!=null && this.escapeRemove(name) && title!=null && this.escapeRemove(title);
		return true;
	}
	
	//Returns a pattern where all punctuation characters are escaped.
	protected boolean escapeRemove(String str) {		 
		logger.error("####" + str);
		final Pattern p = Pattern.compile("[^a-zA-z0-9]");
		if (str!=null) logger.error(p.matcher(str).matches());
		return str != null ? !p.matcher(str).matches() : false;
	}
}
