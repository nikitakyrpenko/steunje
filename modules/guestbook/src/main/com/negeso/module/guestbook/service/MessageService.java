/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.guestbook.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.guestbook.bo.Message;
import com.negeso.module.guestbook.dao.MessageDao;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class MessageService {

	static Logger logger = Logger.getLogger(MessageService.class);
	
	private MessageDao messageDao;

	public void setDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	public void publishMessage(long id) {
		logger.debug("+");
		Message message = messageDao.read(id);
		message.setPublished(new Timestamp(System.currentTimeMillis()));
		messageDao.update(message);
		logger.debug("-");
	}
	
	public void deleteMessage(long id) {
		logger.debug("+");
		Message message = messageDao.read(id);
		messageDao.delete(message);
		logger.debug("-");
	}
	
	public void insertMessage(Message message) {
		logger.debug("+");
		messageDao.create(message);
		logger.debug("-");
	}
	
	public int countPublishedRecords(int gb_id) {
		logger.debug("+");
		int count = messageDao.countPublishedRecords(gb_id);
		logger.debug("-");
		return count;
	}

	public int countRecords(int gb_id) {
		logger.debug("+");
		int count = messageDao.countRecords(gb_id);
		logger.debug("-");
		return count;
	}
	
	public Message findMessage(long id) {
		logger.debug("+");
		Message message = messageDao.read(id);
		logger.debug("-");
		return message;
	}
	
	public List<Message> listPublishedMessages(int offset, int limit, int gb_id) {
		logger.debug("+");
		List<Message> messages = messageDao.listPublishedMessagesInRange(gb_id, limit, offset);
		logger.debug("-");
		return messages;
	}
	
	public List<Message> listMessages(int offset, int limit, int gb_id) {
		logger.debug("+");
		List<Message> messages = messageDao.listMessagesInRange(gb_id, limit, offset);
		logger.debug("-");
		return messages;
	}
	
}
