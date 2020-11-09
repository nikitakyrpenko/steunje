package com.negeso.module.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.module.document.domain.Category;
import com.negeso.module.media_catalog.domain.FolderDomain;

public class State {
	private Long currentFolderId = null;
	private Long parentFolderId = null;
	private String mode = null;
	private String searchWord = null;
	private static Logger logger = Logger.getLogger( State.class );
	
	public State(RequestContext request, Connection conn){
		if (request.getParameter("current_folder_id")==null || request.getParameter("current_folder_id").equals("")){
			currentFolderId = null;
			try{
				PreparedStatement stat = conn.prepareStatement(" SELECT id FROM dc_category WHERE parent_id IS NULL");
				ResultSet res = stat.executeQuery();
				if (res.next()){
					currentFolderId = new Long(res.getLong(1));
				}
				res.close();
			}
			catch (Exception e){
				logger.error("Error while instantiating State");
				logger.error(e.getMessage(),e);
			}
		}
		else{
			currentFolderId = new Long(request.getParameter("current_folder_id"));
		}
		
		if (request.getParameter("parent_folder_id")==null || request.getParameter("parent_folder_id").equals("")){
			parentFolderId = null;
		}
		else{
			parentFolderId = new Long(request.getParameter("parent_folder_id"));
		}
		
		if (request.getParameter("mode")==null || request.getParameter("mode").equals("")){
			mode = null;
		}
		else{
			mode = request.getParameter("mode");
		}
		
		if (request.getParameter("search_word")==null || request.getParameter("search_word").equals("")){
			searchWord = null;
		}
		else{
			searchWord = request.getParameter("search_word");
		}
	}
	
	public Long getCurrentFolderId(){
		return currentFolderId;
	}
	
	public Long getParentFolderId(){
		return parentFolderId;
	}
	
	public String getMode(){
		return mode;
	}
	
	public String getSearchWord(){
		return searchWord;
	}
}
