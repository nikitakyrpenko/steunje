/*
 * @(#)TestFileUtils.java       @version	17.03.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.Timer;


/**
 * Test File ustils.
 *
 * @version 	17.03.2004
 * @author 	Olexiy.Strashko
 */
public class TestFileUtils {
	private static PrintStream out = System.out;

	
	public TestFileUtils(){
		super();
	}

	public static void main(String[] args) {
			
		Timer timer = new Timer();
//		String str = new String("helloooo.jpg");
		
		File file = new File("C:\\test");
		String[] fileNames = file.list();
		System.out.println("Lenght:" + fileNames.length);
//		HashMap map = new HashMap();
		for (int i = 1; i < fileNames.length; i++){
			FileUtils.getExtension(fileNames[i]);
		}
		System.out.println("Time:" + timer.stop());
//		try{
//			
//			
//			System.out.println("Test begins");
//			String directory = "/C:/pro/tomcat41/webapps/opticen/media";
//			File file = new File(directory);
//			
//			String secondFile = "/C:/pro/tomcat41/webapps";
//			File sFile = new File(secondFile);
//
//			System.out.println(sFile.toURL());
//			System.out.println(file.toURL());
//			
//			System.out.println(file.toURL().toString().replaceFirst(sFile.toURL().toString(), ""));
//
//			/*
//			List list = new ArrayList();
//			String[] ext = {"jpg", "gif"}; 
//			List ret = FileUtils.listFiles(file, list, ext);
//			
//			for ( Iterator i = ret.iterator(); i.hasNext();){
//				System.out.println( ((File) i.next()) . getName());
//			}
//			*/
//			
//			List list = null;
//			
//			System.out.println(file.exists());
//			
//			Resource res = Repository.get().getResource(new File(directory));
//			/*res.getContents(
//					
//				Repository.get().getSortComparator("sname"), 
//				Configuration.imageTypeExtensions
//			);
//			res.toDom4jDirectoryElement("sname", "image_gallery");
//			res.toDom4jDirectoryElement("sname", "image_gallery");
//			res.toDom4jDirectoryElement("sname", "imagess_gallery");
//			*/
//
//			Timer timer = new Timer();
//			timer.start(); 
//			
//			
//			//res.toDom4jDirectoryElement("sname", "image_gallery");
//			System.out.println("To directory dom4j image gallery: " + timer.stop());
//
//			timer.start(); 
//			//res.toDom4jDirectoryElement("sname", "imagess_gallery");
//			System.out.println("To directory dom4j standard: " + timer.stop());
//
//			/*
//			for (Iterator i = list.iterator(); i.hasNext();){
//				Resource r = (Resource) i.next();
//				//System.out.println(r.getName());
//			}
//			*/
//			
//			//System.out.println(FileUtils.toUrlPath(file.getPath()));
//			
//			
//			//List list = new ArrayList();
//			//FileUtils.listFolders(file, list);
//			//for (Iterator i = list.iterator(); i.hasNext();){
//			//	File f = (File) i.next();
//			//	System.out.println(f.getAbsolutePath());
//				
//				//System.out.println(f.getAbsolutePath().replaceFirst("W:", "") +
//				//	" Name: " + f.getName() +
//				//	" abs: " + f.getAbsolutePath() +
//				//	" can: " + f.getCanonicalPath() 
//				//	);
//			//}
//
//			//System.out.println("Directory " + directory + " size: " + FileUtils.countDirectorySize(file)/(1024*1024) + "M");
//			/*
//			Resource res = Resource.createFromPath(directory);
//			printXMLElement(res.toDom4jDirectoryElement());
//			
//			*/
//			/*
//			List list = res.getContents();
//			for (Iterator iter = list.iterator(); iter.hasNext();){
//				printXMLElement((Element)iter.next());
//			}
//			*/
//			
//			System.out.println("finish");
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	public static void  printXMLElement(Element el){
		try{
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(el);
		}
		catch(UnsupportedEncodingException ex){
			ex.printStackTrace(out);
		}
		catch(IOException ex){
			ex.printStackTrace(out);
		}
	}
	
}