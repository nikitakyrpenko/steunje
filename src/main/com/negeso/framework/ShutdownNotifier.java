/* @(#)$Id: ShutdownNotifier.java,v 1.4, 2006-02-16 14:12:19Z, Stanislav Demchenko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
 
package com.negeso.framework;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.negeso.framework.event.AppDestroyListener;


/**
 * To clean caches at web-app shutdown, you can either define "destroy-method"
 * of a bean, or programmatically
 * {@link #registerAppDestroyListener(AppDestroyListener) register} your object
 * using this class.
 * 
 * @author  Stanislav Demchenko
 * @version $Revision: 5$
 */
public class ShutdownNotifier {
    
    private static List<AppDestroyListener> listeners =
            new ArrayList<AppDestroyListener>();
    
    private static Logger logger = Logger.getLogger(ShutdownNotifier.class);
    
    /** Objects can subscribe to web-app's "destroy" event using this method */
    public static void registerAppDestroyListener(AppDestroyListener listener) {
        logger.debug("+");
        if (listener != null) {
            logger.info("- registered: " + listener.getClass().getName());
            listeners.add(listener);
        } else {
            logger.error("- error: AppDestroyListener is null");
        }
    }
    
    public static void removeAppDestroyListener(AppDestroyListener listener) {
        logger.debug("+");
        if (listener != null) {
            logger.info("- removed: " + listener.getClass().getName());
            listeners.remove(listener);
        } else {
            logger.error("- error: AppDestroyListener is null");
        }
    }
    
    /**
     * Notifies subscribers of web-app destruction event that they can
     * make cleanup. The method is triggered when the web-app (or the
     * web-server) stops.
     */
    public void shutdown() {
        logger.debug("+ -");
        EventObject event = new EventObject("destroy");
        for (AppDestroyListener listener : listeners) {
            try {
                listener.destroy(event);
            } catch (Exception e) {
                logger.error("Exception while destroying " + listener, e);
            }
        }
        LogManager.shutdown();
    }
    
}
