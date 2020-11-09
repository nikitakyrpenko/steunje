package com.negeso.module.glossary.component;

import com.negeso.module.glossary.domain.Alphabet;
import com.negeso.module.glossary.generators.GlossaryXmlGenerator;
import com.negeso.module.glossary.NavigationForm;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.Env;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.PageComponent;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.Map;
import java.sql.Connection;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 2, 2005
 */

public class GlossaryComponent implements PageComponent {
    private static Logger logger = Logger.getLogger( GlossaryComponent.class );

    public Element getElement(Document document, RequestContext request, Map parameters) {
        logger.debug("+");
        Element elt = Env.createDomElement(document, "glossary_module");
        Connection conn = null;
        try{
            conn = DBHelper.getConnection();
            NavigationForm state = new NavigationForm(request);
            GlossaryXmlGenerator.createHeaderXml(conn, elt, new NavigationForm(request));
            if (!state.getMode().equals(NavigationForm.FIRST_TIME_MODE)){
                if(state.getWordID()!=null){
                    GlossaryXmlGenerator.createWordXml(conn, elt, state);
                }
                else{
                    GlossaryXmlGenerator.createSearchXml(conn, elt, state);
                }
            }
        }
        catch(Exception e){
            if (elt!=null){
                elt.setAttribute("feedback", "error");
            }
            logger.error(e.getMessage(), e);
        }
        finally{
            DBHelper.close(conn);
        }
        logger.debug("-");
        return elt;
    }
}
