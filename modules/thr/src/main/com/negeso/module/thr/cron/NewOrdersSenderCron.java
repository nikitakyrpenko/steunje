package com.negeso.module.thr.cron;

import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Callback;
import com.negeso.framework.dao.SessionTemplate;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.mailer.MailClient;
import com.negeso.module.thr.bo.ThrOrder;
import com.negeso.module.thr.filter.OrderFilter;
import com.negeso.module.thr.service.ThrOrderService;

public class NewOrdersSenderCron extends QuartzJobBean{
	
	private static Logger logger = Logger.getLogger(NewOrdersSenderCron.class);

	private SessionTemplate sessionTemplate = null;
	private ThrOrderService thrOrderService;
	private LockManager lockManager = new LockManager();
	
	private static final String LOCK_KEY = NewOrdersSenderCron.class.getSimpleName();

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		if (!lockManager.getLock(LOCK_KEY, hashCode())) {
			logger.error("Cron locked by another thread or something prevented to release it (in 1h it will be released automat.)");
			return;
		}
		try {
			sessionTemplate.execute(new Callback() {
				@Override
				public void process() {
					send();
				}
			});
		} catch (Exception e) {
			logger.error(e, e);
		} finally {
			lockManager.releaseLock(LOCK_KEY);
		}
		
	}
	
	protected void send() {
		String thrOrderDailyReportEmail = Env.getProperty("thrOrderDailyReportEmail", null);
		if (StringUtils.isNotBlank(thrOrderDailyReportEmail)) {
			OrderFilter filter = new OrderFilter();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			filter.setEndDate(calendar.getTime());
			
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			filter.setStartDate(calendar.getTime());
			
			try {
				List<ThrOrder> list =  thrOrderService.list(filter);
				Element el = OrdersXmlGenerator.buildXml(list);
				String body = OrdersHtmlGenerator.buildHtml(el);
				MailClient mailer = new MailClient();
				for (String email: thrOrderDailyReportEmail.split(",|;")) {
					mailer.sendHTMLMessage(
							email,
							Env.getDefaultEmail(),
							DatabaseResourceBundle.getTranslation("thr_order", "nl", "THR_ORDERS_EMAIL_SUBJECT"),
							body
					);					
				}
			} catch (MessagingException e) {
				logger.error("Error: ", e);
			}
		}
	}



	public SessionTemplate getSessionTemplate() {
		return sessionTemplate;
	}

	public void setSessionTemplate(SessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}

	public void setThrOrderService(ThrOrderService thrOrderService) {
		this.thrOrderService = thrOrderService;
	}

}
