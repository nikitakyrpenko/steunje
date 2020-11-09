/*
 * @(#)$Id: DynamicsXmlBuilder.java,v 1.0, 2005-01-21 16:17:31Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dynamics.generators;

import java.sql.Connection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Language;
import com.negeso.module.dynamics.DynamicsForm;


public interface DynamicsXmlBuilder {
    
    Element getElement(
            Connection conn,
            Document doc,
            DynamicsForm form,
            Language lang);
    
}

