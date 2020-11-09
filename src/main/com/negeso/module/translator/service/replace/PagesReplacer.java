package com.negeso.module.translator.service.replace;

import java.util.Map;
import java.util.Map.Entry;

public class PagesReplacer implements IReplacer {
	
	private Map<String, String> pages = null;

	@Override
	public String replaceBefore(String text) {
		if (pages != null) {
			for (Entry<String, String> entry : pages.entrySet()) {
				text = text.replace(entry.getKey(), entry.getValue());
			}
		}
		return text;
	}

	@Override
	public String replaceAfter(String text) {
		return text;
	}

	public void setPages(Map<String, String> pages) {
		this.pages = pages;
	}

}
