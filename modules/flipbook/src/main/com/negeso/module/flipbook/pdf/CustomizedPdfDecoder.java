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
package com.negeso.module.flipbook.pdf;

import org.jpedal.PdfDecoder;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class CustomizedPdfDecoder extends PdfDecoder {

	private static final long serialVersionUID = -1611228147293452644L;

	public CustomizedPdfDecoder(boolean newRender, float scaling) {
		super(newRender);
		this.scaling = scaling;
	}
}

