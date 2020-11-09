/*
 * @(#)Id: Cloner.java, 22.10.2007 15:21:36, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.IOUtils;


/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class Cloner {

    /**
     * 
     * @param o an object which should be cloned.
     * @return a clone of the object.
     * @throws IOException
     */	
	public static Object clone(Object o) throws CloneNotSupportedException {
		if (o == null)
			throw new IllegalArgumentException("object is null");
		
		Object retObject = null;
		
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bout);
			out.writeObject(o);
			out.flush();
			
			ByteArrayInputStream bin = new ByteArrayInputStream( bout.toByteArray() );
			in = new ObjectInputStream(bin);
			retObject = in.readObject();
		} catch (IOException e) {
			throw new CloneNotSupportedException("unexpected IO error by serialization: "
					+ e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new CloneNotSupportedException("unexpected class not found error by serialization: "
					+ e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
		
		return retObject;
	}
}
