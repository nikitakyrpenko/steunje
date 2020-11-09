/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.controller;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class MockRankObject {

	private String date;
	private int instances;
	private KeyWord[] keyWords;
	private String[] topUrls;

	
		
	public MockRankObject() {
		super();
	}

	public MockRankObject(String date, int instances, KeyWord[] keyWords,
			String[] topUrls) {
		this.date = date;
		this.instances = instances;
		this.keyWords = keyWords;
		this.topUrls = topUrls;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public KeyWord[] getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(KeyWord[] keyWords) {
		this.keyWords = keyWords;
	}

	public String[] getTopUrls() {
		return topUrls;
	}

	public void setTopUrls(String[] topUrls) {
		this.topUrls = topUrls;
	}
}
