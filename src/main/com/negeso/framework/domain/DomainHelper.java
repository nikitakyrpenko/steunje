package com.negeso.framework.domain;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 22, 2005
 */

public class DomainHelper {
    public static Object fromObject(Object obj)
            throws ClassCastException
    {
        if (obj == null){
            return null;
        }
        else if (obj.getClass() == String.class){
            return obj;
        }
        else if (obj.getClass() == Integer.class){
            return new Long(((Integer)obj).longValue());
        }
        else if (obj.getClass() == Timestamp.class){
            return obj;
        }
        else if (obj.getClass() == Date.class){
            return obj;
        }
        else{
            throw new ClassCastException("Can't cast '"+obj.getClass().toString()+"' class");
        }
    }

    public static void toStatement(Object obj, PreparedStatement stat, int pos)
            throws Exception
    {
        if (obj == null){
            stat.setObject(pos, null);
        }
        else if (obj.getClass() == String.class){
            stat.setString(pos, (String)obj);
        }
        else if (obj.getClass() == Long.class){
            stat.setLong(pos, ((Long)obj).longValue());
        }
        else if (obj.getClass() == Timestamp.class){
            stat.setTimestamp(pos, (Timestamp)obj);
        }
        else if (obj.getClass() == Date.class){
            stat.setDate(pos, (Date)obj);
        }
        else{
            throw new ClassCastException("Can't cast '"+obj.getClass().toString()+"' class");
        }
    }
}
