package com.negeso.module.document.component;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.document.State;
import com.negeso.module.document.domain.SortOrder;
import com.negeso.module.document.generators.CategoryXmlBuilder;

public class DocumentCatalogComponent implements PageComponent{
	private static Logger logger = Logger.getLogger( DocumentCatalogComponent.class );
	
	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element document_catalog_component = Xbuilder.createEl(document, "document_catalog_component", null);
		Connection conn = null;
		try{
			conn = DBHelper.getConnection();
			State state = new State(request, conn);
			
			document_catalog_component.setAttribute("authorization", "authorized");
			if (state.getMode()!= null && state.getMode().equals("search")){
				document_catalog_component.setAttribute("mode", "search");
				renderSearchRequest(conn, document_catalog_component, state, request);
			}
			else{
				renderDocumentCatalogRequest(conn, document_catalog_component, request, state);
			}
		}
		catch (Exception e){
			logger.error("Error in DocumentCatalogComponent");
			logger.error(e.getMessage(),e);
			document_catalog_component.setAttribute("error", "error");
		}
		finally{
			DBHelper.close(conn);
		}
		return document_catalog_component;
	}
	
	private Element renderDocumentCatalogRequest(Connection conn, Element parent, RequestContext request, State state) 
				throws Exception{
		Element document_catalog = Xbuilder.addEl(parent, "document_catalog", null);
		if (state.getCurrentFolderId()!= null){
			document_catalog.setAttribute("currentFolderId", ""+state.getCurrentFolderId().longValue());
		}
		if (state.getParentFolderId()!= null){
			document_catalog.setAttribute("parentFolderId", ""+state.getParentFolderId().longValue());
		}
		Long userId =null;
		if (request.getSession().getUser() != null){
			userId = request.getSession().getUser().getId(); 
		}
		CategoryXmlBuilder.buildVisitorCategoryXml(state.getCurrentFolderId(), conn, document_catalog, userId, 
				request.getSession().getLanguage().getId(), SortOrder.getSortOrder(request));
		/*if (request.getSession().getUser() == null){
			DocumentListXmlBuilder.buildDocumentListXmlUnauthorized(conn, document_catalog, 
					state, request.getSession().getLanguage().getId());
		}
		else{
			DocumentListXmlBuilder.buildDocumentListXml(conn, document_catalog, 
					state, request.getSession().getUser().getId(), request.getSession().getLanguage().getId());
		}*/
		return document_catalog;
	}
	
	private Element renderSearchRequest(Connection conn, Element parent, State state, RequestContext request) 
			throws Exception{
		Element search_results = Xbuilder.addEl(parent, "search_results", null);
		
		Xbuilder.addEl(parent, "search_word", state.getSearchWord());
		
		Long userId = null;
		if (request.getSession().getUser()!= null){
			userId = request.getSession().getUser().getId(); 
		}
		CategoryXmlBuilder.buildSearchResultsXml(conn, search_results, state.getSearchWord(), userId, SortOrder.getSortOrder(request));
		return search_results;
	}
	

}
