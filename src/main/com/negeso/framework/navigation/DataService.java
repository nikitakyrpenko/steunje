package com.negeso.framework.navigation;

import java.util.Hashtable;
import java.util.List;

/**
 * 
 * TODO
 * @author Svetlana Bondar
 *
 * @version $Revision: 1$
 */

public interface DataService {
    public List getDataRange(Hashtable mode, int fromIndex, int toIndex);
    
    public int getDataCount(Hashtable mode);    
}
