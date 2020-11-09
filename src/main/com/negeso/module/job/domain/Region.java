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
package com.negeso.module.job.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "region", namespace = Env.NEGESO_NAMESPACE)
public class Region implements Entity {

	private static final long serialVersionUID = -2083654691127423415L;
	
	@XmlAttribute
	private Long id = null;
	@XmlAttribute
	private String title = StringUtils.EMPTY;
	@XmlAttribute
	private Long orderNumber = null;
	
	List<Vacancy> vacancies = new ArrayList<Vacancy>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<Vacancy> getVacancies() {
		return vacancies;
	}

	public void setVacancies(List<Vacancy> vacancies) {
		this.vacancies = vacancies;
	}
}
