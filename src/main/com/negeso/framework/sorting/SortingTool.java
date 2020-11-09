package com.negeso.framework.sorting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class SortingTool {

  private static Logger logger = Logger.getLogger(SortingTool.class);

	/**
	 * Positions a single row within its group. May be called
	 * only on a properly numbered table (for ex., after executing reorderAll())
	 * @param rst MySQL resource with <b>2</b> columns: {id, position}
	 *		sorted (!) by position (and probably filtered (!) using group_id,
	 *		if you need to assign position within a separate group of records)
	 * @param funcSupplier @see PositionSetter#setPosition(int, int)
	 * @param id id of the row that is assigned a new position
	 * @param dest the desired position of range 1...N
	 * @return true (success) or false (failed)
	 */
	public static boolean move(ResultSet rst, PositionSetter funcSupplier, int id, int dest){
          logger.debug("+");
		if(dest < 1) dest = 1;
		int curId = 0;
		int curPos = 0;
		try{
			while(rst.next()){
				curId = rst.getInt(1);
				curPos = rst.getInt(2);
				if(curId == id && curPos == dest) {
                                  logger.debug("- true");
                                  return true;
                                }
				if(curId == id || curPos == dest) break;
			}
			if(rst.isAfterLast()) {
                          logger.debug("- false");
                          return false;
                        }
			if(curId == id){ //the id (source) was found first
				while(true){
					int vacant = curPos;
					rst.next();
					if(rst.isAfterLast()){ //dest exceeds the boundary; putting id to the end
                        logger.debug("- funcSupplier.setPosition(id, vacant)");
						return funcSupplier.setPosition(id, vacant);
					}
					curId = rst.getInt(1);
					curPos = rst.getInt(2);
					boolean succ = funcSupplier.setPosition(curId, vacant); //shift the row up
					if(!succ) {
                                          logger.debug("- false");
                                          return false;
                                        }
					if(curPos == dest) {
                                          logger.debug("- funcSupplier.setPosition(id, dest)");
                                          return funcSupplier.setPosition(id, dest);
                                        }
				}
			}else{				//destination position was found first
				boolean succ = funcSupplier.setPosition(id, dest);
				if(!succ) {
                                  logger.debug("- false");
                                  return false;
                                }
				while(true){ //shift down the following rows
					succ = funcSupplier.setPosition(curId, ++dest);
					if(!succ) {
                                          logger.debug("- false");
                                          return false;
                                        }
					rst.next();
					if(rst.isAfterLast()) {
                                          logger.debug("- true");
                                          return true; //all done
                                        }
					if(rst.getInt(1) == id) {
                                          logger.debug("- true");
                                          return true; //the rest wasn't impacted
                                        }
				}
			}
		}catch(SQLException sqle){
            logger.debug("- false", sqle);
			return false;
		}
	}

	/**
	 * Rearranges rows within groups of rows (groups are defined by a foreign key usually)
	 * @param rst MySQL resource with at least <b>3</b> columns: {id, group_id, position}
	 *		sorted by group_id, then by position
	 * @param funcSupplier @see PositionSetter#setPosition(int, int)
	 * @return true (success) or false (failed)
	 */
	public static boolean reorderAll(ResultSet rst, PositionSetter funcSupplier){
          logger.debug("+");
		try{
			rst.last();
			int c = rst.getRow();
			if(c <1) {
                          logger.debug("- false");
                          return false; //wrong parameter;
                        }
			rst.beforeFirst();
			boolean init = true;
			List toEnd = new ArrayList();
			int i = 1;
			int groupID = 0;
			while(true){
				rst.next();
				if(init){ //it is the first row of the table
					groupID = rst.getInt(2); //remember first group
					init = false;
				}
				if(rst.isAfterLast()){ //end of the table is reached
					Iterator iterator = toEnd.iterator();
					while(iterator.hasNext()){
						int item = ((Integer) iterator.next()).intValue();
						boolean succ = funcSupplier.setPosition(item, i++);
						if(!succ) {
                                                  logger.debug("- false");
                                                  return false;
                                                }
					}
                                        logger.debug("- true");
					return true;
				}
				if(groupID != rst.getInt(2)){ // new group begins
					Iterator iterator = toEnd.iterator();
					while(iterator.hasNext()){
						int item = ((Integer) iterator.next()).intValue();
						boolean succ = funcSupplier.setPosition(item, i++);
						if(!succ) {
                                                  logger.debug("- false");
                                                  return false;
                                                }
					}
					groupID = rst.getInt(2); //remember new group
					toEnd.clear();
					i = 1;
				}
				if(!(rst.getInt(3) >= 1)){ //position is below 1 or NULL
					toEnd.add(new Integer(rst.getInt(1)));
				}else if(rst.getInt(3) > 0 && rst.getInt(3) != i){ //position is positive but wrong
					boolean succ = funcSupplier.setPosition(rst.getInt(1), i);
					if(!succ) {
                                          logger.debug("- false");
                                          return false;
                                        }
					i++;
				}else{ //position is ok, store the next position
					i++;
				}
			}
		}catch(SQLException sqle){
                  logger.debug("- false",  sqle);
			return false;
		}
	}
}
