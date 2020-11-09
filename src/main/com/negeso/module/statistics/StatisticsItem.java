package com.negeso.module.statistics;

public class StatisticsItem {
	private String name;
	private long value;
	private float fvalue;
	private String parameter;
	private String pageName;
	private String title;
	
	public StatisticsItem()
	{
		
	}
	
	public StatisticsItem(String name, long value, float fvalue, String parameter)
	{
		this.name = name;
		this.value = value;
		this.fvalue = fvalue;
		this.parameter = parameter;
	}

	public float getFvalue() {
		return fvalue;
	}
	

	public void setFvalue(float fvalue) {
		this.fvalue = fvalue;
	}
	

	public String getName() {
		return name;
	}
	

	public void setName(String name) {
		this.name = name;
	}
	

	public String getParameter() {
		return parameter;
	}
	

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	

	public long getValue() {
		return value;
	}
	

	public void setValue(long value) {
		this.value = value;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String pageTitle) {
		this.title = pageTitle;
	}
	
	
	
	
}
