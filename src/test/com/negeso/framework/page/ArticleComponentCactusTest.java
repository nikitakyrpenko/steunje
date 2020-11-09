/*
 * @(#)$Id: ArticleComponentCactusTest.java,v 1.5, 2006-11-13 12:25:32Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.TestPageComponentParameters;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.ObjectNotFoundException;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 6$
 *
 */
public class ArticleComponentCactusTest extends PageComponentTestCase {

	public static final String TEST_ARTICLE_ID = "230";
	
	public static final String TEST_CLASS_VALUE = "product_article";
	
	private PageComponent component;
	
	public ArticleComponentCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		component = new ArticleComponent();
	}
	
	private void validateXML(Element element, Article testArticle) throws TransformerException, XpathException {
		XMLAssert.assertNotNull(element);
		
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		
		XMLAssert.assertXpathExists("/*[local-name()='article']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='article'])", document);
		XMLAssert.assertXpathEvaluatesTo(testArticle.getId().toString(), "/*[local-name()='article']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(testArticle.getLanguage(), "/*[local-name()='article']/@lang", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='article']/*[local-name()='head']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='article']/*[local-name()='head'])", document);
		XMLAssert.assertXpathEvaluatesTo(testArticle.getHead(), "/*[local-name()='article']/*[local-name()='head']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='article']/*[local-name()='text']", document);
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='article']/*[local-name()='text'])", document);
		XMLAssert.assertXpathEvaluatesTo(testArticle.getText(), "/*[local-name()='article']/*[local-name()='text']", document);
	}
	
	public void testWithId() throws TransformerException, XpathException {
		getParameters().put("id", TEST_ARTICLE_ID);

		Article testArticle = null; 
		try {
		 testArticle = Article.findById(Long.parseLong(TEST_ARTICLE_ID));
		} catch (ObjectNotFoundException e)  {
		  fail("test environment failed or Article entity class works incorrectly");	
		}
		
		Element element = getElement(component);
		
		validateXML(element, testArticle);		
	}
	
	public void testWithClass() throws TransformerException, XpathException {

		getParameters().put("class", TEST_CLASS_VALUE);
		
		Article testArticle = null; 
		try {
		 testArticle = Article.findByClass(TEST_CLASS_VALUE, getlangCode());
		} catch (ObjectNotFoundException e)  {
		  fail("test environment failed or Article entity class works incorrectly");	
		}
	    
		Element element = getElement(component);
		
		validateXML(element, testArticle);
	}
	
}
