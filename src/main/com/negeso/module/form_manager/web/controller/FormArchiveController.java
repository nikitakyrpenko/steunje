/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.form_manager.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.negeso.module.form_manager.FormExportException;
import com.negeso.module.form_manager.PagesManager;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.form_manager.FormExporter;
import com.negeso.module.form_manager.domain.FormField;
import com.negeso.module.form_manager.domain.Forms;
import com.negeso.module.form_manager.service.FormArchiveService;
import com.negeso.module.form_manager.service.FormFieldService;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class FormArchiveController{

	private static final Logger logger = Logger.getLogger(FormArchiveController.class);

	private FormArchiveService formArchiveService = null;
	private FormFieldService formFieldService = null;
	private SessionFactory sessionFactory;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView viewArchive(HttpServletRequest request, HttpServletResponse response) throws Exception{

		int page = readPageNumber(request);
		int recordsPerPage = 20;
		Long formId = readFormId(request);

		ModelAndView model = new ModelAndView("forms_archive");
		model.addObject("formId", formId);
		model.addObject("formFields", formFieldService.getFormFieldDao().listFormFields(formId));
		model.addObject("archiveList", formArchiveService.listFormArchives(formId, page, recordsPerPage));
		PagesManager pm = new PagesManager(page, formArchiveService.getFormArchiveDao().countRecords(formId), recordsPerPage);
		model.addObject("pageLinks", pm);
		RequestUtil.getHistoryStack(request).push(new Link("Form archive",
				"/admin/forms_archive?formId=" + formId,
				false));

		return model;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView saveConfig(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long formId = readFormId(request);
		Forms form = formFieldService.getFormsDao().read(formId);
		String[] fieldIds = request.getParameterValues("_fieldId");
		for(FormField formField : form.getFields()){
			formField.setVisible(false);
			if(fieldIds != null){
				for(String id : fieldIds){
					if(formField.getId().equals(Long.valueOf(id))){
						formField.setVisible(true);
						break;
					}
				}
			}
		}
		formFieldService.getFormsDao().update(form);

		return new ModelAndView(String.format("redirect:/admin/forms_archive?form_id=%s", formId));
	}

	@RequestMapping(value = "page", method = RequestMethod.GET)
	public ModelAndView page(HttpServletRequest request, HttpServletResponse response) throws Exception{

		int recordsPerPage = 20;
		int page = readPageNumber(request);
		Long formId = readFormId(request);

		ModelAndView mv = new ModelAndView("forms_archive_ajax");
		mv.addObject("formId", formId);
		mv.addObject("formFields", formFieldService.getFormFieldDao().listFormFields(formId));
		mv.addObject("archiveList", formArchiveService.listFormArchives(formId, page, recordsPerPage));
		PagesManager pm = new PagesManager(page, formArchiveService.getFormArchiveDao().countRecords(formId), recordsPerPage);
		mv.addObject("pageLinks", pm);

		return mv;
	}

	@RequestMapping(value = "export", method = RequestMethod.GET)
	public void exportArchive(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long formId = readFormId(request);
		BufferedInputStream in = null;
		try{
			File file = FormExporter.export(formFieldService, formId, sessionFactory);
			int fSize = (int) file.length();

			in = new BufferedInputStream(new FileInputStream(file));

			response.setBufferSize((int) file.length());
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			response.setContentLength(fSize);
			FileCopyUtils.copy(in, response.getOutputStream());
		} catch(FormExportException fe){
			response.setHeader("Content-Disposition", "attachment; filename=server_error.csv");
			response.getOutputStream().close();
		} catch(ClientAbortException var1){
			logger.info("aborted request");
		} catch(IOException var2){
			logger.error("aborted", var2);
		} catch(Exception var3){
			logger.error("unexpected exception", var3);
		} finally {
			if(in != null) in.close();
		}
	}

	private Long readFormId(HttpServletRequest request){
		return Long.valueOf(request.getParameter("form_id"));
	}

	private int readPageNumber(HttpServletRequest request){
		String pageNumberStr = request.getParameter("page");

		return Integer.valueOf(pageNumberStr == null ? "1" : pageNumberStr);
	}

	public void setFormArchiveService(FormArchiveService formArchiveService){
		this.formArchiveService = formArchiveService;
	}

	public void setFormFieldService(FormFieldService formFieldService){
		this.formFieldService = formFieldService;
	}

	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}

}
