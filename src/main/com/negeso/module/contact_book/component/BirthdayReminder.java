package com.negeso.module.contact_book.component;

import com.negeso.module.contact_book.generators.ContactXmlBuilder;
import com.negeso.module.contact_book.Configuration;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.domain.DBHelper;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.*;
import java.sql.Connection;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Feb 25, 2005
 */

public class BirthdayReminder extends AbstractPageComponent {
    private static Logger logger = Logger.getLogger( BirthdayReminder.class );

    public Element getElement(Document document, RequestContext request, Map parameters) {
        logger.debug("+");
        Element elt = Xbuilder.createEl(document, "contact-book", null);
        Element reminderEl = Xbuilder.addEl(elt, "birthday-reminder", null);
        Connection conn = null;
        try{
            conn = DBHelper.getConnection();
            int range = Configuration.getBirthdayReminderRange();
            Calendar start_date = Calendar.getInstance();
            Calendar end_date = Calendar.getInstance();
            end_date.add(Calendar.DAY_OF_YEAR, range);
            ContactXmlBuilder.searchContactsByDate(conn, reminderEl,
                    start_date.get(Calendar.DAY_OF_YEAR),
                    end_date.get(Calendar.DAY_OF_YEAR));
        }
        catch(Exception e){
            if (elt!=null){
                elt.setAttribute("step", "error");
            }
            logger.error(e.getMessage(), e);
        }
        finally{
            if (conn != null)
                DBHelper.close(conn);
        }
        logger.debug("-");
        return elt;
    }

}
