package com.negeso.module.translator.service.replace;

public class WhiteSpaceReplacer implements IReplacer {

	private static final String WHITESPACE = "&nbsp;";
	private static final String WHITESPACE_REPLACER = " 9ws9 ";
	private static final String WHITESPACE_BACK_REPLACER = " ?9ws9 ?";

	@Override
	public String replaceBefore(String text) {
		return text.replace(WHITESPACE, WHITESPACE_REPLACER);
	}

	@Override
	public String replaceAfter(String text) {
		return text.replaceAll(WHITESPACE_BACK_REPLACER, WHITESPACE);
	}

}
