/*
 * @(#)$Id: Navigator.java,v 1.1, 2006-02-22 11:27:49Z, Svetlana Bondar$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.navigation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * Data paging navigator
 * 
 * @author Svetlana Bondar
 *
 * @version $Revision: 2$
 */

public class Navigator {
    private static Logger logger = Logger.getLogger(Navigator.class);
    private static final int CHUNK_SIZE = 25; 	// data items per page      
    private static final int PAGE_SIZE = 5;    	// count of shown chunk numbers 
 
    private DataService dataService;
    private int currentChunk;
    private Hashtable mode;
    
    int dataCount;
    

    public Navigator(DataService dataService, Hashtable mode, String currentChunk, String goChunk) {
        this.dataService = dataService;
        this.mode = mode;                
        dataCount = dataService.getDataCount(mode);
        logger.debug("dataCount " + dataCount);
        
        if (goChunk!=null && !goChunk.equals(""))    {
            if (goChunk.equals("next"))   {
            	this.currentChunk = Integer.parseInt(currentChunk) + 1;                                    
            }   else    {
                if (goChunk.equals("previous"))   {
                	this.currentChunk = Integer.parseInt(currentChunk) - 1;        
                }   else    {
                	this.currentChunk = Integer.parseInt(goChunk);
                }    
            }
        } else {
            this.currentChunk = 0;
        }          	
    }

    //  return current chunk number
    public int getCurrentChunk() {
        return currentChunk;
    }

    //  return list of shown chunk numbers. List size  <= PAGE_SIZE
    public List getChunks() {
    	int pageSize;
        int chunkCount = dataCount/CHUNK_SIZE;
        if (dataCount%CHUNK_SIZE > 0) chunkCount++;
        
        int pageCount =  chunkCount/PAGE_SIZE;
        if (chunkCount%PAGE_SIZE > 0) pageCount++;
    
        int tmp = currentChunk/PAGE_SIZE;
        if(tmp + 1 == pageCount)	{
            pageSize = chunkCount%PAGE_SIZE;    		
            if (pageSize == 0)
            	pageSize = PAGE_SIZE; 
        }	else
        	pageSize = PAGE_SIZE;

        if (dataCount == 0) pageSize = 0;
        
        logger.debug("currentChunk " + currentChunk);
        logger.debug("pageSize " + pageSize);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < pageSize; i++)  {            
            list.add(Integer.toString(tmp*PAGE_SIZE + i + 1));            
        }
        return list;
    }
    
    public boolean isPreviousChunkExists() {
        return currentChunk > 0 ? true : false; 
    }

    public boolean isNextChunkExists() {
        if (dataCount == 0) return false;
        
        int tmp = dataCount/CHUNK_SIZE;
        if (dataCount%CHUNK_SIZE > 0) tmp++;
        return tmp == currentChunk + 1 ? false : true ; 
    }

    //  return list of data in current range, i.e. current data chunk 
    public List getChunk() {
		int chunkSize = isNextChunkExists() ? CHUNK_SIZE : 
			dataCount%CHUNK_SIZE > 0 ? dataCount%CHUNK_SIZE : CHUNK_SIZE;
        List data = dataService.getDataRange(
        	mode, currentChunk * CHUNK_SIZE, currentChunk * CHUNK_SIZE + chunkSize 
        );
        if (data == null)   data = new ArrayList(); 
        return data;          	
    }
}