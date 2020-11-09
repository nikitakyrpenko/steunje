package com.negeso.module.translator.service.replace;

public class AmpReplacer implements IReplacer {
	
	private static final String AMP = "&amp;";
	private static final String AMP_REPLACER = "9am9";

	@Override
	public String replaceBefore(String text) {
		return text.replace(AMP, AMP_REPLACER);
	}

	@Override
	public String replaceAfter(String text) {
		return text.replaceAll(AMP_REPLACER, AMP);
	}

}
