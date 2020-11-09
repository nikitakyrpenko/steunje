/**
 * @(#)$Id: StatisticsParameters.java,v 1.0, 2006-01-25 15:34:00Z, Andrey Morskoy$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsParameters {
    HashMap<String,String> map;
    ArrayList<Month> months;
    ArrayList<Integer> years;
    HashMap<Long,String> users;
    
    
    public HashMap<String, String> getMap() {
        return map;
    }
    public ArrayList<Month> getMonths() {
        return months;
    }
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
    public void setMonths(ArrayList<Month> months) {
        this.months = months;
    }
    public ArrayList<Integer> getYears() {
        return years;
    }
    public void setYears(ArrayList<Integer> years) {
        this.years = years;
    }
    public HashMap<Long, String> getUsers() {
        return users;
    }
    public void setUsers(HashMap<Long, String> users) {
        this.users = users;
    }

}
