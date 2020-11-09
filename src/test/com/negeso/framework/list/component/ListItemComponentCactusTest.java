/*
 * @(#)$Id: ListItemComponentCactusTest.java,v 1.2, 2006-11-20 09:30:15Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.list.component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.xml.transform.TransformerException;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.archive.domain.ArchivedListItem;
import com.negeso.module.dictionary.DictionaryUtil;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public class ListItemComponentCactusTest extends PageComponentTestCase {
    //this id have to exist in DB (list_item) and container_id must be not null
	public static final String TEST_LIST_ITEM_ID_FOR_ACCESS_DENIED = "119";
	
    //this id have to exist in DB (list_item)
	public static final String TEST_LIST_ITEM_ID = "56";
	
	public static final String TEST_ARCHIVED_LIST_ITEM_ID = "83";
	
	public static final String TEST_LIST_ITEM_ID_NOT_FOUND = "9999999";
	
	private PageComponent component;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		component = new ListItemComponent();
	}

   public void testWithoutINPUT_LIST_ITEM_IDparameter() throws TransformerException, XpathException {
		Element element = getElement(component);
		
		Document document = element.getOwnerDocument();
		document.appendChild(element);
		
        validateInvalidListItemXML(document);
   }

   private void validateInvalidListItemXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathExists("/*[local-name()='listItem']", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem'])", document);
   }
   
   public void beginAccessDenied(WebRequest request) throws TransformerException, SQLException {
	   request.addParameter(ListItemComponent.INPUT_LIST_ITEM_ID, TEST_LIST_ITEM_ID_FOR_ACCESS_DENIED);
   }
   
   public void testAccessDenied() throws TransformerException, SQLException, XpathException {
	   requestContext.getSession().destroySession();
	   requestContext.getSession().startSession();
	   // make sure user is anonymous
	   assertNull(requestContext.getSession().getUser());
	   
	   Element element = getElement(component);

		Document document = element.getOwnerDocument();
		document.appendChild(element);

		Connection conn = DBHelper.getConnection();
		try {
		//make sure container id is not null in test listItem record
			ListItem listItem = ListItem.findById(conn, Long.parseLong(TEST_LIST_ITEM_ID_FOR_ACCESS_DENIED));
			assertNotNull("test environment failed", listItem);
			assertNotNull("test environment failed", listItem.getContainerId());
		} finally {
			conn.close();
		}

	   validateAccessDeniedXML(document);
   }
   
   private void validateAccessDeniedXML(Document document) throws TransformerException, SQLException, XpathException {
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem'])" , document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem']/*[local-name()='details'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem']/*[local-name()='details']/*[local-name()='article'])", document);
	   
	   XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem']/*[local-name()='details']/*[local-name()='article']/*[local-name()='text'])", document);
	   
	   String langCode = getlangCode();
	   
	   Connection conn = DBHelper.getConnection();
	   try {
	   assertNotNull("test environment failed", conn);
	   XMLAssert.assertXpathEvaluatesTo(DictionaryUtil.findEntry(conn, "FOR_AUTHORIZED_VISITORS", langCode),
			                            "/*[local-name()='listItem']/*[local-name()='details']/*[local-name()='article']/*[local-name()='text']",
			                            document);
	   } finally {
		   conn.close();
	   }
	   
   }
  
   public void beginListItemNotFound(WebRequest request) throws TransformerException {
	   request.addParameter(ListItemComponent.INPUT_LIST_ITEM_ID, TEST_LIST_ITEM_ID_NOT_FOUND);
   }
	
   public void testListItemNotFound() throws TransformerException, XpathException {
	   Element element = getElement(component);

	   Document document = element.getOwnerDocument();
	   document.appendChild(element);
		
		validateInvalidListItemXML(document);
   	}
  
   private ListItem getTestListItem() {
	  // configure test ListItem from DB or manually
	   try {
		   Connection conn = DBHelper.getConnection();
		   try {
			   ListItem listItem = ListItem.findById(conn, Long.parseLong(TEST_LIST_ITEM_ID));
			   assertNotNull("test data failed", listItem);
			   return listItem;
    	  }	  
    	  finally {
    		  conn.close();
    	  }
      } catch (SQLException e) {
    	  fail("test data failed");
    	  return null;
      }
  }
  
  private ArchivedListItem getTestArchivedListItem() {
	  // configure test ArchivedListItem from DB or manually
      try {
    	  Connection conn = DBHelper.getConnection();
    	  try {
    		  ArchivedListItem listItem = ArchivedListItem.findByListItemId(conn, Long.parseLong(TEST_ARCHIVED_LIST_ITEM_ID));
    		  assertNotNull("test data failed", listItem);
   	   	  	  return listItem;
    	  } finally {
    		  conn.close();
    	  }
      } catch (SQLException e) {
    	  fail("test data failed");
    	  return null;
      }
  }
  
  public void beginListItem(WebRequest request) throws Exception {
	  request.addParameter(ListItemComponent.INPUT_LIST_ITEM_ID, TEST_LIST_ITEM_ID);
  }
  
  public void testListItem() throws TransformerException, CriticalException, com.negeso.framework.domain.ObjectNotFoundException, XpathException {
	  Element element = getElement(component);

	   //make sure test ListItem exists in db
       ListItem listItem = getTestListItem();
       
       Document document = element.getOwnerDocument();
       document.appendChild(element);
       XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='listItem'])", document);
       validateListItemAttrubutes(document, listItem);
       validateArticleXML(document,
				           "/*[local-name()='listItem']/*[local-name()='teaser']",
				           Article.findById(listItem.getTeaserId()));
       validateArticleXML(document,
				           "/*[local-name()='listItem']/*[local-name()='details']",
				           Article.findById(listItem.getArticleId()));
   }
  
  public void beginArchivedListItem(WebRequest request) throws TransformerException {
	  request.addParameter(ListItemComponent.INPUT_LIST_ITEM_ID, TEST_ARCHIVED_LIST_ITEM_ID);
  }
  
  public void testArchivedListItem() throws TransformerException, XpathException {
	   Element element = getElement(component);

	   //make sure test ArchivedListItem exists in db
       ArchivedListItem listItem = getTestArchivedListItem();
       
       Document document = element.getOwnerDocument();
       document.appendChild(element);
		
       validateArchivedListItemXML(document, listItem);
  }
  
  private void validateArchivedListItemXML(Document document, ArchivedListItem listItem) throws TransformerException, XpathException {
	  XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='archivedListItem'])", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getId().toString(), "/*[local-name()='archivedListItem']/@id", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getListId().toString(), "/*[local-name()='archivedListItem']/@listId", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getTitle(), "/*[local-name()='archivedListItem']/@title", document);
	  
	  XMLAssert.assertXpathEvaluatesTo((listItem.getLink()==null?"":listItem.getLink()), "/*[local-name()='archivedListItem']/@link", document);
	  
	  XMLAssert.assertXpathEvaluatesTo((listItem.getImageLink()==null?"":listItem.getImageLink()), "/*[local-name()='archivedListItem']/@imageLink", document);
	  
	  XMLAssert.assertXpathEvaluatesTo((listItem.getDocumentLink()==null?"":listItem.getDocumentLink()), "/*[local-name()='archivedListItem']/@documentLink", document);
	  
	  XMLAssert.assertXpathEvaluatesTo((listItem.getThumbnailLink()==null?"":listItem.getThumbnailLink()), "/*[local-name()='archivedListItem']/@thumbnailLink", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getTeaserHead(), "/*[local-name()='archivedListItem']/@teaserHead", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getTeaserText(), "/*[local-name()='archivedListItem']/@teaserText", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getArticleHead(), "/*[local-name()='archivedListItem']/@articleHead", document);
	  
	  XMLAssert.assertXpathEvaluatesTo(listItem.getArticleText(), "/*[local-name()='archivedListItem']/@articleText", document);
	  
	  assertDateAttribute(listItem.getViewDate(), "/*[local-name()='archivedListItem']/@viewDate" , document);
	  assertDateAttribute(listItem.getPublishDate(), "/*[local-name()='archivedListItem']/@publishDate" , document);
	  assertDateAttribute(listItem.getExpiredDate(), "/*[local-name()='archivedListItem']/@expiredDate" , document);
	  assertDateAttribute(listItem.getCreatedDate(), "/*[local-name()='archivedListItem']/@createdDate" , document);
	  assertDateAttribute(listItem.getLastModifiedDate(), "/*[local-name()='archivedListItem']/@lastModifiedDate" , document);
	  
	  //	 TODO lastmodifiedby attribute
	  //  	 TODO createdby attribute
	  
  }
  
   private void validateArticleXML(Document document, String path, Article article) throws TransformerException, XpathException {
	   
       XMLAssert.assertXpathEvaluatesTo("1", "count(" + path + ")", document);
		
       XMLAssert.assertXpathEvaluatesTo("1", "count(" + path + "/*[local-name()='article'])", document);
		
       XMLAssert.assertXpathEvaluatesTo((article.getClass_()==null?"":article.getClass_()), path + "/*[local-name()='article']/@class", document);
		
       XMLAssert.assertXpathEvaluatesTo(article.getId().toString(), path + "/*[local-name()='article']/@id", document);
		
       XMLAssert.assertXpathEvaluatesTo(article.getLanguage(), path + "/*[local-name()='article']/@lang", document);
		
       XMLAssert.assertXpathEvaluatesTo(article.getHead(), path + "/*[local-name()='article']/*[local-name()='head']", document);
       
       System.out.println(article.getText());
       XMLAssert.assertXpathEvaluatesTo(article.getText(), path + "/*[local-name()='article']/*[local-name()='text']", document);
   }
  
   private void validateListItemAttrubutes(
		   Document document, 
		   ListItem listItem) 
   			throws TransformerException, XpathException {
	   XMLAssert.assertXpathExists("/*[local-name()='listItem']/@return-link", document);
	   
	   XMLAssert.assertXpathEvaluatesTo(listItem.getId().toString(), 
			   "/*[local-name()='listItem']/@id", document);
	   
	   XMLAssert.assertXpathEvaluatesTo(listItem.getListId().toString(), 
			   "/*[local-name()='listItem']/@listId", document);
	   
	   XMLAssert.assertXpathEvaluatesTo(String.valueOf(listItem.getOrderNumber()), 
			   "/*[local-name()='listItem']/@orderNumber", document);
	   
	   XMLAssert.assertXpathEvaluatesTo(listItem.getTitle(), 
			   "/*[local-name()='listItem']/@title", document);

	   XMLAssert.assertXpathEvaluatesTo((listItem.getDocumentLink()==null?"":listItem.getDocumentLink()), 
			   "/*[local-name()='listItem']/@documentLink", document);
	   
	   XMLAssert.assertXpathEvaluatesTo((listItem.getParameters()==null?"":listItem.getParameters()), 
			   "/*[local-name()='listItem']/@parameters", document);

	   XMLAssert.assertXpathEvaluatesTo((listItem.getContainerId() == null ? "null" : listItem.getContainerId().toString()),
			   "/*[local-name()='listItem']/@containerId", document);
	   
	   assertDateAttribute(listItem.getViewDate(), "/*[local-name()='listItem']/@viewDate", document);
	   assertDateAttribute(listItem.getPublishDate(), "/*[local-name()='listItem']/@publishDate", document);
	   assertDateAttribute(listItem.getExpiredDate(), "/*[local-name()='listItem']/@expiredDate", document);
	   assertDateAttribute(listItem.getCreatedDate(), "/*[local-name()='listItem']/@createdDate", document);
	   assertDateAttribute(listItem.getLastModifiedDate(), "/*[local-name()='listItem']/@lastModifiedData", document);
	   
       assertLinkAttribute(listItem.getLink(), "/*[local-name()='listItem']/@href", document);
       assertImageLinkAttribute(listItem.getImageLink(), "/*[local-name()='listItem']" , document);
       assertThumbnailLinkAttribute(listItem.getThumbnailLink(), "/*[local-name()='listItem']", document);

   }
   
   private void assertDateAttribute(Timestamp date, String pathToAttribute, Document document) throws TransformerException, XpathException {
	  if (date != null) {
		  String dateStr = String.valueOf(date);
		  XMLAssert.assertXpathEvaluatesTo(dateStr.substring(0, dateStr.indexOf(' ')), pathToAttribute, document); 
	  }
   }
   
  private void assertLinkAttribute(String link, String path, Document document) throws TransformerException, XpathException {
	  if (link != null && link.length() > 0) {
		  XMLAssert.assertXpathExists(path, document);
	  }
  }
  
  private void assertThumbnailLinkAttribute(String thumbnailLink, String path, Document document) throws TransformerException, XpathException {
	  if (thumbnailLink != null) {
		  try {
			  Repository.get().getImageResource(thumbnailLink).getWidth();
		  }  catch (RepositoryException ex){
			  XMLAssert.assertXpathNotExists(path + "/@th_width", document);
			  XMLAssert.assertXpathNotExists(path + "/@th_height", document);
			  XMLAssert.assertXpathNotExists(path + "/@thumbnailLink", document);
		  }
		  XMLAssert.assertXpathExists(path + "/@th_width", document);
		  XMLAssert.assertXpathExists(path + "/@th_height", document);
		  XMLAssert.assertXpathExists(path + "/@thumbnailLink", document);
	  }
  }
  
  private void assertImageLinkAttribute(String imageLink, String path, Document document) throws TransformerException, XpathException {
	  if (imageLink != null) {
		  try {
			  Repository.get().getImageResource(imageLink).getWidth();
		  }  catch (RepositoryException ex){
			  XMLAssert.assertXpathNotExists(path + "/@img_width", document);
			  XMLAssert.assertXpathNotExists(path + "/@img_height", document);
			  XMLAssert.assertXpathNotExists(path + "/@imgLink", document);
			  return;
		  }
		  XMLAssert.assertXpathExists(path + "/@img_width", document);
		  XMLAssert.assertXpathExists(path + "/@img_height", document);
		  XMLAssert.assertXpathExists(path + "/@imgLink", document);
	  }
  }
}

