package com.negeso.framework.menu.bo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.negeso.framework.Env;
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "menu", namespace = Env.NEGESO_NAMESPACE)
public class JaxbMenuWraper {

	@XmlElement(name = "menu_item", namespace = Env.NEGESO_NAMESPACE)	
	private List<Menu> menu;
	@XmlAttribute
	private Long selectedMenuId = null;
	@XmlAttribute
	private boolean truncate = true;
	@XmlAttribute
	private String  langCode;
	
	public JaxbMenuWraper() {
	}
	public JaxbMenuWraper(Long selId) {
		selectedMenuId = selId;
	}


	public JaxbMenuWraper(Long selId, boolean truncate) {
		this.selectedMenuId = selId;
		this.setTruncate(truncate);
	}
	public List<Menu> getMenu() {
		return menu;
	}
	
	public void setMenu(List<Menu> menu) {
		this.menu = menu;
	}

	public void setSelectedMenuId(Long selectedMenuId) {
		this.selectedMenuId = selectedMenuId;
	}

	public Long getSelectedMenuId() {
		return selectedMenuId;
	}
	
	public void setTruncate(boolean truncate) {
		this.truncate = truncate;
	}
	
	public boolean isTruncate() {
		return truncate;
	}
	
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public String getLangCode() {
		return langCode;
	}
	
	
	
}
