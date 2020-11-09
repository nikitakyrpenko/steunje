package com.negeso.framework.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.generators.Xquery;
import com.negeso.framework.security.SecurityGuard;

public class AdminServiceServlet extends HttpServlet {

	private static SessionData session;

	private static final File TEMPLATE_WSDL =
		new File(Env.getRealPath("/WEB-INF/webservices/serviceAdmin.wsdl"));

	private static final File TEMPLATE_RESPONSE =
		new File(Env.getRealPath("/WEB-INF/webservices/response.xml"));
	
	private static final File TEMPLATE_ERROR =
		new File(Env.getRealPath("/WEB-INF/webservices/error.xml"));
	
	private static final long serialVersionUID = -2823841377455510866L;
	
	private static Logger logger = Logger.getLogger(AdminServiceServlet.class);
	
	/** Sends WSDL file to the browser */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		logger.debug("+");
		PrintWriter out = null;
		try {
			resp.setContentType("text/xml; charset=utf-8");
			resp.setDateHeader("Expires", -1);
			out = resp.getWriter();
			String url = Env.getHostName() + "adminwebservice";
			out.print(
					FileUtils.readFileToString(TEMPLATE_WSDL, "UTF-8")
					.replaceFirst("WEBSERVICE_URL_HERE", url));
			logger.debug("-");
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
	
	/** Prepare a Command invocation through the webservice. */
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
        logger.debug("+");
        req.setCharacterEncoding("UTF-8");
        InputStream is = null;
		try {
			is = req.getInputStream();
			Document doc = XmlHelper.newBuilder().parse(is);
			String operation = Xquery.val(doc, "//operationKind/text()");
			Validate.notEmpty(operation);
			List<Element> elNames =
				Xquery.elems(doc, "//parameterNames/string");
			List<Element> elVals =
				Xquery.elems(doc, "//parameterValues/string");
			Validate.isTrue(elNames.size() == elVals.size());
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			for (int i = 0; i < elNames.size(); i++)
				params.put(
					Xquery.val(elNames.get(i), "text()"),
					new String[] {Xquery.val(elVals.get(i), "text()")} );
			Map<String, Object> io = new HashMap<String, Object>();
			io.put(WebFrontController.HTTP_SERVLET_REQUEST, req);
			io.put(WebFrontController.HTTP_SERVLET_RESPONSE, resp);
			session = SessionData.getSessionData(req);
			session.startSession();
	        if(!SecurityGuard.canContribute(session.getUser(), null)) {
	            throw new Exception("You are not authorized for this operation");
	        }
            String result = execute(
    			new RequestContext(params,  SessionData.getSessionData(req), io),
    			CommandFactory.getCommandClassForName(operation) );
            Document docResult =
            	XmlHelper.newBuilder().parse(TEMPLATE_RESPONSE);
            Xbuilder.addText(Xquery.elem(docResult, "//result"), result);
            serialize(resp, docResult);
		} catch (Exception e) {
			logger.error("- Error while executing command", e);
            sendError(resp, e);
		} finally {
			IOUtils.closeQuietly(is);
		}
        logger.debug("-");
    }
    
    /**
     * Executes a command.
     * @return serialized XML-result of the command
     */
    private String execute(RequestContext request, Class commandClass)
    throws Exception {
    	logger.debug("+");
    	Command command = (Command) commandClass.newInstance(); 
    	command.setRequestContext(request);
    	ResponseContext response = command.execute();
    	Validate.isTrue(
			response.getResultName() == AbstractCommand.RESULT_SUCCESS,
			(String) response.getResultMap().get(AbstractCommand.OUTPUT_ERROR));
    	Document xml = (Document)
    		response.getResultMap().get(AbstractCommand.OUTPUT_XML);
    	if (xml == null) {
    		logger.info("- no XML result for command: " + commandClass);
    		return "";
    	}
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	XmlHelper.newTransformer()
    		.transform(new DOMSource(xml), new StreamResult(out));
    	logger.debug("- Success");
    	return out.toString("UTF-8");
    }
    
    /** Output SOAP <b>auxOperationResponse</b> in case of success. */
    private void serialize(HttpServletResponse resp, Document doc)
    throws Exception {
    	logger.debug("+");
    	ServletOutputStream out = null;
    	try {
    		resp.setContentType("text/xml; charset=utf-8");
    		resp.setDateHeader("Expires", -1);
    		out = resp.getOutputStream();
    		XmlHelper.newTransformer()
    			.transform(new DOMSource(doc), new StreamResult(out));
    		logger.debug("-");
    	} finally {
    		IOUtils.closeQuietly(out);
    	}
    }

    /** Output SOAP <b>env:Fault</b> in case of any errors */
	private void sendError(HttpServletResponse resp, Exception e) {
		logger.debug("+");
		try {
			Document docError = XmlHelper.newBuilder().parse(TEMPLATE_ERROR);
			String msg = e.getMessage();
			if (StringUtils.isBlank(msg)) {
				msg = "Unknown error";
			} else {
				msg = msg.replace('[', '(').replace(']', ')');
			}
			Xbuilder.addText(
				Xquery.elem(docError, "//faultstring"), "[" + msg + "]" );
			serialize(resp, docError);
			logger.debug("-");
		} catch (Exception e2) {
			logger.error("- Exception", e2);
		}
	}

}