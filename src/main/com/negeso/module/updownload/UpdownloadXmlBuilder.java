/*
 * @(#)UpdownloadXmlBuilder.java       @version	22.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload;


import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;

import com.negeso.framework.generators.StandardXmlBulder;
import com.negeso.framework.generators.XmlHelper;

/**
 * Updownload xml builder singleton object.
 *
 * @version 	22.04.2004
 * @author 		OStrashko
 */
public class UpdownloadXmlBuilder extends StandardXmlBulder{

	public QNameCache qNameCache = new QNameCache();
	public Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();

	private static UpdownloadXmlBuilder instance = new UpdownloadXmlBuilder();
	
	public UpdownloadXmlBuilder() {
		super();
	}
	
	public static UpdownloadXmlBuilder get(){
		return instance;
	}


	public Element createElement(String name){
		return DocumentHelper.createElement(
			qNameCache.get(name, ngNamespace)
		);
	}
	


	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public Element getThumbnailUploadResult(
		String realImagePath, 
		String thumbnailImagePath) 
	{
		Element thumbnailResult = DocumentHelper.createElement(
			qNameCache.get("thumbnail-upload-result", ngNamespace)
		)
			.addAttribute("real-file", realImagePath)
			.addAttribute("thumbnail-file",	thumbnailImagePath)
		;
		return thumbnailResult;
	}

	/**
	 * @param string
	 * @return
	 */
	public Element getImageUploadResult(String imagePath) {
		Element imageResult = DocumentHelper.createElement(
			qNameCache.get("image-upload-result", ngNamespace)
		)
			.addAttribute("real-file", imagePath)
		;
		return imageResult;
	}

	/**
	 * @param string
	 * @return
	 */
	public Element getFileUploadResult(String filePath) {
		Element fileResult = DocumentHelper.createElement(
			qNameCache.get("file-upload-result", ngNamespace)
		)
			.addAttribute("real-file", filePath)
		;
		return fileResult;
	}

	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public Element getFileUploadOption(String workingFolder, String mode) {
		Element fileOption = DocumentHelper.createElement(
			qNameCache.get("upload-option", ngNamespace)
		)
			.addAttribute("working-folder",	workingFolder)
			.addAttribute("mode", mode)
		;
		return fileOption;
	}

	/**
	 * @param workingFolder
	 * @param mode
	 * @return
	 */
	public Element getDocUploadOption(String workingFolder, String mode) {
		return this.getFileUploadOption(workingFolder, mode);
	}
}
