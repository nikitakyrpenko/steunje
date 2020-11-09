package com.negeso.module.translator.service.replace;

public class FontReplecer implements IReplacer {
	
	private static final String FONT = "<font";
	private static final String FONT_END = "</font";
	private static final String FONT_REPLACER = "<zfnt";
	private static final String FONT_END_REPLACER = "</zfnt";
	
	@Override
	public String replaceBefore(String text) {
		return text.replace(FONT, FONT_REPLACER).replace(FONT_END, FONT_END_REPLACER);
	}

	@Override
	public String replaceAfter(String text) {
		return text.replace(FONT_REPLACER, FONT).replace(FONT_END_REPLACER, FONT_END);
	}

}
