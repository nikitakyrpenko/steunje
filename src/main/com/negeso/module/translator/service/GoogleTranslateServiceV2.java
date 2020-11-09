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

import com.negeso.framework.Env;
import com.negeso.module.translator.Configuration;
import com.negeso.module.translator.exception.TranslationExeption;
import com.negeso.module.translator.service.replace.FontReplecer;
import com.translator.google.translator.OnlineGoogleTranslator;
import com.translator.google.translator.OnlineGoogleTranslator.Language;
import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class GoogleTranslateServiceV2 extends AbstractTranslateService{
	
	private static final Logger logger = Logger.getLogger(GoogleTranslateServiceV2.class);
	
	private OnlineGoogleTranslator translator = null;;
	
	public GoogleTranslateServiceV2() {
		translator = OnlineGoogleTranslator.createInstance(Env.getNotCachedSiteProperty(Configuration.NEGESO_GOOGLE_API_KEY, Configuration.NEGESO_GOOGLE_API_KEY_VALUE));
		this.isGetMethod = true;
		replacers.add(new FontReplecer());
	}
	
	public String translateTextPart(String fromLangCode, String toLangCode,
			String textPart) {
		try{
			textPart = translator.translate(textPart, getLanguage(fromLangCode), getLanguage(toLangCode)).getTranslatedText();
		}catch (Exception e) {
			logger.error(e);
			throw new TranslationExeption(e);
		}
		return textPart;
	}
	
	@Override
	public com.negeso.framework.domain.Language[] getSupportedLaguages() {
		com.negeso.framework.domain.Language[] supportedLaguages = new com.negeso.framework.domain.Language[Language.values().length];
		for (int i = 0; i < supportedLaguages.length; i++) {
			supportedLaguages[i] = new com.negeso.framework.domain.Language(null, Language.values()[i].toString(), Language.values()[i].value);
		}
//		Arrays.sort(supportedLaguages);
		return supportedLaguages;
	}
	
	public static Language getLanguage(String langCode) {
		for (Language language : Language.values()) {
			if (language.value.equalsIgnoreCase(langCode)) {
				return language;
			}
		}
		throw new TranslationExeption("Language code " + langCode + "could not be found in Google languages list");
	}
	
		
		public static void main(String[] args) {
//			OnlineGoogleTranslator translator = OnlineGoogleTranslator.createInstance("AIzaSyC0UF9XNyfbh65uZkrp6Q1qhaMq1qRi8O4");
//	        Translation translation = null;
//			try {
//				translation = translator.translate("<head><div title=\"amount of peace\" style=\"width: 10px;\">Hello _word_</div></head>", getLanguage("en"), getLanguage("ru"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	//
//	        
//	        System.out.printf("In %s text: %s\n", "rr", translation.getTranslatedText());
					
//			    Pattern p = Pattern.compile(">(.*?)<");
//				Matcher m = p.matcher("<dd>ddddd<y> sdfsdf </sdf> sdf<sdf>sdf <ppf> sdfsdf<sdf><1><1><1><><y> sd<sd>");
//				StringBuffer sb = new StringBuffer();
//				boolean result = m.find();
//				while(result) {
//					String word = m.group(0);
//					System.out.println(word);
//					result = m.find();
//				}
			
			GoogleTranslateServiceV2 trServiceV2 = new GoogleTranslateServiceV2();
			trServiceV2.setTranslator(OnlineGoogleTranslator.createInstance("AIzaSyC0UF9XNyfbh65uZkrp6Q1qhaMq1qRi8O4"));
			//textLengthLimitation = 111;
			String str = "<p align=\"justify\">%SITEMENTRIX% is een online Website Builder en professioneel&nbsp;Content Management Systeem (CMS), met veel standaard opties. Met %SITEMENTRIX% kan iedereen zelf een website ontwerpen en aanpassen.</p><p>&nbsp;</p><table border=\"0\" width=\"80%\" align=\"center\"><tbody><tr><td align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff; width: 70px; border-top: 2px dashed #d4d4d4\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff; border-top: 2px dashed #d4d4d4\"><h2>Ontwerp zelf uw website</h2><p align=\"justify\">Stel zelf het uiterlijk van uw website samen met de <a href=\"http://builder.sitementrix.com/\" title=\"Zelf uw %SITEMENTRIX% website ontwerpen en bouwen met uw browser\">Builder</a>. Kies uit meer dan 100.000 ontwerpcombinaties. U kunt naderhand op elk gewenst moment het uiterlijk van uw website aanpassen, m&eacute;t behoud van uw pagina&#39;s.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Gebruik uw eigen logo en banner</h2><p>Upload uw logo en banner. Deze worden automatisch op alle pagina&#39;s geplaatst.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Gebruik uw eigen domeinnaam</h2><p>Gebruik zoveel domeinnamen als u wenst! U kunt ook <a href=\"../domein_registratie_nl.html\" title=\"Domeinnaam registreren tegen scherpe prijs\">domeinnamen</a> registreren tegen scherpe prijzen.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Eenvoudig uw pagina&#39;s aanpassen</h2><p align=\"justify\">Geen technische vaardigheden vereist en u heeft geen kennis van HTML of programmeren nodig.&nbsp;U hoeft geen software te installeren. U kunt de pagina&#39;s van uw website met uw browser vanaf elke plek aanpassen. Het werkt als een tekstverwerker.&nbsp;<em>Just point and click</em>!</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>100% reclamevrij</h2><p>Gegarandeerd reclamevrije website en e-mail. </p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Menu editor</h2><p>Pas het menu aan naar uw wens.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Onbeperkt aantal pagina&#39;s</h2><p>Maak zoveel pagina&#39;s als u wilt. U wordt niet beperkt in het gebruik van %SITEMENTRIX%</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Formulieren</h2><p>Laat uw bezoekers een formulier invullen. De antwoorden worden naar u toe gemaild. Maak zoveel formulieren als u wilt. U wordt niet beperkt in het gebruik van %SITEMENTRIX%<br /></p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Fotoalbum</h2><p>Toon uw foto&#39;s in een <em>slide show</em>.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Web poll</h2><p>Doe zelf een (markt-)onderzoek. Vraag de mening van uw bezoekers over een stelling, uw organisatie, producten&nbsp;of diensten.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Smoelenboek</h2><p>Presenteer&nbsp;uw leden of collega&#39;s. Beveiligbaar met een wachtwoord.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Gastenboek</h2><p>Bezoekers kunnen een openbaar bericht op uw website plaatsen.</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Klaar voor Google</h2><p>Volg de bezoekstatistieken van uw website met <a href=\"http://www.google.com/webmasters/tools/\" target=\"_blank\" title=\"Google Web Master Tools (gratis)\">Google Web Master Tools</a> (gratis) en <a href=\"http://www.google.com/analytics/\" target=\"_blank\" title=\"Google Analytics (gratis)\">Google Analytics</a>&nbsp;(gratis).</p></td></tr><tr><td class=\"price\" align=\"center\" valign=\"middle\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #fff1ff\"><img style=\"width: 35px; height: 35px\" src=\"../rte/media/bluetick.png\" alt=\"\" width=\"35\" height=\"35\" /></td><td class=\"price\" style=\"border-bottom: 2px dashed #d4d4d4; background-color: #e9f3ff\"><h2>Scherp geprijsd</h2><p>Vanaf %PRICE_FROM_SM%. Geen automatische verlenging!&nbsp;Kostenloos 1 maand op <a href=\"http://builder.sitementrix.com/\" target=\"_blank\">proef</a>.</p></td></tr></tbody></table><p>&nbsp;</p><p align=\"center\">&nbsp;</p>";
			System.out.println(str);
			System.out.println(trServiceV2.translate(str, 
					"nl", "en"));
			

			
		}

		private void setTranslator(OnlineGoogleTranslator translator) {
			this.translator = translator;			
		}
}

