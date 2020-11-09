/*
 * @(#)$Id: QueryBuilder.java,v 1.25, 2007-01-10 10:52:57Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.search;

import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.LucenePackage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.security.SecurityGuard;

/**
 *
 * Query buider. Takes Search Parameters and builds Query.
 * 
 * @version		$Revision: 26$
 * @author		Olexiy Strashko
 * 
 */
public class QueryBuilder {
    
	private static Logger logger = Logger.getLogger(QueryBuilder.class);
    
	private static String UNSUPPORTED_TERMS_REGEX = "(\\s\\*+)|(^\\*+)";
	private static String QUERY_PARTS_AND_CONNECTOR = " AND ";   
	private static String QUERY_PARTS_OR_CONNECTOR = " OR ";   
	private static String QUERY_PARTS_NOT_CONNECTOR = " NOT ";   
	private static String DEFAULT_FIELD_NAME = "contents";	
	
    SearchParameters searchParams = null;
    
    
    public static String getDefaultField(){
        return DEFAULT_FIELD_NAME;
    }
    
    public static Analyzer getAnalyzer(){
        return new SimplifiedAnalyzer();
    }

    public QueryBuilder(SearchParameters searchParams)
    {
        this.searchParams = searchParams; 
    }

    /**
     * Query getter. Return Query, proiduced by builder.
     * 
     * @return			The search Query, 
     * 					If any error - error is logged and empty 
     * 					PhraseQuery is returned
     */
    public Query getQuery(){
        logger.debug("+");
        BooleanQuery query = new BooleanQuery();
        BooleanQuery baseQuery = new BooleanQuery();
        
        //search words + date range filter
        String queryString  = this.buildQueryString(true);
        if ( queryString == null){
            return new BooleanQuery();
        }
        if ( logger.isInfoEnabled() ){
        	logger.info("Query: " + queryString.toString());
        }

        if(searchParams.getCategoryProductExclude() != null){
            for(String str : searchParams.getCategoryProductExclude()) {
                query.add(new TermQuery(new Term("categoryName", str)), BooleanClause.Occur.MUST_NOT);
            }
        }

        addSearchWordsFilter(baseQuery, QueryBuilder.getDefaultField(), queryString);
       	if ("true".equals(Env.getProperty("search.includeTitlePages"))) 
       		addSearchWordsFilter(baseQuery, "title", queryString);
       	query.add(baseQuery, BooleanClause.Occur.MUST);
        
        //language code filter
        if ( searchParams.getLanguageCode() != null) {
        	query.add(new TermQuery(new Term("language", searchParams
					.getLanguageCode())), BooleanClause.Occur.MUST);
            
            this.addContainerFilter(query);
        }

        if ( logger.isInfoEnabled() ){
            logger.info("ResultQuery: " + query.toString(QueryBuilder.getDefaultField()));
        }
            
        logger.debug("-");
        return query;
    }
    
    
    /**
	 * @param query
	 */
	private void addContainerFilter(BooleanQuery query) {
        logger.debug("+");

        
        BooleanQuery containerQuery = new BooleanQuery();
        containerQuery.add(new TermQuery(new Term("cid", "0")),
				BooleanClause.Occur.SHOULD);
		containerQuery.add(new TermQuery(new Term("cid", "null")),
				BooleanClause.Occur.SHOULD);
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id FROM containers");

            int count = 0;
            while(rs.next()) {
                if(count++ > 800) {
                    logger.error("Cannot process over 800 containers");
                    break;
                }

                Long cid = new Long(rs.getLong("id"));
                if( SecurityGuard.canView( searchParams.getUid(), cid ) ) {
                	containerQuery.add(new TermQuery(new Term("cid", cid
							.toString())), BooleanClause.Occur.SHOULD);
                }
            }
        } 
        catch (SQLException e) {
            logger.error("- Exception while building list of allowed cids", e);
        } 
        finally {
            DBHelper.close(rs, stmt, conn);
        }
        
        query.add(containerQuery, BooleanClause.Occur.MUST);
        logger.debug("-");
	}

	/**
     * Build query String by SearchParameters.
     * 
     * @return		The QueryString, 
     * 				null - if SearchParameters null or empty
     */
    private String buildQueryString(boolean addDateFilter)
    {
        logger.debug("+");

        if ( this.searchParams == null){
            logger.error("- ERROR: SearchParameters is null");
            return null;
        }
        
        StringBuffer queryBuffer = new StringBuffer("");
        
        // all words term
        QueryBuilder.appendAllWordsTerm(
                queryBuffer, 
                searchParams.getAllWords() + "*", 
                QUERY_PARTS_AND_CONNECTOR
        );
        
        // exact phrase
        QueryBuilder.appendExactPhrase(
                queryBuffer, 
                searchParams.getExactPhrase(), 
                QUERY_PARTS_AND_CONNECTOR
        );

        // at least one words
        QueryBuilder.appendTerm(
                queryBuffer, 
                QueryBuilder.removeUnsupportedTerms(searchParams.getAtLeastOne()), 
                QUERY_PARTS_AND_CONNECTOR
        );

        // without terms 
        QueryBuilder.appendTerm(
                queryBuffer, 
                QueryBuilder.removeUnsupportedTerms(searchParams.getWithout()), 
                QUERY_PARTS_NOT_CONNECTOR
        );
        
        // without terms (escaped)
        QueryBuilder.appendTerm(
                queryBuffer, 
                getEscapedTerm(QueryBuilder.removeUnsupportedTerms(searchParams.getWithout())), 
                QUERY_PARTS_NOT_CONNECTOR
        );
        
        if ( addDateFilter ) {        	
        	addDateFilter(queryBuffer);
        }
        
        String queryString = queryBuffer.toString();
        if ( queryString.trim().length() == 0 ){
            logger.info("Empty search parameters");
            logger.debug("-");
            return null;
        }
        logger.debug("-");
        return queryString;
    }
        
    void addDateFilter(StringBuffer queryBuffer)
    {
        logger.debug("+");
        int lastMonths = searchParams.getLastMonths();
        if (lastMonths != 0 || searchParams.getLowerTime() != 0  || searchParams.getUpperTime() != 0 ){
        	String start, end;
        	if (lastMonths != 0 ) {        		
        		Calendar date = Calendar.getInstance();
        		end = Utils.getDate(date.getTimeInMillis());
        		date.add(Calendar.MONTH, -lastMonths);
        		start = Utils.getDate(date.getTimeInMillis());
        	} else {
        		start = String.valueOf( searchParams.getLowerTime());
        		end = searchParams.getUpperTime() > 0 
        			? String.valueOf( searchParams.getUpperTime())
        			: String.valueOf( new Date().getTime() );
        	}
        	
            QueryBuilder.appendTerm(
                    queryBuffer,
                    "date:["+ start + " TO " + end + "]",
                    QUERY_PARTS_AND_CONNECTOR
            );
            if ( logger.isInfoEnabled() ){
            	logger.info("date:["+ start + " TO " + end + "]");
            }
        }
        logger.debug("-");
        

    }
    

    /**
	 * @param query
	 */
	private void addSearchWordsFilter(BooleanQuery query, String field, String queryString) {		
		logger.debug("+");
        try {
            QueryParser queryParser = new QueryParser(field, QueryBuilder
					.getAnalyzer());
        	Query userInput = queryParser.parse(queryString);

			query.add(userInput, BooleanClause.Occur.SHOULD);

        } catch (ParseException e){
    		logger.error("- Query: " + queryString + " Error:" + e.getMessage());
    	}
		logger.debug("-");
	}
    
    /**
     * 
     * @param queryString
     * @param term
     * @param connector
     */
    private static void appendExactPhrase(StringBuffer queryString, String term, String connector){
        if ( term == null ) return;
        if ( term.trim().length() == 0 ) return;
        QueryBuilder.appendTerm(
                queryString, 
                "\"" + term + "\"", 
                QUERY_PARTS_AND_CONNECTOR
        );
        QueryBuilder.appendTerm(
                queryString, 
                "\"" + getEscapedTerm(term) + "\"",
                QUERY_PARTS_OR_CONNECTOR
        );
    }

    /**
     * Append StringBuffer  
     * 
     * @param queryString
     * @param term
     * @param connector
     */
    private static void appendAllWordsTerm(StringBuffer queryString, String term, String connector){
        if ( term == null ) return;
        if ( term.trim().length() == 0 ) return;
        term = QueryBuilder.removeUnsupportedTerms(term);
        term = QueryBuilder.rewriteQueryConnector(term, QueryBuilder.QUERY_PARTS_AND_CONNECTOR);
        QueryBuilder.appendTerm(
                queryString, 
                term, 
                QUERY_PARTS_AND_CONNECTOR
        );
    }
    
    private static String getEscapedTerm(String term){
        return new String(StringEscapeUtils.escapeHtml(term));
    }
    
    /**
     * Parse query string and place connector between terms.
     * 
     * @param query			
     * @param connector
     * @return
     */
    private static String rewriteQueryConnector(String query, String connector){
        if (query == null) return null;
        StringTokenizer parser = new StringTokenizer(query);
        StringBuffer buffer = new StringBuffer("");
        boolean isFirst = true;
        while ( parser.hasMoreTokens() ) {
            if ( !isFirst ){
                buffer.append(connector);
            }
            else{
                isFirst = false;
            }
            buffer.append(parser.nextToken());
        }
        return buffer.toString();
    }
    
    
    /**
     * Process query string and removes all unsupported substrings. 
     * Unsupported substrings:
     * 	- *..* at the beginning of word	 

     * @param queryString
     * @return
     */
    private static String removeUnsupportedTerms(String string){
        if (string == null) return null;
        return string.toString().replaceAll(UNSUPPORTED_TERMS_REGEX, " ");
    }
    
    
    /**
     * Appends term(String) to query (StringBuffer) using operator/connector(String).
     * If length of query is 0 - no connector used.
     * If term is null - nothing happens
     * 
     * @param queryString
     * @param term
     * @param connector
     */
    private static void appendTerm(StringBuffer queryString, String term, String connector){
        if ( term == null ) return;
        if ( term.trim().length() == 0 ) return;
        if ( queryString.length() > 0 ){
            queryString.append(connector);
        }
        
        if (( queryString.length() == 0 ) && (connector.equals(QUERY_PARTS_NOT_CONNECTOR))){
        	queryString.append(" ANY " + connector);
        }
        
        queryString.append("( "); 
        queryString.append(term); 
        queryString.append(" )");
        queryString.append("( "); 
        queryString.append(getEscapedTerm(term)); 
        queryString.append(" )"); 
    }
    
    public static void main( String[] args ) {
        SearchParameters sp = new SearchParameters();
        sp.setWithout("*Go**ogle*  yamaxa");
        sp.setAtLeastOne("***");
        
        QueryBuilder qb = new QueryBuilder(sp);
        Query q = qb.getQuery();
        
        /**
        try{
            q = QueryParser.parse("AAA** He**lloooo", "contents", new GermanAnalyzer());
        }
        catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
        */
        System.out.println("Result: " + q.toString("contents"));
    }

}
