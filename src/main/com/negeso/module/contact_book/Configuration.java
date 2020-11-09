package com.negeso.module.contact_book;

import com.negeso.framework.Env;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Feb 25, 2005
 */

public class Configuration {
    private static String CONTACT_TYPE = "contact_book";

    public static int getBirthdayReminderRange(){
        return new Integer(
                    Env.getProperty("contact-book.birthdayReminder.range")).intValue();
    }

    public static String getContactType(){
        return CONTACT_TYPE;
    }
    
	public static int getImageWidth(){
		return Env.getIntProperty("contact-book.image.width", 100);
	}

	public static int getImageHeight(){
		return Env.getIntProperty("contact-book.image.height", 0);
	}

}
