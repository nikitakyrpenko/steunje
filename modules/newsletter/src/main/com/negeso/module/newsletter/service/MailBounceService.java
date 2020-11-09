/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.util.Timer;
import com.negeso.module.newsletter.Configuration;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class MailBounceService implements Job {

	private static final Logger logger = Logger.getLogger(MailBounceService.class);
	
	public static final String MAIL_CACHE_PATH = "/WEB-INF/generated/mail_cache";

	private StateService stateService = null;
	
	public static final String JOB_NAME = "Mail Bounce Handler"; // $NON-NLS-1$
	public static final String JOB_GROUP = "Mailing"; // $NON-NLS-1$
	private Scheduler scheduler = null;
	public void startup() {
		startScheduler();
	}

	private void startScheduler() {
		logger.debug("+");
		
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			scheduler = schedulerFactory.getScheduler();
			JobDetail jobDetail = 
				new JobDetail(JOB_NAME, JOB_GROUP, this.getClass());
			CronTrigger cronTrigger = new CronTrigger(JOB_NAME, JOB_GROUP);
			cronTrigger.setStartTime(new Date());
			try {
				CronExpression cexp = new CronExpression(getCronExpression());
				cronTrigger.setCronExpression(cexp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			scheduler.scheduleJob(jobDetail, cronTrigger);
			
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error("quartz sheduler error: " + e.getMessage());
		}
		
		logger.debug("-");
	}
	
	public void shutdown() {
		logger.debug("+");
		try {
			if (scheduler != null && !scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			logger.error("quartz sheduler error: " + e.getMessage());
		}
		logger.debug("+");
	}
	
	private String getCronExpression() {
		return Configuration.getBounceTimePattern();
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("+");
	
		if (stateService == null){
			stateService = (StateService) DispatchersContainer.getInstance().
				getBean(ModuleConstants.NEWSLETTER_MODULE, "stateService");
		}
		
		handleBounceMessage();
		
		logger.debug("-");
	}

	private void handleBounceMessage() {
		logger.debug("+");
		Timer timer = new Timer();
		try{
			ImapClient imapClient = new ImapClient();
			imapClient.archiveMails();
			File[] mails = getArchivedMails();
			for (int i = 0; i < mails.length; i++){
				processMail(mails[i]);
			}
		}catch(Exception e){
			logger.error("mail bounce service error: " +e.getMessage(), e);
		}
		logger.error("mail bouncing process time = " + timer.stop());
		logger.debug("-");
	}
	
	@SuppressWarnings("unchecked")
	private void processMail(File mail) throws IOException{
		logger.debug("+");
		if (!mail.exists()){
			logger.error("- mail is not exists: ");
			return;
		}
		Map attributes = new HashMap();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(mail));
			while(in.ready()){
				getRequiredAttributes(in.readLine(), attributes);
			}
			if (checkRequiredAttributes(attributes)){
				logger.debug("all necessary attributes of" +
						"bounced mail found.");
				try{
					Long uniqId = Long.valueOf((String) attributes.get(
							Configuration.getXMailingIdAttrKey()));
					stateService.updateBounceState(uniqId, 
							(String)attributes.get(Configuration.ERROR_MESSAGE));
				}catch(Exception e){
					logger.error("error while updating mail status");
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("- mail is not found: " + e.getMessage());
		} finally{
            assert in != null;
            in.close();
		}

		if (Configuration.isDeleteBouncedEmail()) {
			if (!mail.delete())
				logger.error("mail file cannot be deleted");
		}
		
	}
	
	private boolean checkRequiredAttributes(Map<String, String> attributes){
		return attributes.containsKey(Configuration.getXMailerAttrKey()) &&
				attributes.containsKey(Configuration.getXMailerAttrKey());
	}
	
	private void getRequiredAttributes(String line, Map<String, String> attributes){
		String trimLine = line.toLowerCase().trim();
		if (trimLine.contains(Configuration.getXMailerAttrKey()) &&
				trimLine.contains(Env.WCMS_X_MAILER.trim().toLowerCase())){
			attributes.put(Configuration.getXMailerAttrKey(), Env.WCMS_X_MAILER);
		}
		if (trimLine.contains(Configuration.getXMailingIdAttrKey())){
			String[] arr = trimLine.split(" ");
			attributes.put(Configuration.getXMailingIdAttrKey(), arr[arr.length - 1]);
		}
		if (trimLine.contains(Configuration.getBouncedSubject())){
			String errorMessage = trimLine.substring(trimLine.indexOf(":") + 1).trim();
			attributes.put(Configuration.ERROR_MESSAGE, errorMessage);
		}
	}
	
	private File[] getArchivedMails(){
		File f = new File(Env.getRealPath(MailBounceService.MAIL_CACHE_PATH));
		return f.listFiles();
	}

	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}
	
}
