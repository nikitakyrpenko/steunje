/*
 * @(#)$Id: AdvancedSearchComponent.java,v 1.12, 2006-08-11 08:00:40Z, Vyacheslav Zapadnyuk$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.negeso.framework.domain.DBHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.page.PageComponent;

public class AdvancedSearchComponent implements PageComponent {
    private static Logger logger =
        Logger.getLogger(AdvancedSearchComponent.class);


    private static final String GET_ALL_HIDDEN_PRODUCTS_GROUP = "SELECT name" +
            " FROM ws_category as cat_ , ws_category2customer_visibility as w2_" +
            " WHERE cat_.parent_category_name = w2_.category_name\n" +
            " UNION" +
            " SELECT category_name" +
            " FROM ws_category2customer_visibility";

    private static final String GET_HIDDEN_PRODUCTS_GROUP = " SELECT name" +
            " FROM ws_category as cat_ WHERE cat_.parent_category_name IN (" +
            "  SELECT category_name" +
            "  FROM ws_category2customer_visibility AS v_" +
            "  EXCEPT (SELECT category_name" +
            "          FROM ws_category2customer_visibility AS w_" +
            "            LEFT JOIN ws_customer AS c_ ON w_.visible_to = c_.login" +
            "            LEFT JOIN user_list u ON c_.login = u.login" +
            "          WHERE u.login = ?))" +
            " UNION" +
            " SELECT category_name" +
            " FROM ws_category2customer_visibility AS v_" +
            " EXCEPT (SELECT category_name" +
            "        FROM ws_category2customer_visibility AS w_" +
            "          LEFT JOIN ws_customer AS c_ ON w_.visible_to = c_.login" +
            "          LEFT JOIN user_list u ON c_.login = u.login" +
            "        WHERE u.login = ?)";


    public Element getElement(
            Document document,
            RequestContext request,
            Map parameters )
    {
        logger.debug("+");
        SessionData session = request.getSession();
        SearchParameters searchParams =
            (SearchParameters) session.getAttribute("searchParameters");

        if(searchParams == null) {
            searchParams = new SearchParameters();
            session.setAttribute("searchParameters", searchParams);
            searchParams.setLanguageCode(session.getLanguageCode());
        }

        if(request.getParameter("page") != null) {
// to search with new parameters and directly get the required page  
        	if(
            		request.getParameter("query")!=null
            		|| request.getParameter("allWords")!=null
            		|| request.getParameter("atLeastOne")!=null
            		|| request.getParameter("exactPhrase")!=null
            		|| request.getParameter("lastMonths")!=null
            		) {
// setting new parameters from request
            	searchParams = createParamsForSearch(request);
            }
            searchParams.setCurPage((int) request.getLongValue("page"));
        } else {
// setting parameters from request
        	searchParams = createParamsForSearch(request);
            searchParams.setCurPage(1);
        }

        // building XML with search params
        Element searchEl = Env.createDomElement(document, "search");
        addSearchParams(searchEl, searchParams, request);

        if(
        		request.getParameter("page")!=null
        		||request.getParameter("query")!=null
        		|| request.getParameter("allWords")!=null
        		|| request.getParameter("atLeastOne")!=null
        		|| request.getParameter("exactPhrase")!=null
        		|| request.getParameter("lastMonths")!=null
        		) {

// building XML with search results

            List<String> hiddenGroup = new ArrayList<String>();
            try {
                Connection conn = DBHelper.getConnection();
                PreparedStatement stm = null;
                User u = request.getSession().getUser();

                if(u != null) {
                    stm = conn.prepareStatement(GET_HIDDEN_PRODUCTS_GROUP);
                    stm.setString(1, u.getLogin());
                    stm.setString(2, u.getLogin());
                }else {
                    stm = conn.prepareStatement(GET_ALL_HIDDEN_PRODUCTS_GROUP);
                }
                 ResultSet rs = stm.executeQuery();
                 while(rs.next()){
                     hiddenGroup.add(rs.getString("name"));
                 }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            searchParams.setCategoryProductExclude(hiddenGroup);
            searchEl.appendChild(SearchProducer.search(document, searchParams));
        }

        logger.debug("-");
        return searchEl;
    }

    private SearchParameters createParamsForSearch(RequestContext request)
    {
        logger.debug("+");
        SearchParameters searchParams = new SearchParameters();
        SessionData session = request.getSession();
        session.setAttribute("searchParameters", searchParams);
        searchParams.setLanguageCode(session.getLanguageCode());
        if("advanced".equals(request.getString("mode", null))) {
            searchParams.setAllWords(request.getParameter("allWords"));
            searchParams.setAtLeastOne(request.getParameter("atLeastOne"));
            searchParams.setExactPhrase(
                    request.getParameter("exactPhrase"));
            searchParams.setWithout(request.getParameter("without"));
            searchParams.setLastMonths(
                    (int) request.getLongValue("lastMonths"));
            searchParams.setPaging((int) request.getLongValue("paging"));
            searchParams.setSortOrder(request.getParameter("sortOrder"));
            User user = session.getUser();
            searchParams.setUid(user == null ? null : user.getId());
        }	else {
        	// free search
            searchParams.setAllWords(request.getParameter("query"));
        }

        logger.debug("-");
        return searchParams;
    }


    private void addSearchParams(
            Element searchEl,
            SearchParameters searchParams,
            RequestContext request)
    {
        logger.debug("+");
        if("advanced".equals(request.getString("mode", null))) {
        	searchEl.setAttribute("mode","advanced");
        	searchEl.setAttribute("allWords", searchParams.getAllWords());
        	searchEl.setAttribute("exactPhrase", searchParams.getExactPhrase());
        	searchEl.setAttribute("atLeastOne", searchParams.getAtLeastOne());
        	searchEl.setAttribute("without", searchParams.getWithout());
        	searchEl.setAttribute("paging", "" + searchParams.getPaging());
        	searchEl.setAttribute("lastMonths", "" + searchParams.getLastMonths());
        	searchEl.setAttribute("sortOrder", searchParams.getSortOrder());
        } else {
        	searchEl.setAttribute("mode","free");
        	searchEl.setAttribute("query", searchParams.getAllWords());
        }
        logger.debug("-");
    }
}