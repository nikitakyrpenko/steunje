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
package com.negeso.module.translator.service;

import com.negeso.module.translator.Translit;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.replace.IReplacer;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public abstract class AbstractTranslateService implements ITranslateService {
	
	
	private boolean isPageNameTranslatable = false;
	
	private static String translitableLanguage = "ru";
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	protected int textLengthLimitation = 950;	
	private static final Pattern NOT_HTML_TAG = Pattern.compile("(^|>)([^><]+?)(?=<|$)");
	private static final Pattern TITLE_ATTRIBUTE_PATTERN = Pattern.compile("(<[^>]*title=\")([^\"]*)");
	
	private static final char[] charsForGoodSplitting = {'.', '!', '?', '<', ' '};
		
	private int charsCounter = 0;

	protected boolean isWithoutHtmlTags = false;
	protected boolean isGetMethod;
	protected List<IReplacer> replacers = new LinkedList<IReplacer>();
	
	public AbstractTranslateService() {
	}

	@Override
	public String[] translate(String[] text, String fromLangCode,
			String toLangCode) {
		if (text == null || text.length == 0) {
			return text;
		}
	    String[] translatedArray = text;
	    
	    //else make translation request for each array item
	    translatedArray = new String[text.length];
		for (int i = 0; i < translatedArray.length; i++) {
			if (StringUtils.isNotBlank(text[i])) {
				translatedArray[i] = translate(text[i], fromLangCode, toLangCode);
			} else {
				translatedArray[i] = StringUtils.EMPTY;
			}
		}
		return translatedArray;
	}

	@Override
	public String translateHtml(String html, String fromLangCode,
			String toLangCode) {
		return translate(html, fromLangCode, toLangCode);
	}

	@Override
	public String translate(String innerText, String fromLangCode,
			String toLangCode) {
		String text = innerText;
		if (StringUtils.isBlank(text)) {
			return text;
		}
		for (IReplacer replacer: replacers) {
			text = replacer.replaceBefore(text);
		}
		if (StringUtils.isNotBlank(text)) {
			if (isWithoutHtmlTags) {
				text = translateTextPartsByPattern(text, fromLangCode, toLangCode, NOT_HTML_TAG);
				text = translateTextPartsByPattern(text, fromLangCode, toLangCode, TITLE_ATTRIBUTE_PATTERN);
			} else {
				text = innerTranslate(text, fromLangCode, toLangCode);				
			}		
		}
		for (IReplacer replacer: replacers) {
			text = replacer.replaceAfter(text);
		}
		return text;
	}

	private String translateTextPartsByPattern(String text, String fromLangCode, String toLangCode,
			Pattern p) {
		Matcher m = p.matcher(text);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		while(result) {
			String word = m.group(2);
			StringBuffer beforeWhiteSpaces = new StringBuffer(word.length());
			StringBuffer afterWhiteSpaces = new StringBuffer(1);
			if (StringUtils.isNotBlank(word)) {
				for (int i = 0; i < word.length(); i++) {
					if (word.charAt(i) == ' ') {
						beforeWhiteSpaces.append(' ');
					} else {
						break;
					}
				}
				for (int i = word.length() - 1; i >= 0; i--) {
					if (word.charAt(i) == ' ') {
						afterWhiteSpaces.append(' ');
					} else {
						break;
					}
				}
			}
			word = beforeWhiteSpaces.append(innerTranslate(word, fromLangCode, toLangCode).trim()).append(afterWhiteSpaces).toString(); 
			m.appendReplacement(sb, "$1" + word);
			result = m.find();
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		Pattern p = Pattern.compile("(<[^>]*title=\")([^\"]*)");
//		Matcher m = p.matcher("asd title=\"Chat with a sales consultant now\" asd <a title=\"Chat with a sales consultant now\" href=\"javascript:displayPopup('livesupport_en.html');\" rel=\"nofollow\" style=\"color: #fff; border: none; font: bold 16px Arial, Helvetica, sans-serif;\"><span title=\"\" style=\"color: #ffffff;\">Live chat with our<br>sales consultant</span></a>");
//		boolean b = m.find();
//		StringBuffer sb = new StringBuffer();
//		while (b) {
//			System.out.println(m.group(2));
//			m.appendReplacement(sb, "$1" + "11");
//			b = m.find();
//		}
//		m.appendTail(sb);
//		System.out.println(sb);
//	}

	private String innerTranslate(String text, String fromLangCode,
			String toLangCode) {
		if (StringUtils.isNotBlank(text)) {
			try {
				charsCounter += text.length();
				return splitBigText(text, fromLangCode, toLangCode);
			} catch (UnsupportedEncodingException e) {
				throw new TranslationExeption(e);
			}
			
		}
		return text;
	}
	
	public String splitBigText(String text, String fromLangCode, String toLangCode) throws UnsupportedEncodingException {
		String textPart = text;
		int textLength = 0;
		if (isGetMethod) {
			textLength = calculateLengthTakingIntoAccountURLEncoding(text);
			
			if (textLength == text.length()) {		
				return translateTextPart(fromLangCode, toLangCode, textPart);
			}
			
		}else {//POST
			if (textLengthLimitation >= text.length()) {
				return translateTextPart(fromLangCode, toLangCode, textPart);
			}
			textLength = textLengthLimitation;
		}
		
		String tmpText = text.substring(0, textLength);
		
		if (tmpText.lastIndexOf('<') > tmpText.lastIndexOf('>') && tmpText.lastIndexOf('<') != 0) {
			textPart = tmpText.substring(0, tmpText.lastIndexOf('<'));
			text = text.substring(tmpText.lastIndexOf('<'));
		} else {
			for (char c : charsForGoodSplitting) {
				if ((tmpText.lastIndexOf(c) > tmpText.lastIndexOf('>')) || (c == '<' && tmpText.lastIndexOf(c) > 0)) {
					textPart = tmpText.substring(0, tmpText.lastIndexOf(c));
					text = text.substring(tmpText.lastIndexOf(c));
					break;
				}
			}
		}
		
		return new StringBuffer(textLengthLimitation)
					.append(translateTextPart(fromLangCode, toLangCode, textPart))
					.append(splitBigText(text, fromLangCode, toLangCode))
					.toString();
	}

	private int calculateLengthTakingIntoAccountURLEncoding(String text) throws UnsupportedEncodingException {
		int encodedLangth = 0;
		int realLength = 0;
		for (int i = 0; i < text.length(); i++) {
			int charLength = URLEncoder.encode(text.charAt(i) + StringUtils.EMPTY, DEFAULT_ENCODING).length();
			if (encodedLangth + charLength > textLengthLimitation) {
				break;
			}
			encodedLangth += charLength;
			realLength++;
		}
		return realLength;
	}


	abstract public String translateTextPart(String fromLangCode, String toLangCode, String textPart); 

	@Override
	public String translatePageName(String pageName, String fromLangCode,
			String toLangCode) {
		
		if (pageName == null) {
			return null;
		}
		
		String pageNameWithoutCode = pageName.replace("_" + fromLangCode + ".", ".").replace(".html", StringUtils.EMPTY);
		
		if (isPageNameTranslatable && !"index".equals(pageNameWithoutCode) && !"news".equals(pageNameWithoutCode)) {
			pageName = pageNameWithoutCode.replace("_", " ").replace("-", " ");
			pageName = translate(pageName, fromLangCode, toLangCode).replace(" ", "_").replace("-", "_").replace(".", StringUtils.EMPTY).toLowerCase();
			if (translitableLanguage.equals(toLangCode)) {
				pageName = Translit.toTranslit(pageName);
			}
		} else {
			pageName = pageNameWithoutCode;
		}
		return pageName.concat("_" + toLangCode + ".html");
	}

	

	@Override
	public void setPageNameTranslatable(boolean isPageNameTranslatable) {
		this.isPageNameTranslatable = isPageNameTranslatable;
	}
	
	@Override
	public void setWithoutHtmlTags(boolean isWithoutHtmlTags) {
		this.isWithoutHtmlTags = isWithoutHtmlTags;
	}

	@Override
	public int getCharsCounter() {
		return charsCounter;
	}

	public List<IReplacer> getReplacers() {
		return replacers;
	}


}
