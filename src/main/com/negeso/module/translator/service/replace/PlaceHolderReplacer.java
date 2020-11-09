package com.negeso.module.translator.service.replace;

import com.negeso.module.core.domain.PlaceHolder;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class PlaceHolderReplacer implements IReplacer {
	
	private static final String PLECEHOLDER = "9ph%sr";
	
	private List<PlaceHolder> placeHolders = null;

	@Override
	public String replaceBefore(String text) {
		if (placeHolders != null && !placeHolders.isEmpty()) {
			for (int i = 0; i < placeHolders.size(); i++) {
				text = StringUtils.replace(text, placeHolders.get(i).getKey(), String.format(PLECEHOLDER, i));
			}
		}
		return text;
	}

	@Override
	public String replaceAfter(String text) {
		if (placeHolders != null && !placeHolders.isEmpty()) {
			for (int i = 0; i < placeHolders.size(); i++) {
				text = text.replaceAll(String.format(PLECEHOLDER, i), placeHolders.get(i).getKey());
			}
		}
		return text;
	}

	public void setPlaceHolders(List<PlaceHolder> placeHolders) {
		this.placeHolders = placeHolders;
	}

}
