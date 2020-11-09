/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.flipbook;

import org.jpedal.exception.PdfException;

import com.negeso.framework.domain.CriticalException;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FilePagesReaderFactory {
	
	public static AbstractFilePagesReader getReader(String filename) {
		if (filename.endsWith(FlipBookModule.PDF)) {
			try {
				return new PdfFilePagesReader(filename);
			} catch (PdfException e) {
				throw new CriticalException(e);
			}
		}
		throw new CriticalException("Not supported file extension");
	} 
	
	public static AbstractFilePagesReader getReader(String filename, float scaling) {
		if (filename.endsWith(FlipBookModule.PDF)) {
			try {
				return new PdfFilePagesReader(filename, scaling);
			} catch (PdfException e) {
				throw new CriticalException(e);
			}
		}
		throw new CriticalException("Not supported file extension");
	} 
}

