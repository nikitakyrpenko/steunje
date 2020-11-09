package com.negeso.module.job;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.mailer.BadEmailAddressException;
import com.negeso.framework.mailer.MailClient;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.Timer;
import com.negeso.framework.Env;
import com.negeso.module.job.domain.Application;
import com.negeso.module.job.domain.ApplicationFieldValue;
import com.negeso.module.job.domain.DVA;
import com.negeso.module.job.domain.Department;
import com.negeso.module.job.domain.ExtraField;
import com.negeso.module.job.extra_field.FieldRepository;
import com.negeso.module.job.extra_field.FieldType;
import com.negeso.module.job.generator.ApplicationXmlBuilder;
import com.negeso.module.job.generator.VacancyXmlBuilder;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.FileResource;

import java.sql.Connection;
import java.util.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 25, 2005
 */

public class ApplicationSaverUtil {
    private static Logger logger = Logger.getLogger( ApplicationSaverUtil.class );

    public static void saveApplication(RequestContext request, Connection conn)
            throws Exception{
        try{
            Timer timer = null;
            if (logger.isInfoEnabled()){ timer = new Timer();; }
            conn.setAutoCommit(false);
            State state = new State(request);
            
            if ( state.getVacancyId() == null ){
                logger.error("ERROR: vacancyId is null");
            }
            if ( state.getDepartmentId() == null ){
                logger.error("ERROR: departmentId is null");
            }

            Iterator iter = request.getParameterNames();
            String parName = null;
            Application app = new Application();
            Long appId = app.insert(conn);
			List<File> attachFileList = new ArrayList<File>();
            while(iter.hasNext()){
                parName = (String) iter.next();
                if (!parName.equals("mode") && !parName.equals("dep_id") &&
                        !parName.equals("vac_id") && !parName.equals("REQUEST_URI") &&
                        !parName.equals("REQUEST_LOCALES") && !parName.equals("filename") ){
                    if(parName.length()>3 && parName.substring(0,3).equals("_id")){
                        saveExtraField(parName, appId, request, conn, attachFileList);
                    }
                    else{
                        saveGeneralField(parName, app, request, conn, attachFileList);
                    }
                }
            }
            DVA dva = new DVA();
            dva.setApplication_id(appId);
            dva.setDepartmentId(state.getDepartmentId());
            dva.setVacancy_id(state.getVacancyId());
            dva.setApplicationStatus(JobModule.APPLICATION_STATUS_NEW);
            dva.insert(conn);
            app.update(conn);

            // prepare and invoke application mailing 
            List<Long> deptIds = new ArrayList<Long>();
            deptIds.add(state.getDepartmentId());
            sendApplicationEmail(
                conn, 
                request,
                app,
                state.getVacancyId(), 
                request.getSession().getLanguage().getId(),
                deptIds,
                attachFileList
            );
            conn.commit();
            if (logger.isInfoEnabled()){ logger.info(timer); }
        }
        catch(Exception e){
            conn.rollback();
            logger.error(e.getMessage(), e);
            throw new Exception("Error while saving application");
        }
        finally{
            conn.setAutoCommit(true);
        }
    }

    public static void saveGeneralApplication(RequestContext request, Connection conn)
            throws Exception{
        try{
            conn.setAutoCommit(false);
            State state = new State(request);
            Iterator iter = request.getParameterNames();
            String parName = null;
            Application app = new Application();
            Long appId = app.insert(conn);
			List<File> attachFileList = new ArrayList<File>();
            while(iter.hasNext()){
                parName = (String) iter.next();
                if (!parName.equals("mode") && !parName.equals("dep_id") && !parName.equals("_dep") &&
                        !parName.equals("vac_id") && !parName.equals("REQUEST_URI") &&
                        !parName.equals("REQUEST_LOCALES") && !parName.equals("filename") ){
                    if(parName.length()>3 && parName.substring(0,3).equals("_id")){
                        saveExtraField(parName, appId, request, conn, attachFileList);
                    }
                    else{
                        saveGeneralField(parName, app, request, conn, attachFileList);
                    }
                }
            }
            DVA dva = null;

            String []s = request.getParameters("_dep");
            List<Long> deptIds = new ArrayList<Long>();
            for(int i=0; i<s.length; i++){
                dva = new DVA();
                dva.setApplication_id(appId);
                dva.setDepartmentId(new Long(s[i]));
                dva.setVacancy_id(state.getVacancyId());
                dva.setApplicationStatus(JobModule.APPLICATION_STATUS_NEW);
                dva.insert(conn);
                deptIds.add(dva.getDepartmentId());
            }
            app.update(conn);

            // prepare and invoke application mailing 
            sendApplicationEmail(
                conn, 
                request,
                app, 
                state.getVacancyId(),
                request.getSession().getLanguage().getId(),
                deptIds,
                attachFileList
            );

        }
        catch(Exception e){
            conn.rollback();
            conn.setAutoCommit(true);
            logger.error(e.getMessage(), e);
            throw new Exception("Error while saving application");
        }
        conn.commit();
    }

    /**
     * Save general app field
     * 
     * @param parName
     * @param app
     * @param request
     * @param conn
     * @throws Exception
     */
    private static void saveGeneralField(
        String parName, Application app, RequestContext request, Connection conn, List<File> attachFileList)
    throws Exception {
        if (parName.equals("first_name")){
            app.setName(request.getParameter("first_name"));
        }
        else if (parName.equals("second_name")){
            app.setSurname(request.getParameter("second_name"));
        }
        else if (parName.equals("address_line")){
            app.setAddress(request.getParameter("address_line"));
        }
        else if (parName.equals("phone")){
            app.setTelephone(request.getParameter("phone"));
        }
        else if (parName.equals("mobile")){
            app.setMobile(request.getParameter("mobile"));
        }
        else if (parName.equals("email")){
            app.setEmail(request.getParameter("email"));
        }
        else if (parName.equals("birthday")){
            if(request.getParameter("birthday")!=null && !request.getParameter("birthday").equals("")){
                Date date = JobModule.parseDate(request.getParameter("birthday"));
                if (date == null){
                    logger.error("invalid date for birthday");
                }
                else{
                    Timestamp birth = new Timestamp(date.getTime());
                    app.setBirthdate(birth);
                }
            }
        }
        else if (parName.equals("birthplace")){
            app.setBirthplace(request.getParameter("birthplace"));
        }
        else if (parName.equals("citizenship")){
            app.setCitizenship(request.getParameter("citizenship"));
        }
        else if (parName.equals("cv_file")){
            app.setCv(saveFile(request, parName, attachFileList));
        }
    }

    /**
     * Handle extra field
     * 
     * @param parName
     * @param appId
     * @param request
     * @param conn
     */
    private static void saveExtraField(
        String parName, Long appId, RequestContext request, Connection conn, List<File> attachFileList)
    {
        if (logger.isInfoEnabled()){
            logger.info("field:" + parName);
        }
        try{
            Long id = new Long(parName.substring(3, parName.length()));
            ExtraField field = ExtraField.findById(conn, id);
            if ( field == null ){
                 logger.error("Extra field not found by id: " + id);
                 return;
            }
            FieldType fieldType = FieldRepository.get().getType(conn, field.getTypeId()); 
            if ( fieldType == null ){
                logger.error("Field type not found by id: " + field.getTypeId());
                return;
            }
            ApplicationFieldValue fieldValue = new ApplicationFieldValue();
        
            if(fieldType.getType().equals("string")){
                fieldValue.setExtra_field_value(request.getParameter(parName));
            }
            else if(fieldType.getType().equals("number")){
                fieldValue.setExtra_field_value(request.getParameter(parName));
            }
            else if(fieldType.getType().equals("text")){
                fieldValue.setExtra_field_value(request.getParameter(parName));
            }
            else if(fieldType.getType().equals("select_box")){
                fieldValue.setExtra_field_value_id(new Long(request.getParameter(parName).substring(3, request.getParameter(parName).length())));
            }
            else if(fieldType.getType().equals("check_box")){
                String []s = request.getParameters(parName);
                String val = "";
                for(int i=0; i<s.length; i++){
                    val+=s[i].substring(3, request.getParameter(parName).length());
                    if(i!=(s.length-1)){
                        val+=",";
                    }
                }
                fieldValue.setExtra_field_value(val);
            }
            else if(fieldType.getType().equals("file")){
                fieldValue.setExtra_field_value(saveFile(request, parName, attachFileList));
            }
            else if(fieldType.getType().equals("email")){
                fieldValue.setExtra_field_value(request.getParameter(parName));
            }
            else if(fieldType.getType().equals("radio_box")){
                fieldValue.setExtra_field_value_id(new Long(request.getParameter(parName).substring(3, request.getParameter(parName).length())));
            }
            else if(fieldType.getType().equals("date")){
                fieldValue.setExtra_field_value(request.getParameter(parName));
            }
            fieldValue.setApplication_id(appId);
            fieldValue.setExtra_field_id(field.getId());
            fieldValue.insert(conn);
        }
        catch(CriticalException e){
            String error = MessageFormat.format(
                "Error processing field: {0} appId: {1} value: {2}. CriticalError: {3}", 
                parName, 
                appId,
                request.getParameter(parName),
                e.getMessage()
            );
            logger.error(error , e);
        }
    }

    private static String saveFile(RequestContext request, String field, List<File> fileList) 
    	throws CriticalException
    {
    	FileItem fileItem = request.getFile(field);
				
    	if ( fileItem == null ){
    		logger.warn("cv file item is null");
    		return null;
    	}
		
		if ( fileItem.getSize() == 0 ){
    		logger.warn("cv file item size = 0");
    		return null;
    	}
    	
    	try{
	    	File file = Repository.get().saveFileItemSafe(
	    		request.getSession().getUser(), 
	    		JobModule.get().getConfig().getUloadProfile(),
	    		fileItem
	    	);
			fileList.add(file);
			if (logger.isInfoEnabled()){
				logger.info("processing file: " + file.getAbsolutePath());
			}

			if (file != null){
	    		FileResource res = Repository.get().getFileResource( file );
	    		return res.getCatalogPath();
	    	}
    	}
    	catch(AccessDeniedException e){
    		logger.error(
    			"!!!Access denied for user:" + 
    			request.getSession().getUserId() +
    			" folder:" + 
    			JobModule.get().getConfig().getUloadProfile().getWorkingFolder(),
    			e
    		);
    	} 
    	catch (RequestParametersException e) {
    		logger.error("-ERROR", e);
		}
        return null;
    }
    
    /**
     * Send email with notification to client
     * 
     * 
     * @param conn
     * @param appl
     * @param langId
     * @throws CriticalException 
     */
    private static void sendApplicationEmail(
        Connection con,
        RequestContext request,
        Application appl, 
        Long vacancyId,
        Long langId,
        List<Long> departmentIds,
		List<File> attachFileList
    ) 
        throws CriticalException
    {
        List<String> emails = new ArrayList<String>();
        
        if ( !departmentIds.contains(JobModule.get().getGeneralDepartmentId()) ){
            Department dept = JobModule.get().getGeneralDepartment(con);
            if ( dept.getEmail()!= null )
            emails.add(dept.getEmail());
        }
        Element bodyXml = XmlHelper.createPageElement(request);
        for (Long deptId: departmentIds){
            Department dept = Department.findById(con, deptId);
            Element deptEl = Xbuilder.addEl(bodyXml, "department", null);
            Xbuilder.setAttr(deptEl, "title", dept.getTitle()); 
            logger.info("adding dept:" + dept.getTitle());
            if ( dept == null ){
                logger.error("Department not found by id:" + deptId);
            }
            if ( dept.getEmail() == null ) {
                continue;
            }
            if ( "".equals( dept.getEmail().trim()) ){
                continue;
            }
            
            if ( !emails.contains(dept.getEmail()) ){
                emails.add(dept.getEmail());
            }
        }

        // create mail body
        VacancyXmlBuilder.buildVacancyDetails(con, bodyXml, vacancyId, langId, false);
        ApplicationXmlBuilder.buildApplication(con, bodyXml, appl.getId(), langId);
        

        String mailBody = XmlHelper.transformToString(
            bodyXml.getOwnerDocument(), 
            JobModule.APPLICATION_MAIL_XSL, 
            "html"
        );
            
        
        // prepare datasource
		List<DataSource> dsList = new ArrayList<DataSource>();
		for (File attachFile: attachFileList){
			dsList.add(new FileDataSource(attachFile));
			if (logger.isInfoEnabled()){
				logger.info("Attach: " + attachFile.getAbsolutePath());
			}
		}
		
        DataSource[] ds = dsList.toArray(new DataSource[0]);

        // send emails
        String validSmtpHost = Env.getValidSmtpHost();
        for (String email: emails){
            try{
            	MailClient mailer = new MailClient();
                mailer.sendHTMLMessage(
                    email,
                    JobModule.get().getConfig().getFeedbackEmail(),
                    JobModule.get().getConfig().getApplicationEmailSubject(),
                    mailBody,
                    validSmtpHost,
                    ds
                );
				logger.error("mail sent to: " + email);
            }
            catch( MessagingException e ){
                logger.error("Error sending mail", e);
            } catch (Throwable e) {
				logger.error("Unknown mail exception ", e);
			}
        }
    }
}
