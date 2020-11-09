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

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FlipBookModule {
	
	public static final String eol = System.getProperty("line.separator");
	
	public static final String FILE_PATH = "filePath",
							   PDF = ".pdf",
							   PAGE_NUMBER_PARAM = "p",
							   GENERATED_PAGES_FOLDER = "/WEB-INF/generated/flip_book/",
							   MODULE_MEDIA_FOLER = "/media/flip_book/",
							   NAME = "flip_book",
							   
							   FLIP_BOOK_SCALING = "flip_book.scaling",
							   DEFAULT_SCALING = "1",
							   
							   FLIP_BOOK_BACK_GROUND_COLOR = "flip_book.bgcolor",
							   DEFAULT_BACK_GROUND_COLOR = "F8F8F8",
							   
							   FLIP_BOOK_LOADER_COLOR = "flip_book.loadercolor",
							   DEFAULT_LOADER_COLOR = "FC8137",
							   
							   FLIP_BOOK_PANEL_COLOR = "flip_book.panelcolor",
							   DEFAULT_PANEL_COLOR = "EDECEC",
							   
							   FLIP_BOOK_BUTTON_COLOR = "flip_book.buttoncolor",
							   DEFAULT_BUTTON_COLOR = "259F3E",
							   
							   FLIP_BOOK_TEXT_COLOR = "flip_book.textcolor",
							   DEFAULT_TEXT_COLOR = "FC8137";
							   
	
	public static float getScaling() {
		String scaling = Env.getProperty(FLIP_BOOK_SCALING, DEFAULT_SCALING);
		return Float.valueOf(scaling);
	}
	
	public static String getBackgroundColor() {
		return Env.getProperty(FLIP_BOOK_BACK_GROUND_COLOR, DEFAULT_BACK_GROUND_COLOR);
	}
	
	public static String getLoaderColor() {
		return Env.getProperty(FLIP_BOOK_LOADER_COLOR, DEFAULT_LOADER_COLOR);
	}
	
	public static String getPanelColor() {
		return Env.getProperty(FLIP_BOOK_PANEL_COLOR, DEFAULT_PANEL_COLOR);
	}
	
	public static String getButtonColor() {
		return Env.getProperty(FLIP_BOOK_BUTTON_COLOR, DEFAULT_BUTTON_COLOR);
	}
	
	public static String getTextColor() {
		return Env.getProperty(FLIP_BOOK_TEXT_COLOR, DEFAULT_TEXT_COLOR);
	}
}

