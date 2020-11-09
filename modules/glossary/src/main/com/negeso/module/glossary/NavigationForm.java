package com.negeso.module.glossary;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 3, 2005
 */

public class NavigationForm {
    public static String FIRST_TIME_MODE = "first_time";
    public static String ALL_MODE = "all";
    public static String LETTER_MODE = "letter";
    public static String CATEGORY_MODE = "category";
    public static String FIND_MODE = "find";

    private String mode = null;
    private Long letter_id = null;
    private Long category_id = null;
    private String search_word = null;
    private Long word_id = null;
    private String action = null;

    private static Logger logger = Logger.getLogger( NavigationForm.class );

    public NavigationForm(RequestContext request){
        if (request.getParameter("show")==null || request.getParameter("show").equals("")){
            mode=FIRST_TIME_MODE;
        }
        else if (request.getParameter("show").equals("letter")){
            mode=LETTER_MODE;
            letter_id=new Long(request.getParameter("id"));
        }
        else if (request.getParameter("show").equals("category")){
            mode=CATEGORY_MODE;
            category_id = new Long(request.getParameter("cat"));
        }
        else if (request.getParameter("show").equals("find")){
            mode=FIND_MODE;
            search_word=request.getParameter("find");
        }
        else if (request.getParameter("show").equals("all")){
            mode=ALL_MODE;
        }
        
        this.action = request.getNonblankParameter("action");
        
        if (request.getParameter("wid")!=null && !request.getParameter("wid").equals("")){
            word_id = new Long(request.getParameter("wid"));
        }
    }

    public String getMode(){
        return mode;
    }

    public String getSearchWord(){
        return search_word;
    }

    public Long getLetterID(){
        return letter_id;
    }

    public Long getCategoryID(){
        return category_id;
    }

    public Long getWordID(){
        return word_id;
    }
	
    public void setWordID(Long newId){
        this.word_id = newId;
    }

    
    /**	 
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}
}
