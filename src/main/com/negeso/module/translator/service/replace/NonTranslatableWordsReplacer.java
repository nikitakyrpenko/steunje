package com.negeso.module.translator.service.replace;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NonTranslatableWordsReplacer implements IReplacer {
	
	private static final String CASE_INSENSITIVE_PATTERN = "(?i)%s";
	private static final String NONTRANSLATABLE_WORD = "9nt%swd";
	private static final String DELIMITER = ";";
	
	private String[] nonTranslatableWords = null;
	private List<String> foundWords = new LinkedList<String>();

	@Override
	public String replaceBefore(String text) {
		foundWords.clear();
		if (nonTranslatableWords != null) {
			int i = 0;
			for (String nonTranslatableWord: nonTranslatableWords) {
				Pattern p = Pattern.compile(String.format(CASE_INSENSITIVE_PATTERN, nonTranslatableWord), Pattern.MULTILINE);
				Matcher m = p.matcher(text);
				StringBuffer sb = new StringBuffer();
				boolean result = m.find();
				while(result) {
					String word = m.group(0);
					foundWords.add(word);
					m.appendReplacement(sb, String.format(NONTRANSLATABLE_WORD, i++));
					result = m.find();
				}
				m.appendTail(sb);
				text = sb.toString();
			}
		}
		return text;
	}

	@Override
	public String replaceAfter(String text) {
		if (!foundWords.isEmpty()) {
			int i = 0;
			for (String word : foundWords) {
				text = text.replace(String.format(NONTRANSLATABLE_WORD, i++), word);
			}
		}
		return text;
	}
	
	public void setNonTranslatableWords(String nonTranslatableWords) {
		if (nonTranslatableWords != null) {
			String[] nonTranslatableWordsArray = nonTranslatableWords.split(DELIMITER);
			List<String> list = new ArrayList<String>();
			for (String word : nonTranslatableWordsArray) {
				if (StringUtils.isNotBlank(word)) {
					list.add(word);
				}
			}
			if (list.size() > 0) {
				this.nonTranslatableWords = list.toArray(new String[list.size()]);
			}
		}
	}
}
