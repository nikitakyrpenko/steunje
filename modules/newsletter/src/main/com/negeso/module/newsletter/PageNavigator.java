/*
 * @(#)Id: PageNavigator.java, 02.04.2008 15:03:48, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class PageNavigator {

	private int recordsPerPage;
	private int pageNumber;
	
	private int count;
	private int currentPid;
	private String link = "";
	private Map<String, String> additionalURLparams = new HashMap<String, String>();
	
	public PageNavigator(){};
	
	public PageNavigator(int recordsPerPage, int pageNumber, int count,
			int currentPid, String link) {
		this.recordsPerPage = recordsPerPage;
		this.pageNumber = pageNumber;
		this.count = count;
		this.currentPid = currentPid;
		this.link = link;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getFirstElementInRange(){
		return currentPid * recordsPerPage;
	}
	
	public int getMinpid(){
		int minPid = 0;
		if (currentPid - pageNumber / 2 > 0){
			minPid = currentPid - pageNumber / 2 + 1;
		}
		if (minPid + getPidNumber() > getMaxpid()){
			minPid = getMaxpid() - getPidNumber() + 1;
		}
		return minPid;
	}
	
	public int getCorrectedCurrentPid(){
		return PageNavigator.correctCurrentPid(currentPid, recordsPerPage, count);
	}
	
	public static int correctCurrentPid(int currentPid, int recordsPerPage, int count){
		if (currentPid > 0 && currentPid * recordsPerPage > count){
			currentPid = (count - 1) / recordsPerPage;
		}
		return currentPid;
	}
	
	public void checkAndPutAdditionalParam(String name, String value){
		if (StringUtils.hasText(name) && StringUtils.hasText(value)){
			additionalURLparams.put(name, value);
		}
	}
	
	public int getPidNumber(){
		return Math.min(pageNumber, getMaxpid() + 1);
	}
	
	public int getMaxpid(){
		return (count - 1) / recordsPerPage;
	}

	public int getRecordsPerPage() {
		return recordsPerPage;
	}

	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getCurrentPid() {
		return currentPid;
	}

	public void setCurrentPid(int currentPid) {
		this.currentPid = currentPid;
	}

	public Map<String, String> getAdditionalURLparams() {
		return additionalURLparams;
	}

	public void setAdditionalURLparams(Map<String, String> additionalURLparams) {
		this.additionalURLparams = additionalURLparams;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getLinkWithParams(){
		String link = this.link + "?";
		String conjaction = "";
		for (String key : additionalURLparams.keySet()) {
			link += conjaction + key + "=" + additionalURLparams.get(key);
			conjaction = "&";
		}
		return link + conjaction;
	}
	
}
