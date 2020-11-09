/*
* @(#)$$Id: HistoryStack.java,v 1.4, 2007-01-18 16:53:09Z, Svetlana Bondar$$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.framework.navigation;

import java.io.Serializable;
import java.util.Stack;

import org.apache.log4j.Logger;
/**
 * The <code>HistoryStack</code> class represents a last-in-first-out 
 * (LIFO) stack of <code>{@link com.negeso.framework.navigation.Link Link}</code> objects. The usual
 * <tt>push</tt> operation, getter/setter for default link are provided, 
 * a method to get a stack of links, and a method <tt>goBack</tt> 
 * for switch to previous link in history.
 * A method <tt>go</tt> allow to clean stack above the specifed number link. 
 * <p>
 * Default link is returned if error or stack is empty.
 * When a stack is first created, it contains no items. 
 * 
 * <br/>
 * 
 *
 * @author  sbondar
 * @version $Revision: 5$ 
 */

public class HistoryStack implements Serializable {
	private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(HistoryStack.class);  

    public static final String HISTORY_STACK_KEY   = "historyStack";   
    
	private Stack<Link> hSt = new Stack<Link>();

	private Link defaultLink;
    
    /**
     * Creates an empty HistoryStack with specified default link.
     * 
     * @param   defaultLink   the default link to be returned if error.  
     */
	public HistoryStack(Link defaultLink) {
               this.defaultLink = defaultLink;		
	}

    /**
     * Creates an empty HistoryStack with default 
     * link new Link("login.link", "/sis_reseller/login.html").
     */
	public HistoryStack() {
        this.defaultLink = new Link("login.link", "/sis_reseller/login.html", 0);
	}
	
    /**
     * Pushes a link on defined level if pushed link have level, else </br>   
     * pushes a link into the top of this stack, if link is not found in stack, </br>
     * top link is replaced with pushed link, if pushed link title equals to the top link title, </br> and
     * removes all links above pushed link if link exists in stack.       
     * 
     * @param   link   the link to be pushed into this stack.
     */
	public void push(Link link) {
		logger.debug("+");
		logger.debug("? can push Link = " + link + " ?");
		
		checkAndFix_topItem(link); // HOT FIX
		
		if ( link.getLevel() == 0 && hSt.size()==0 ){
			hSt.push(link);
		}else if( link.getLevel() == 0 && hSt.size() > 0 ){
			pushLinkToDefinedLevel(link);
		} else {
			pushLinkToNextLevel(link);
		}
		logger.debug("-");		
	}

	private void checkAndFix_topItem(Link link) {	
		if (link.getLevel() == -1) {
			// apererva: hot fix to clean up the history stack when 
			// an item should be forcedly placed on the first position. When
			// the history stack has items, but the page was closed, the entities
			// are stored in history. An identifier "-1" serves to define that
			// the specified link should be placed on the first place.
			// Strong refactoring needs of history stack functionality!
			logger.debug("SIZE=" + hSt.size());
			if (hSt.size() > 0) {
				hSt.clear();
			}
			link.setLevel(0);
		}
	}
	
	private void pushLinkToNextLevel(Link link) {
		logger.debug("+");	
		if (link.getLevel() == 1) {				
			link.setLevel(hSt.size());
		}			
		//search for link title in history stack  
		int i = 0;		
		for (; i < hSt.size() &&  
		!hSt.get(i).getTitle().equals(link.getTitle()); i++)
			;
		
		if (i + 1 >= hSt.size())	{
			pushNewLinkOrChangeUrl(link);	
		} else { //link already in stack
			go(i);			
		}
		logger.debug("-");	
	}

	private void pushLinkToDefinedLevel(Link link) {
		logger.debug("+");
		while (!hSt.empty() && link.getLevel() <= hSt.peek().getLevel() && link.getLevel() != 0) {
			hSt.pop();
		}
		hSt.push(link);
		logger.debug("-");	
	}

	private void pushNewLinkOrChangeUrl(Link link) {
		logger.debug("+");	
		//new link
		if (hSt.empty() || 
				!hSt.peek().getTitle().equals(link.getTitle())){
			hSt.push(link);
			logger.debug("push Link:title=" + link.getTitle() + " url=" + link.getUrl() + 
					" ordering " + hSt.size());			
		}
		//pushed link have other url
		else 	{
			if (hSt.peek().getTitle().equals(link.getTitle()) && 
					!hSt.peek().getUrl().equals(link.getUrl())) {
				hSt.pop();				
				hSt.push(link);	
				logger.debug("pop and push Link:title=" + link.getTitle() + " url=" + link.getUrl() + 
						" ordering " + hSt.size());			
				
			}	
		}
		logger.debug("-");	
	}
	
    /**
     * Returns stack of <code>Link</code> objects. 
     *
     * @return     stack of <code>Link</code> objects.
     */		
	public Stack<Link> getLinks() {
		return hSt;
	}

	/**
	 * Removes top link and returns new top link. 
	 * 
	 * @return 	new top link  
	 */
	public Link goBack() {
		hSt.pop();
		logger.debug("goBack to "  + hSt.peek());
		return hSt.peek();		
	}

	
	/**
	 * Remove all links above <code>goIndex</code> number and
	 * returns new top link url or default link url if if stack empty (or is null).
	 * 
     * @param   goIndex   the new top link number.    
	 * @return new top link url or default link url if stack empty  
	 */
	
	public String go(int goIndex) {
		logger.debug("+");		
		logger.debug("go index" + goIndex);		

		if (hSt == null) {
			logger.error("historyStack is null!");
			return this.defaultLink.getUrl();
		}

		int i = goIndex + 1;
		try{		
			
			while ( !hSt.empty() && i < hSt.size() )	{
				hSt.pop();
			}
			logger.debug("-");			
			return hSt.peek().getUrl();		
			
		} catch (Exception e) {
			logger.error("-error", e);
			return this.defaultLink.getUrl();
		}		
	}

	/**
	 * Gets the default link 
	 * 
	 * @return default link
	 */
	public Link getDefaultLink() {
		return this.defaultLink;
	}

	/**
	 * Sets the default link 
	 * 
     * @param   defaultLink   the default link new value.   
	 */
	public void setDefaultLink(Link defaultLink) {
		this.defaultLink = defaultLink;
	}

    public int getSize() {
        return this.hSt.size();
    }

}
