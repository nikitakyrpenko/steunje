/*
 * Created on 04.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 */
package com.negeso.module.form_manager.command;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.page.PageBuilder;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.module.form_manager.FormManagerException;
import com.negeso.module.form_manager.domain.FormArchive;
import com.negeso.module.form_manager.domain.FormArchiveValue;
import com.negeso.module.form_manager.domain.FormField;
import com.negeso.module.form_manager.domain.Forms;
import com.negeso.module.form_manager.service.FormArchiveService;
import com.negeso.module.form_manager.service.FormFieldService;
import com.negeso.module.form_manager.service.FormService;

/**
 * @author Shkabi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 */
public class ProcessForm extends AbstractCommand {

    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */ 
    private static final Logger logger = Logger.getLogger(ProcessForm.class);
    
    public static final String OUTPUT_DOCUMENT = "xml";
    public static final String FORM_MAIL_TEMPL_XSL = "FORM_MAIL_TEMPL_XSL";
    
    public static final String MAIL_SUCCESS = "mail_success";
    
    private User user = null;
    private File tmpdir = null;    
    
    @SuppressWarnings("unchecked")
	public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        String method = request.getSessionData().getRequest().getMethod();
        if ("GET".equals(method)){
        	response.setResultName(RESULT_FAILURE);
        	return response;
        }
        
        user = request.getSession().getUser();
        String languageCode = request.getSession().getLanguage().getCode();
        String success = "mail_failure";
        Forms form = null;
        try {
            try {
                //Getting directory
                String dirname = Env.getRealPath("/tmpfiles");
                tmpdir = new File(dirname);
                if (!tmpdir.exists()) {
                    tmpdir.mkdir();
                }
                String formID = request.getParameter("mailToFlag");
                
                FormArchive formArchive = new FormArchive();
                formArchive.setSentDate(new java.sql.Timestamp(System.currentTimeMillis()));                
                
                if ( formID == null ) {
                    logger.error("NO mailToFlag");
                    throw new FormManagerException();
                }
                form = getFormService().findById(Long.valueOf(formID));
                if (form != null) {
                    formArchive.setFormId(Long.valueOf(formID));
                } else {
                    logger.error("NO Form with such("+ formID +") form_id");
                    throw new FormManagerException();
                }
                
                //Generating Message body
                Iterator parameters = request.getParameterNames();
                String parameter = null;
                FileItem postedFile = null;
                InternetAddress[] addr = { new InternetAddress( form.getEmail())};
                String[] stringValues = null;
                Multipart mp = new MimeMultipart();
                File imgfile = null;
                try {
                	StringBuilder posted = new StringBuilder();
                	Formatter postedFormatter = new Formatter(posted, Locale.getDefault());
                	postedFormatter.format("%1$te-%1$tm-%1$tY %tT", Calendar.getInstance());
                
                	String contentType=Env.getProperty("MAIL_CONTENT_TYPE", "text/plain");
                	String messageBody="";
                	Element bodyEl=null, formEl = null, inputEl=null;
                
                	if("text/html".equalsIgnoreCase(contentType)) {
                		logger.info("### starting to build XML");
                		bodyEl = XmlHelper.createPageElement(request);
                    
                		formEl = Xbuilder.addEl(bodyEl,
                				"webform",
                				null
                    			); 
                		formEl.setAttribute("title", form.getName());
                		formEl.setAttribute("posted", posted.toString());
                	} else {
                		logger.info("### starting to write message string");
                		messageBody = posted.toString() + "\n";
                		messageBody += form.getName() + "\n \n";                	
                	}
                	
                	boolean defaultEmailFlag = true;
                	if (Env.getProperty("DEFAULT_FROM_EMAIL_VALUE")!= null
                			&& Env.getProperty("DEFAULT_FROM_EMAIL_VALUE").equals("false"))
                	{
                		defaultEmailFlag = false;
                	}
                	String defaultFrom = "mailform@negeso.com";
                	while (parameters.hasNext())
                	{
                		parameter = parameters.next().toString();
                		if (parameter.equals("mailToFlag") ||
                				parameter.equals("REQUEST_URI") ||
                				parameter.equals("REQUEST_LOCALES"))
                		{
                			//Already processed
                		}
                		else if (parameter.equals("first_obligatory_email_field")
                				&& 
                				!request.
                				getParameter("first_obligatory_email_field").
                				equals("")
                				&& !defaultEmailFlag)
                		{    
                			defaultFrom = 
                				request.getParameter("first_obligatory_email_field"); 
                		}
                		else if (parameter.equals("first_obligatory_email_field"))
                		{
                			//Already processed
                		}
                		else
                		{
                			postedFile = request.getFile(parameter);
                			if (postedFile != null)//File parameter
                			{
                				if (postedFile.getName().length() > 0) {                				
	                				try {
	                					File file = new File(
	                							this.tmpdir
	                							+ "/"
	                							+ new File(postedFile.getName()).getName());
	                					
	                					postedFile.write(file);
	                					imgfile = file;
	                				}
	                				catch (Exception ex1) {
	                					logger.error("-", ex1);
	                				}
	                				
	                				BodyPart filePart = new MimeBodyPart();
	                				DataSource source = new FileDataSource(imgfile);
	                				filePart.setDataHandler(new DataHandler(source));
	                				filePart.setFileName(imgfile.getName());
	                				filePart.setDisposition("attachment");
	                				//Adding part to multiPart
	                				mp.addBodyPart(filePart);
	                				addFormValueToArchive(parameter, imgfile.getName(), 
        									formArchive, Long.valueOf(formID));
                				}
                			}
                			else//String parameter
                			{
                				stringValues = request.getParameters(parameter);
                				if (stringValues != null)
                				{
                					for (int i=0; i<stringValues.length; i++)
                					{
                						if("text/html".equals(contentType)) {
                							inputEl = Xbuilder.addEl(formEl,
                									"input-field",
                									null
                                        			);
                							inputEl.setAttribute("name", parameter);
                							inputEl.setAttribute("value", stringValues[i]);
                							addFormValueToArchive(parameter, stringValues[i], 
                									formArchive, Long.valueOf(formID));
                						} else {
                							messageBody += 
                								parameter + ": " +
                								stringValues[i] + "\n";
                						}
                					}
                				}
                			}
                		}
                	}
                	
                	//Sending email
                	sendEmail(messageBody, contentType, bodyEl, form.getName(), defaultFrom, mp, addr);
                	getFormArchiveService().getFormArchiveDao().create(formArchive);
                	success = MAIL_SUCCESS;
                } finally {
                	if (imgfile != null) {
                		imgfile.delete();
                }
            }	
                
         } catch (Exception e) {
                logger.error("mail_failure", e);
            }
            
            //Generating Response
         	PageH page = null;
	      	if (success.equals(MAIL_SUCCESS) 
	      			&& form != null 
	      			&& form.getPageId() != null) {
         		page = PageService.getInstance().findById(form.getPageId());
	      	} else {
         		page = PageService.getInstance().findByCategory(success, languageCode);
	      	}
            Document pageDoc = new PageBuilder(this.user)
                .buildPage(page, getRequestContext(), true);
            
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, pageDoc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
            
        }
        catch (Exception e) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", e);
        }
        return response;
    }

    protected void sendEmail(String messageBody, String contentType,
    		Element bodyEl, String formName, String defaultFrom,
    		Multipart mp, InternetAddress[] addr) throws MessagingException {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", Env.getSmtpHost());
        Session session = Session.getDefaultInstance(props, null);    	
        MimeMessage mess = new MimeMessage(session);
        mess.setSentDate(new Date());
        BodyPart textPart = new MimeBodyPart();
		if("text/html".equalsIgnoreCase(contentType)) {
			messageBody = XmlHelper.transformToString(
                    bodyEl.getOwnerDocument(), 
                    FORM_MAIL_TEMPL_XSL, 
                    "html"
                );
			textPart.setContent(messageBody,"text/html; charset=\"UTF-8\"");
    	} else {
    		textPart.setContent(messageBody, "text/plain; charset=\"UTF-8\"");
    	}
        mp.addBodyPart(textPart);
        mess.setSubject(formName);
        mess.setFrom(new InternetAddress(defaultFrom));
        mess.setRecipients(Message.RecipientType.TO, addr);
        logger.debug("### sending webform mail to: " + addr[0]);
        mess.setContent(mp);
        Transport tr = session.getTransport("smtp");
        tr.addTransportListener(new TransportHandler());
        tr.connect();
        tr.sendMessage(mess, addr);
    }
    
    private void addFormValueToArchive(String fieldName, String fieldValue, FormArchive formArchive, Long formId){
    	FormArchiveValue formArchiveValue = new FormArchiveValue();
    	FormField formField = getFormFieldService().getFormField(fieldName, formId);
    	if (formField != null){
    		formArchiveValue.setValue(fieldValue);
    		formArchiveValue.setFormField(formField);
    		formArchiveValue.setFormArchive(formArchive);
    		formArchive.getFields().add(formArchiveValue);
    	}
    };
    
    private FormArchiveService getFormArchiveService() {
		return (FormArchiveService) DispatchersContainer.getInstance().getBean("form_manager", "formArchiveService");
	}
    
    private FormFieldService getFormFieldService() {
    	return (FormFieldService) DispatchersContainer.getInstance().getBean("form_manager", "formFieldService");
	}
    
    private FormService getFormService(){
    	return (FormService) DispatchersContainer.getInstance().getBean("form_manager", "formService");
    }
    
}

/************************************************************************
 * 
 * @author oleg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 ************************************************************************/
class TransportHandler implements TransportListener {
    
    
    private static Logger logger = Logger.getLogger(TransportHandler.class);
    
    
    public void messageDelivered(TransportEvent evt)
    {
        logger.debug("+ -");
    }
    
    
    public void messageNotDelivered(TransportEvent evt)
    {
        logger.error("+ - messageNotDelivered: " + evt.getMessage());
    }
    

    public void messagePartiallyDelivered(TransportEvent evt)
    {
        logger.warn("+ - messageNotDelivered: " + evt.getMessage());
    }
    
    
}
