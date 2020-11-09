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
package com.negeso.module.form_manager;

import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.module.form_manager.domain.*;
import com.negeso.module.form_manager.service.FormArchiveService;
import com.negeso.module.form_manager.service.FormFieldService;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import org.hibernate.*;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FormExporter {
	
	private static final Logger logger = Logger.getLogger(FormExporter.class);

	public static synchronized File export(FormFieldService formFieldService, Long formId, SessionFactory sessionFactory) throws FormExportException{
		Folder folder = createFolder();
		File file = createOrGetFile(folder, formId);
		if(file.exists()) return file;

		ScrollableResults scrollableResults;
		StatelessSession session = null;
		PrintWriter writer = null;

		try{
			List<FormField> formFields = formFieldService.getFormFieldDao().listFormFields(formId);
			writer = openStreamAndWriteFirstLine(file, formFields);

			session = sessionFactory.openStatelessSession();
			Query query = session.createQuery("SELECT fa FROM FormArchive fa JOIN FETCH fa.fields f WHERE fa.formId = :formId");
			query.setParameter("formId", formId);
			scrollableResults = query.scroll(ScrollMode.FORWARD_ONLY);
			while(scrollableResults.next()){
				FormArchive row = (FormArchive) scrollableResults.get(0);
				writeLineToFile(writer, formFields, row);
			}
		} catch(HibernateException he){
			throw new FormExportException(he);
		} catch(IOException e){
			throw new FormExportException(e);
		} finally {
			if(writer != null) writer.close();
			if(session != null) session.close();
		}

		return file;
	}

	private static PrintWriter openStreamAndWriteFirstLine(File file, List<FormField> formFields) throws IOException {
		PrintWriter writer = null;
		if(file.createNewFile()){
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			String line = "date";
			for(FormField field : formFields){
				if(field.isVisible()){
					line = MessageFormat.format("{0},{1}", line, StringEscapeUtils.escapeCsv(field.getName()));
				}
			}
			writer.println(line);
		}
		return writer;
	}

	private static Folder createFolder(){
		return Repository.get().createFolder(
				Repository.get().getRootFolder(),
				"form_archive"
		);
	}

	private static void writeLineToFile(PrintWriter writer, List<FormField> formFields, FormArchive row){
		String line = Env.formatRoundDate(row.getSentDate());
		for(FormField field : formFields){
			if(field.isVisible()){
				String value = StringUtils.EMPTY;
				for(FormArchiveValue rowField : row.getFields()){
					if(rowField.getFormField().getId().equals(field.getId())){
						value = rowField.getValue();
						break;
					}
				}
				String sheet = value.replaceAll("[\\n\\r]", " ");
				sheet = StringEscapeUtils.escapeCsv(sheet);
				line = MessageFormat.format("{0},{1}", line, sheet);
			}

		}
		writer.println(line);
	}

	private static synchronized File createOrGetFile(Folder folder, Long formId){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		return new File(
				folder.getFile(),
				formatter.format(new Date(System.currentTimeMillis())) + "_" + formId + "-forms-data-export.csv"
		);
	}
}

