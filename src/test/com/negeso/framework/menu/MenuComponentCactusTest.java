/*
 * @(#)$Id: MenuComponentCactusTest.java,v 1.2.1.5, 2006-11-13 11:59:35Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.ComponentManager;
import com.negeso.framework.page.PageComponent;


/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 9$
 *
 */
public class MenuComponentCactusTest extends PageComponentTestCase {
	
	private static final int EN_LANG_ID 	= 0;
	private static final int NL_LANG_ID 	= 1;
	
	/*-------- test language --------*/
	private static int CURR_LANG_CONF 				= EN_LANG_ID;
	
	/*
	 * --------TESTING PURPOSES-----------
	 * */
	
	
	private static final int FEATURE_HREF_ID 	= 0;
	private static final int FEATURE_TITLE_ID 	= 1;
	
	private static Map<String, String[][]> TEST_VALUES;
	private static Map<String, String[][]> TEST_VALUES_SUB;
	
	private static final Map<String, String [/*WHICH FEATURE: href, title..*/][/*PAGE NUMBER*/]> 

			TEST_VALUES_EN = new LinkedHashMap<String, String[][]>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -2406235233642796546L;

				{
					put("mainMenu",new String[][]{
							{"about_us_en.html","news_faq_en.html","products_en.html","download_en.html","intranet_extranet_en.html","contact_en.html"},
							{"About Negeso","News & FAQ","Products","Download","Intranet/Extranet","Contact us"}
					});
					
					put("about_submenu",new String[][]{
							{"simple_page_en.html","complex_page_en.html","news_en.html"},
							{"Our organization","Our services","News"}
					});
					
					put("faq_submenu",new String[][]{
							{"newsline_en.html","newsletter_en.html","events_list_en.html","events_calendar_en.html","faq_en.html","faq_linked_en.html","marquee_en.html","glossary_en.html"},
							{"Newsline","Newsletter","Events","Events calendar","FAQ","FAQ linked","Marquee","Glossary"}
					});
					
					put("products_submenu",new String[][]{
							{"photo_album_en.html","product_module_en.html","job_module_en.html","inquiry_en.html"},
							{"Photo album","Product module","Job module","Inquiry module"}
					});
					
					put("download_submenu",new String[][]{
							{"free_download_en.html", "document_module_en.html"},
							{"Free download", "Document module"}
					});
					
					put("contuct_submenu",new String[][]{
							{"route_map_en.html","contact_form_en.html", "contact_book_en.html", "guestbook_en.html","post_en.html"},
							{"Route map","Contact form", "Contact book", "Guestbook","Post message"}
					});
					
					put("team_sub_submenu",new String[][]{
							{"simple_page_level_3_en.html"},
							{"Our team"}
					});
					
					put("expired_page",new String[][]{
							{"expired_page_en.html"},
							{"Expired page"}
					});
				}
	};
	
	private static final Map<String, String [/*PAGE NUMBER:1,2,3..*/][/*WHICH FEATURE: title, href..*/]> 

				TEST_VALUES_NL = new LinkedHashMap<String, String[][]>() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -7609612267032208198L;

					{
						put("mainMenu",new String[][]{
								{"about_us_en.html","news_faq_nl.html","products_nl.html","download_nl.html","intranet_extranet_nl.html","contact_nl.html"},
								{"Over Negeso","Nieuws & vragen","Producten","Downloaden","Intranet / Extranet","Contact"}
						});
						
						put("about_submenu",new String[][]{
								{"simple_page_nl.html","complex_page_nl.html","news_nl.html"},
								{"Wat doet Negeso","Waarom Negeso","Nieuws","menu_item"}
						});
						
						put("faq_submenu",new String[][]{
								{"newsline_nl.html","newsletter_nl.html","events_list_nl.html","events_calendar_nl.html","faq_nl.html","faq_linked_nl.html","marquee_nl.html","glossary_nl.html"},
								{"Nieuwsline","Nieuwsbrief","Evenementen","Evenementen kalender","FAQ","FAQ uitgebreid","Marquee","Glossary"}
						});
						
						put("products_submenu",new String[][]{
								{"photo_album_nl.html","product_module_nl.html","job_module_nl.html","inquiry_nl.html"},
								{"Foto album","Producten module","Vacature module","Enquête module"}
						});
						
						put("download_submenu",new String[][]{
								{"free_download_nl.html", "document_module_nl"},
								{"Gratis downloaden", "Documenten module"}
						});
						
						put("contuct_submenu",new String[][]{
								{"route_map_nl.html","contact_form_nl.html", "contact_book_nl.html", "guestbook_nl.html","post_nl.html"},
								{"Route beschriving","Contact formulier", "Smoelenboek", "Gastenboek","Contactformulier"}
						});
						
						put("team_sub_submenu",new String[][]{
								{"simple_page_level_3_nl.html"},
								{"Ons team"}
						});
						
						put("expired_page",new String[][]{
								{"expired_page_nl.html"},
								{"ExpiredPage"}
						});
					}
		};
	
	
	
	private PageComponent component;
	
	public MenuComponentCactusTest() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDBLanguage();
		
		switch(CURR_LANG_CONF){
			case(EN_LANG_ID):
				TEST_VALUES = TEST_VALUES_EN;
				TEST_VALUES_SUB = new LinkedHashMap<String, String[][]>();
				TEST_VALUES_SUB.put("team_sub_submenu", TEST_VALUES_EN.get("team_sub_submenu"));
				
				break;
			case(NL_LANG_ID):
				TEST_VALUES = TEST_VALUES_NL;
				TEST_VALUES_SUB = new LinkedHashMap<String, String[][]>();
				TEST_VALUES_SUB.put("team_sub_submenu", TEST_VALUES_NL.get("team_sub_submenu"));
		}
		
		component = new MenuComponent();
	}
	
	private void setDBLanguage() throws SQLException{
		
		Connection con = DBHelper.getConnection();
		
		String query = "select core_property.value from core_property where id = ? ";
		
		PreparedStatement stat = con.prepareStatement(query);
		stat.setObject(1, 21, Types.BIGINT);
		
		ResultSet rs = stat.executeQuery();
		
		String lang = "";
		
		while (rs.next()){
			lang = rs.getString(1);
		}
		
		if (lang.equals("en")){
			CURR_LANG_CONF = EN_LANG_ID;
		}else{
			CURR_LANG_CONF = NL_LANG_ID;
		}
		rs.close();
		stat.close();
		con.close();
	}
	
	public void testNotVisibleExpiredMenuItems() throws TransformerException, XpathException {
		getParameters().put(ComponentManager.RUNTIME_PARAM_VISITOR_MODE, "true");
		Element element = getElement(component);
		
		Document document = getDocument(element);
		
		/*assertCommonXml(document);*/
		
		
		assertExpiredPageNotVisible(document);
	}
	
	private void assertExpiredPageNotVisible(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathNotExists("/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][3]", document);		
	}
	
	public void testVisibleExpiredMenuItems() throws TransformerException {
		
		Element element = getElement(component);
		
		Document document = getDocument(element);
		
		//assertCommonXml(document);  @TODO: The test is not correct
		//assertExpiredPageVisible(document);
	}
	
	private void assertExpiredPageVisible(Document document) throws TransformerException, XpathException {
		
		String[][] menu = TEST_VALUES.get("expired_page");
		
		XMLAssert.assertXpathExists("/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][2]", document);
		XMLAssert.assertXpathEvaluatesTo(menu[FEATURE_HREF_ID][0], "/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][3]/@href",
				document);
		XMLAssert.assertXpathEvaluatesTo(menu[FEATURE_TITLE_ID][0], "/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][3]/@title",
				document);
	}
	
	private Document getDocument(Element element) {
		
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		return document; 
		
	}
	
	private void assertCommonXml(Document document) throws TransformerException, XpathException {
		assertNotNull(document);
		
		// make sure that only one root menu element 
		XMLAssert.assertXpathEvaluatesTo(
				"1", 
				"count(/*[local-name()='menu'])",
				document);
		
		
		String[][] menu = TEST_VALUES.get("mainMenu");

        //checks number of submenus
		XMLAssert.assertXpathEvaluatesTo(
				/*6*/
				String.valueOf(menu[0].length),
				"count(/*[local-name()='menu']/*[local-name()='menu_item'])",
				document);
		
        for (int i = 0 ; i < menu[0].length; i++){

            XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title", 
					document);
		}

		
		//check each submenu
		assertAboutUsMenuItem(document);
		assertNewsAndFaqMenuItem(document);
		assertProductsMenuItem(document);		
		assertDownloadMenuItem(document);
		assertContactUsMenuItem(document);
	}

	
	/*
	 * GENERAL TESTING
	 * 
	 * 1. Testing for correct menu counts
	 * 2. Testing all menus for correct: - hrefs
	 * 									 - titles					
	 */
	
	private void assertAboutUsMenuItem(Document document) throws TransformerException, XpathException {
		
		
		String[][] menu = TEST_VALUES.get("about_submenu");
		
		
		XMLAssert.assertXpathExists(
				"/*[local-name()='menu']/*[local-name()='menu_item'][1]/*[local-name()='menu']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"2", 
				"/*[local-name()='menu']/*[local-name()='menu_item'][1]/*[local-name()='menu']/@level",
				document);
				
		
		for (int i = 0 ; i < menu[0].length; i++){
			XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][1]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][1]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href",
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][1]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title",
					document);
		}
		
	}
	
	
	private void assertNewsAndFaqMenuItem(Document document) throws TransformerException, XpathException {
		
		String[][] menu = TEST_VALUES.get("faq_submenu");
		
		
		XMLAssert.assertXpathExists(
				"/*[local-name()='menu']/*[local-name()='menu_item'][2]/*[local-name()='menu']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"2", 
				"/*[local-name()='menu']/*[local-name()='menu_item'][2]/*[local-name()='menu']/@level",
				document);
		
		for (int i = 0 ; i < menu[0].length; i++){
			XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][2]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][2]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href",
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][2]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title",
					document);
		}
	}

	private void assertDownloadMenuItem(Document document) throws TransformerException, XpathException {
		
		
		String[][] menu = TEST_VALUES.get("products_submenu");
		
		
		XMLAssert.assertXpathExists(
				"/*[local-name()='menu']/*[local-name()='menu_item'][3]/*[local-name()='menu']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"2", 
				"/*[local-name()='menu']/*[local-name()='menu_item'][3]/*[local-name()='menu']/@level",
				document);
		
		for (int i = 0 ; i < menu[0].length; i++){
			XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][3]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][3]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href",
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][3]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title",
					document);
		}
	}
	
	private void assertProductsMenuItem(Document document) throws TransformerException, XpathException {
		
		String[][] menu = TEST_VALUES.get("download_submenu");
		
		
		XMLAssert.assertXpathExists(
				"/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"2", 
				"/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/@level",
				document);
		
		for (int i = 0 ; i < menu[0].length; i++){
			XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href",
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][4]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title",
					document);
		}
	}
	
	private void assertContactUsMenuItem(Document document) throws TransformerException, XpathException {
		
		String[][] menu = TEST_VALUES.get("contuct_submenu");
		
		
		XMLAssert.assertXpathExists(
				"/*[local-name()='menu']/*[local-name()='menu_item'][6]/*[local-name()='menu']", 
				document);
		XMLAssert.assertXpathEvaluatesTo(
				"2", 
				"/*[local-name()='menu']/*[local-name()='menu_item'][6]/*[local-name()='menu']/@level",
				document);
		
		for (int i = 0 ; i < menu[0].length; i++){
			XMLAssert.assertXpathExists(
					"/*[local-name()='menu']/*[local-name()='menu_item'][6]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]", 
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_HREF_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][6]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@href",
					document);
			XMLAssert.assertXpathEvaluatesTo(
					menu[FEATURE_TITLE_ID][i], 
					"/*[local-name()='menu']/*[local-name()='menu_item'][6]/*[local-name()='menu']/*[local-name()='menu_item'][" + (i + 1) + "]/@title",
					document);
		}
	}

}