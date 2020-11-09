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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import com.negeso.framework.domain.CriticalException;
import com.negeso.module.flipbook.pdf.CustomizedPdfDecoder;
import com.negeso.module.media_catalog.Repository;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class PdfFilePagesReader extends AbstractFilePagesReader{
	
	private static Logger logger = Logger.getLogger(PdfFilePagesReader.class);
	
	private PdfDecoder pdfDecoder;
	private int width;
	private int height;
	
	public PdfFilePagesReader(String filename, float scaling) throws PdfException {
		super();
		pdfDecoder = new CustomizedPdfDecoder(true, scaling);
		pdfDecoder.openPdfFile(Repository.get().getRealPath(filename));
	}
	
	public PdfFilePagesReader(String filename) throws PdfException {
		this(filename, FlipBookModule.getScaling());
	}
	


	@Override
	public int getPagesCount() {
		return pdfDecoder.getPageCount();
	}


	@Override
	public void finish() {
		pdfDecoder.closePdfFile();
	}


	@Override
	public File getPage(File file, int i) {
		try {			
			BufferedImage img = pdfDecoder.getPageAsImage(i);
			ImageIO.write(img, "png", file);			
		} catch (PdfException e) {
			e.printStackTrace();
			logger.error(e);
			throw new CriticalException("Unable to create file: " + file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new CriticalException("Unable to create file: " + file.getAbsolutePath(), e);
		}
		return file;
	}
	
	@Override
	public int getWidth() {
		if (width > 0) {
			return width;
		}
		try {			
			BufferedImage img = pdfDecoder.getPageAsImage(1);
			width = img.getWidth();
			height = img.getHeight();
			return width;
		} catch (PdfException e) {
			logger.error(e);
		}
		width = super.getWidth();
		return width;
	}
	
	@Override
	public int getHeight() {
		if (height > 0) {
			return height;
		}
		try {			
			BufferedImage img = pdfDecoder.getPageAsImage(1);
			width = img.getWidth();
			height = img.getHeight();
			return height;
		} catch (PdfException e) {
			logger.error(e);
		}
		height = super.getHeight();
		return height;
	}

}

