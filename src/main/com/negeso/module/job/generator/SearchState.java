package com.negeso.module.job.generator;

import com.negeso.framework.controller.RequestContext;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 31, 2005
 */

public class SearchState {
    private String searchWord = null;
    private Long searchSalary = null;

    public SearchState(RequestContext request){
        if (request.getParameter("search_word")!=null && !request.getParameter("search_word").trim().equals("")){
            searchWord = request.getParameter("search_word");
        }
        if (request.getParameter("search_salary")!=null && !request.getParameter("search_salary").equals("")){
            searchSalary = new Long(request.getParameter("search_salary"));
        }
    }

    public String getSearchWord(){
        return searchWord;
    }

    public Long getSearchSalary(){
        return searchSalary;
    }
}
