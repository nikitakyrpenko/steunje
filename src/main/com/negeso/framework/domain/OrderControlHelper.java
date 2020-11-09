/*
 * @(#)OrderControlHelper.java       @version	03.06.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;


/**
 * Helper class for ordering support in <code>Domain</code> objects. Domain
 * object sould have two fields:
 * - order_number field;
 * - parent id field (odering category field);
 * Provide:
 * - get max order;
 * - incOrder (for object addition);
 * - decOrder (for object deletion);
 * (- move up;)
 * (- move down;)
 *
 * @author Olexiy V. Strashko
 * @version 03.06.2004
 */
public class OrderControlHelper {
    private static final Logger logger = Logger.getLogger(OrderControlHelper.class);

    private String tableId = null;
    private String parentFieldId = "parent_id";
    private String orderFieldId = "order_number";

    private String getMaxOrderSql = null;
    private String incOrderSql = null;
    private String decOrderSql = null;
    private String getPrevOrderSql = null;
    private String getNextOrderSql = null;

    //String getOrderByIdSql = null;
    private String setOrderByIdSql = null;
    private String getIdByParentAndOrderSql = null;
    private String getIdByNullParentAndOrderSql = null;

    private static final Long FIRST_ORDER_NUMBER = new Long(0);

    /**
     *
     */
    public OrderControlHelper(String tableId, String parentFieldId) {
        this.tableId = tableId;
        this.parentFieldId = parentFieldId;

        this.getMaxOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS max FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0}";

        this.incOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " + 1 " +
                        " WHERE " + this.parentFieldId + " {0} ";


        this.decOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " - 1 " +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + ">?"
                ;

        this.getPrevOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS prev FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " < ?"
                ;

        this.getNextOrderSql =
                " SELECT MIN(" + this.orderFieldId + ") AS next FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " > ?"
                ;

        this.getIdByParentAndOrderSql =
                " SELECT id  FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " = ? "
                ;

        this.setOrderByIdSql =
                " UPDATE " + this.tableId + " SET " + this.orderFieldId + "=?" +
                        " WHERE id = ?"
                ;

    }

    public OrderControlHelper(String tableId) {
        this.tableId = tableId;

        this.getMaxOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS max FROM " + this.tableId;

        this.incOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " + 1 ";


        this.decOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " - 1 " +
                        " WHERE " + this.orderFieldId + ">?"
                ;

        this.getPrevOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS prev FROM " + this.tableId +
                        " WHERE " + this.orderFieldId + " < ?"
                ;

        this.getNextOrderSql =
                " SELECT MIN(" + this.orderFieldId + ") AS next FROM " + this.tableId +
                        " WHERE " + this.orderFieldId + " > ?"
                ;

        this.getIdByParentAndOrderSql =
                " SELECT id  FROM " + this.tableId +
                        " WHERE " + this.orderFieldId + " = ? "
                ;

        this.setOrderByIdSql =
                " UPDATE " + this.tableId + " SET " + this.orderFieldId + "=?" +
                        " WHERE id = ?"
                ;

    }

    /**
     *
     */
    public OrderControlHelper(String tableId, String parentFieldId, String clause) {
        this.tableId = tableId;
        this.parentFieldId = parentFieldId;

        this.getMaxOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS max FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} AND " + clause
                ;

        this.incOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " + 1 " +
                        " WHERE " + this.parentFieldId + " {0} AND " + clause
                ;

        this.decOrderSql =
                " UPDATE " + this.tableId +
                        " SET " + this.orderFieldId + "=" + this.orderFieldId + " - 1 " +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + ">? AND " + clause
                ;

        this.getPrevOrderSql =
                " SELECT MAX(" + this.orderFieldId + ") AS prev FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " < ?  AND " + clause
                ;

        this.getNextOrderSql =
                " SELECT MIN(" + this.orderFieldId + ") AS next FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " > ?  AND " + clause
                ;

        this.getIdByParentAndOrderSql =
                " SELECT id  FROM " + this.tableId +
                        " WHERE " + this.parentFieldId + " {0} " +
                        " AND " + this.orderFieldId + " =? " +
                        " AND " + clause
                ;

        this.setOrderByIdSql =
                " UPDATE " + this.tableId + " SET " + this.orderFieldId + "=?" +
                        " WHERE id = ?"
                ;

    }

    /**
     * Incremets order in all elements of the level (defined by parent)
     *
     * @param con
     * @param parentId
     * @return
     * @throws CriticalException
     */
    public long incOrder(Connection con, Long parentId) throws CriticalException {
        logger.debug("+");
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.incOrderSql, (parentId == null ? "is null" : "=?")));
            if (parentId != null)
                stmt.setObject(1, parentId);
            logger.debug("-");
            return stmt.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Decrement order in level from current and upper.
     *
     * @param con
     * @param parentId
     * @param currentOrder
     * @return
     * @throws CriticalException
     */
    public long decOrder(Connection con, Long parentId, Long currentOrder) throws CriticalException {
        logger.debug("+");
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.decOrderSql, (parentId == null ? "is null" : "=?")));
            //logger.error(this.incOrderSql);
            if (parentId != null) {
                stmt.setObject(1, parentId);
                stmt.setLong(2, currentOrder.longValue());
            } else {
                stmt.setLong(1, currentOrder.longValue());
            }
            logger.debug("-");
            return stmt.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }


    /**
     * Get max order for level.
     *
     * @param con
     * @param parentId
     * @return
     * @throws CriticalException
     */
    private boolean hasMaxOrder(Connection con, Long parentId) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.getMaxOrderSql, (parentId == null ? "is null" : "=?")));
            //logger.error("SQL:" + this.getMaxOrderSql);
            if (parentId != null)
                stmt.setObject(1, parentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getObject("max") == null) {
                    return false;
                }
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get max order for level.
     *
     * @param con
     * @param parentId
     * @return
     * @throws CriticalException
     */
    private Long getMaxOrder(Connection con, Long parentId) throws CriticalException {
        try {
            logger.debug("+");
            PreparedStatement stmt = con.prepareStatement(MessageFormat.format(this.getMaxOrderSql, (parentId == null ? "is null" : "=?")));
            if (parentId != null)
                stmt.setObject(1, parentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("max"));
            }
            return OrderControlHelper.FIRST_ORDER_NUMBER;
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get max order for level.
     *
     * @param con
     * @return
     * @throws CriticalException
     */
    private Long getMaxOrder(Connection con) throws CriticalException {
        try {
            logger.debug("+");
            PreparedStatement stmt = con.prepareStatement(this.getMaxOrderSql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("max"));
            }
            return OrderControlHelper.FIRST_ORDER_NUMBER;
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get next insert order number (maximum + 1).
     *
     * @param con
     * @param parentId
     * @return
     * @throws CriticalException
     */
    public Long getNextInsertOrder(Connection con, Long parentId) throws CriticalException {
        if (!this.hasMaxOrder(con, parentId)) {
            //logger.error("Return(ZERO) : " + OrderControlHelper.FIRST_ORDER_NUMBER);
            return OrderControlHelper.FIRST_ORDER_NUMBER;
        }
        //logger.error("Return : " + new Long( this.getMaxOrder(con, parentId).longValue() + 1 ));
        return new Long(this.getMaxOrder(con, parentId).longValue() + 1);
    }


    /**
     * Set order number by id
     *
     * @param con
     * @param currentId
     * @param newOrder
     * @throws CriticalException
     */
    private void setOrderNumberById(Connection con, Long currentId, Long newOrder) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(this.setOrderByIdSql);
            stmt.setLong(1, newOrder.longValue());
            stmt.setLong(2, currentId.longValue());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Move item up. Means exchange of order numbers of current element with
     * previous (smaller order number).
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @param parentId
     * @return
     * @throws CriticalException
     */
    public Long moveUp(Connection con, Long currentId, Long currentOrder, Long parentId) throws CriticalException {
        logger.debug("+");
        Long prevOrder = currentOrder;
        try {
            if (currentOrder.compareTo(OrderControlHelper.FIRST_ORDER_NUMBER) > 0) {

                prevOrder = this.getPrevOrderId(con, currentId, currentOrder, parentId);
                Long prevId = this.getIdByParentAndOrder(con, parentId, prevOrder);

                this.setOrderNumberById(con, currentId, prevOrder);
                this.setOrderNumberById(con, prevId, currentOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("-");
        return prevOrder;
    }

    /**
     * Move item up. Means exchange of order numbers of current element with
     * previous (smaller order number).
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @return
     * @throws CriticalException
     */
    public Long moveUp(Connection con, Long currentId, Long currentOrder) throws CriticalException {
        logger.debug("+");
        Long prevOrder = currentOrder;
        try {
            if (currentOrder.compareTo(OrderControlHelper.FIRST_ORDER_NUMBER) > 0) {

                prevOrder = this.getPrevOrderId(con, currentId, currentOrder);
                Long prevId = this.getIdByParentAndOrder(con, prevOrder);

                this.setOrderNumberById(con, currentId, prevOrder);
                this.setOrderNumberById(con, prevId, currentOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("-");
        return prevOrder;
    }

    /**
     * Move down item. Means order number exchange with next item (with bigger
     * order number)
     *
     * @return
     * @throws SQLException
     */
    public Long moveDown(Connection con, Long currentId, Long currentOrder, Long parentId) throws CriticalException {
        logger.debug("+");
        Long nextOrder = currentOrder;
        if (currentOrder.longValue() < this.getMaxOrder(con, parentId).longValue()) {
            nextOrder = this.getNextOrderId(con, currentId, currentOrder, parentId);
            Long nextId = this.getIdByParentAndOrder(con, parentId, nextOrder);
            this.setOrderNumberById(con, currentId, nextOrder);
            this.setOrderNumberById(con, nextId, currentOrder);
        }
        logger.debug("-");
        return nextOrder;
    }

    /**
     * Move down item. Means order number exchange with next item (with bigger
     * order number)
     *
     * @return
     * @throws SQLException
     */
    public Long moveDown(Connection con, Long currentId, Long currentOrder) throws CriticalException {
        logger.debug("+");
        Long nextOrder = currentOrder;
        if (currentOrder.longValue() < this.getMaxOrder(con).longValue()) {
            nextOrder = this.getNextOrderId(con, currentId, currentOrder);
            Long nextId = this.getIdByParentAndOrder(con, nextOrder);
            this.setOrderNumberById(con, currentId, nextOrder);
            this.setOrderNumberById(con, nextId, currentOrder);
        }
        logger.debug("-");
        return nextOrder;
    }


    /**
     * Get previous order
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @param parentId
     * @return
     * @throws CriticalException
     */
    private Long getPrevOrderId(Connection con, Long currentId, Long currentOrder, Long parentId) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.getPrevOrderSql, (parentId == null ? "is null" : "=?")));
            if (parentId != null) {
                stmt.setObject(1, parentId);
                stmt.setLong(2, currentOrder.longValue());
            } else {
                stmt.setLong(1, currentOrder.longValue());
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("prev"));
            }
            return currentOrder;
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get previous order
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @return
     * @throws CriticalException
     */
    private Long getPrevOrderId(Connection con, Long currentId, Long currentOrder) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(this.getPrevOrderSql);

            stmt.setLong(1, currentOrder.longValue());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("prev"));
            }
            return currentOrder;
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get next order
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @param parentId
     * @return
     * @throws CriticalException
     */
    private Long getNextOrderId(Connection con, Long currentId, Long currentOrder, Long parentId) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.getNextOrderSql, (parentId == null ? "is null" : "=?")));
            if (parentId != null) {
                stmt.setObject(1, parentId);
                stmt.setLong(2, currentOrder.longValue());
            } else {
                stmt.setLong(1, currentOrder.longValue());
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("next"));
            }
            return this.getMaxOrder(con, parentId);
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get next order
     *
     * @param con
     * @param currentId
     * @param currentOrder
     * @return
     * @throws CriticalException
     */
    private Long getNextOrderId(Connection con, Long currentId, Long currentOrder) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(this.getNextOrderSql);
            stmt.setLong(1, currentOrder.longValue());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("next"));
            }
            return this.getMaxOrder(con);
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Find id by parentId and order
     *
     * @param con
     * @param parentId
     * @param orderNumber
     * @return
     * @throws CriticalException
     */
    private Long getIdByParentAndOrder(Connection con, Long parentId, Long orderNumber) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    MessageFormat.format(this.getIdByParentAndOrderSql, (parentId == null ? "is null" : "=?")));
            if (parentId != null) {
                stmt.setObject(1, parentId);
                stmt.setLong(2, orderNumber.longValue());
            } else {
                stmt.setLong(1, orderNumber.longValue());
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("id"));
            } else {
                throw new CriticalException(
                        "Object " + this.tableId +
                                " not found by parent: " + parentId.toString() +
                                " and order: " + orderNumber.toString()
                );
            }
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Find id by parentId and order
     *
     * @param con
     * @param orderNumber
     * @return
     * @throws CriticalException
     */
    private Long getIdByParentAndOrder(Connection con, Long orderNumber) throws CriticalException {
        try {
            PreparedStatement stmt = con.prepareStatement(this.getIdByParentAndOrderSql);
            stmt.setLong(1, orderNumber.longValue());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Long(rs.getLong("id"));
            } else {
                throw new CriticalException(
                        "Object " + this.tableId +
                                " not found by order: " + orderNumber.toString()
                );
            }
        }
        catch (SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

	public void setOrderFieldId(String orderFieldId) {
		if (orderFieldId != null) {
			getMaxOrderSql = getMaxOrderSql.replace(this.orderFieldId, orderFieldId);
			incOrderSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			decOrderSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			getPrevOrderSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			getNextOrderSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			
			setOrderByIdSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			getIdByParentAndOrderSql = incOrderSql.replace(this.orderFieldId, orderFieldId);
			this.orderFieldId = orderFieldId;
		}
	}
}
