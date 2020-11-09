package com.negeso.framework.domain;

import com.negeso.framework.dao.Entity;

public class Country implements Entity {
	private Long id;
	private String name;
	private String alpha2;
	private String alpha3;
	private String code;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlpha2() {
		return alpha2;
	}
	public void setAlpha2(String alpha2) {
		this.alpha2 = alpha2;
	}
	public String getAlpha3() {
		return alpha3;
	}
	public void setAlpha3(String alpha3) {
		this.alpha3 = alpha3;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
