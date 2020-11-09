/*
 * @(#)$Id: PositionDescriber.java,v 1.0, 2007-04-11 06:26:45Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command.creating;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class PositionDescriber {

    	private Long id;
    	private Long langId;    	
    	private String fileName;
		public PositionDescriber(Long id, Long langId, String fileName) {
			super();
			this.id = id;
			this.langId = langId;
			this.fileName = fileName;
		}
		public String toString() {
			return ReflectionToStringBuilder.reflectionToString(this);
		}
		public Long getId() {
			return id;
		}
		public String getFileName() {
			return fileName;
		}
		public Long getLangId() {
			return langId;
		}
}

