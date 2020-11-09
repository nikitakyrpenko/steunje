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
package com.negeso.module.core.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class QRCodeController extends AbstractController{
	
	private static Logger logger = Logger.getLogger(QRCodeController.class);
	
	private static final String  DEFAULT_CHARSET = "ISO-8859-1";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Charset charset = Charset.forName(DEFAULT_CHARSET);
	    CharsetEncoder encoder = charset.newEncoder();
	    byte[] b = null;
	    try {
	        String text = request.getParameter("txt");
	        if (StringUtils.isBlank(text)) {
	        	logger.error("Obligatory param 'txt' is empty, so none code generated");
	        	return null;
	        }
	        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(text));
	        b = bbuf.array();
	    } catch (CharacterCodingException e) {
	        logger.error(e);
	    }

	    String data = null;
	    try {
	        data = new String(b, DEFAULT_CHARSET);
	    } catch (UnsupportedEncodingException e) {
	        logger.error(e);
	    }

	    // get a byte matrix for the data
	    BitMatrix matrix = null;
	    int h = Env.getIntProperty("QRCodeImageHeight", 120);
	    int w = Env.getIntProperty("QRCodeImageWidth", 120);
	    com.google.zxing.Writer writer = new QRCodeWriter();
	    try {
	        matrix = writer.encode(data,
	        com.google.zxing.BarcodeFormat.QR_CODE, w, h);
	    } catch (com.google.zxing.WriterException e) {
	        logger.error(e);
	    }
	 
	    try {
	    	response.setHeader("Pragma","no-cache");
	    	response.setDateHeader ("Expires", -1);
	    	response.setHeader("Content-Type", "image/png");
	    	response.setHeader("Content-Disposition", "inline; filename=\"chart.png\"");
	        MatrixToImageWriter.writeToStream(matrix, "PNG", response.getOutputStream());
	        IOUtils.closeQuietly(response.getOutputStream());
	    } catch (IOException e) {
	        logger.error(e);
	    }
		return null;
	}

}

