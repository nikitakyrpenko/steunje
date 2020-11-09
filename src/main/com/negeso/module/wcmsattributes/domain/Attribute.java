/*
 * Created on 06.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.negeso.framework.domain.*;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author OLyebyedyev
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Attribute implements DbObject {

    private static Logger logger = Logger.getLogger(Attribute.class);

    public static final String tableId = "wcms_attribute";

    public static final String sequenceId = "wcms_attribute_wcms_attr_id_seq";

    public static final String attributeSetTableId = "wcms_attribute_set";

    public static final String attributeSetSequenceId = "wcms_attribute_set_wcms_attribute_set_id_seq";

    public static final String WCMS_ATTRIBUTE_ARTICLE = "article";

    public static final String WCMS_ATTRIBUTE_IMAGE = "picture";

    private Long id = null;

    private Long attrSetId = null;

    private Long attrTypeId = null;

    private ImageSet imgSet = new ImageSet();

    private String name = null;

    private String strValue = null;

    private String attributeClass = null;

    private String typeName = null;

    private Integer typeId = null;

    private WcmsAttributeType type = null;

    private Article article = null;

    private static final String selectAttrSetSql = " SELECT wcms_attr_id, "
            + " wcms_attribute_set_id, type_name, type_id, attribute_name, "
            + " str_value, class, image_set_id, image_set_req_width, "
            + " image_set_req_height, image_set_max_width, "
            + " image_set_max_height, image_set_min_width, "
            + " image_set_min_height, number_of_images, img_id, img_src, "
            + " img_alt, img_max_width, img_max_height, "
            + " img_min_width, img_min_height, img_req_width, "
            + " img_req_height, img_order , img_link, img_target "
            + " FROM wcms_attr_view WHERE wcms_attribute_set_id = ? " +
            		" ORDER BY image_set_id, img_order ";

    private static final String selectAttributeSetId = " SELECT aset.wcms_attribute_set_id "
            + " FROM wcms_attribute_set aset, wcms_attribute a, wcms_attr_template "
            + " WHERE aset.wcms_templ_id = wcms_attr_template.wcms_templ_id "
            + " AND wcms_attr_template.class = ? ";

    
    /** @deprecated */
    private static final String insertAttributeSql = " INSERT INTO "
            + Attribute.tableId + " (wcms_attr_id, " + " image_set_id, "
            + " wcms_attr_type_id, " + " wcms_attribute_set_id, " + " name,"
            + " str_value) " + " VALUES ( ?,?,?,?,?,?) ";

    private static final String insertAttributeFromTemplate = " INSERT INTO "
            + Attribute.tableId + " (image_set_id, wcms_attr_type_id,  "
            + "wcms_attribute_set_id, name, class)  VALUES ( ?,?,?,?,?) ";

    private static final String insertAttributeSetSql = " INSERT INTO "
            + attributeSetTableId + " (wcms_attribute_set_id) VALUES (?) ";
    
    private static final String createAttributesFromTemplate = 
    	" SELECT create_attribute_from_template(?) ";

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#setId(java.lang.Long)
     */
    public void setId(Long newId) {
        if (newId == null || newId.longValue() < 0) {
            logger.error("Incorrect Id: " + newId);
            throw new RuntimeException("Incorrect Id: " + newId);
        }
        this.id = newId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) throws CriticalException {
        throw new UnsupportedOperationException("This method is not supported");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
     */
    public void update(Connection con) throws CriticalException {
        throw new UnsupportedOperationException("This method is not supported");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
        throw new UnsupportedOperationException("This method is not supported");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
            throws SQLException {
        throw new UnsupportedOperationException("This method is not supported");
    }

    /***************************************************************************
     * This method returns template attributes
     * 
     * @param con
     * @param className -
     *            template class name
     * @return @throws
     *         CriticalException
     */
    public static Attribute[] getAttributes(Connection con, String templName)
            throws CriticalException {
        logger.debug("+");
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long id = -1;
        try {
            
            pstmt = con.prepareStatement(Attribute.selectAttributeSetId);
            pstmt.setString(1, templName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            } else {
                logger.error("Cannot find attribute set id for class = "
                        + templName);
                return null;
            }
        } catch (SQLException ex) {
            logger.error("-", ex);
            return null;
        } finally {
            try {
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                logger
                        .error(
                                "SQL Exception on prepared statement and result set close",
                                e);
            }
        }
        Attribute[] a = Attribute.getAttributes(con, id);

        logger.debug("-");
        return a;

    }

    
/**
 * 
 * @param con
 * @param attr_set_id
 * @return
 * @throws CriticalException
 */
    public static Attribute[] getAttributes(Connection con, long attr_set_id)
            throws CriticalException {
        logger.debug("+ attr_set_id=" + attr_set_id);
        if (attr_set_id <= 0) {
            logger.error("- Error. id <= 0 ");
            return new Attribute[0];
        }

        ArrayList attr_set = new ArrayList();
        //ArrayList images = null;
        try {
           
            PreparedStatement pstmt = con
                    .prepareStatement(Attribute.selectAttrSetSql);
            pstmt.setLong(1, attr_set_id);
            ResultSet rs = pstmt.executeQuery();

            long old_attr_id = -1;

            ArrayList tmp_images = new ArrayList(5);

            Attribute atr = null;

            while (rs.next()) {
                logger.debug("Next record");
                long new_id = rs.getLong("wcms_attr_id");

                if (old_attr_id != new_id) {
                    if (tmp_images.size() > 0) {
                        //copy images to array
                        atr = (Attribute) attr_set.get(attr_set.size() - 1);
                        atr.imgSet.images = new Image[tmp_images.size()];
                        tmp_images.toArray(atr.imgSet.images);

                        tmp_images = new ArrayList(5);

                    }
                    atr = new Attribute();
                    //tmp_images = new ArrayList(5);
                    attr_set.add(atr);
                    old_attr_id = new_id;
                    atr = (Attribute) attr_set.get(attr_set.size() - 1);

                    atr.setId(new Long(new_id));

                    atr.attrSetId = new Long(rs
                            .getLong("wcms_attribute_set_id"));
                    atr.typeName = rs.getString("type_name");
                    
                    atr.typeId = new Integer(rs.getInt("type_id"));
                    atr.type = new WcmsAttributeType(atr.typeName);
                    atr.name = rs.getString("attribute_name");
                    atr.strValue = rs.getString("str_value");
                    atr.attributeClass = rs.getString("class");

                    if (atr.typeName.equals(Attribute.WCMS_ATTRIBUTE_IMAGE)) {
                        // read image set
                        atr.imgSet.id = new Long(rs.getLong("image_set_id"));

                        int tmp = rs.getInt("image_set_req_width");
                        if (rs.wasNull())
                            atr.imgSet.requiredWidth = null;
                        else
                            atr.imgSet.requiredWidth = new Integer(tmp);

                        tmp = rs.getInt("image_set_req_height");
                        if (rs.wasNull())
                            atr.imgSet.requiredHeight = null;
                        else
                            atr.imgSet.requiredHeight = new Integer(tmp);

                        tmp = rs.getInt("image_set_max_width");
                        if (rs.wasNull())
                            atr.imgSet.maxWidth = null;
                        else
                            atr.imgSet.maxWidth = new Integer(tmp);

                        tmp = rs.getInt("image_set_max_height");
                        if (rs.wasNull())
                            atr.imgSet.maxHeight = null;
                        else
                            atr.imgSet.maxHeight = new Integer(tmp);

                        tmp = rs.getInt("number_of_images");
                        if (rs.wasNull())
                            atr.imgSet.numberOfImages = null;
                        else
                            atr.imgSet.numberOfImages = new Integer(tmp);

                        atr.imgSet.minWidth = null;
                        atr.imgSet.minHeight = null;
                    }
                }
                if (atr.typeName.equals(Attribute.WCMS_ATTRIBUTE_IMAGE)) { // read
                    // image

                    Image img = new Image();

                    img.setImageSetId(new Long(rs.getLong("image_set_id")));
                    img.setId(new Long(rs.getLong("img_id")));
                    img.setSrc(rs.getString("img_src"));
                    img.setAlt(rs.getString("img_alt"));
                    img.setLink(rs.getString("img_link"));
                    img.setTarget(rs.getString("img_target"));

                    int tmp = rs.getInt("img_max_width");
                    if (rs.wasNull())
                        img.setMaxWidth(null);
                    else
                        img.setMaxWidth(new Integer(tmp));

                    tmp = rs.getInt("img_max_height");
                    if (rs.wasNull())
                        img.setMaxHeight(null);
                    else
                        img.setMaxHeight(new Integer(tmp));

                    img.setMinWidth(null);
                    img.setMinHeight(null);

                    tmp = rs.getInt("img_req_width");
                    if (rs.wasNull())
                        img.setWidth(null);
                    else
                        img.setWidth(new Integer(tmp));

                    tmp = rs.getInt("img_req_height");
                    if (rs.wasNull())
                        img.setHeight(null);
                    else
                        img.setHeight(new Integer(tmp));

                    tmp = rs.getInt("img_order");
                    if (rs.wasNull())
                        img.setOrder(null);
                    else
                        img.setOrder(new Integer(tmp));

                    tmp_images.add(img);

                } else if (atr.typeName
                        .equals(Attribute.WCMS_ATTRIBUTE_ARTICLE)) {
                    Article artcl = new Article();
                    artcl.setId(new Long(rs.getLong("article_id")));
                    artcl.setLangId(new Long(rs.getLong("article_lang_id")));
                    artcl.setHead(rs.getString("article_head"));
                    artcl.setText(rs.getString("article_txt"));
                    artcl.setClass_(rs.getString("article_class"));

                    atr.article = artcl;

                } else {
                    logger.error("- unknown attribute type: " + atr.typeName);
                    continue; //go to next attribute
                }
            }

            if (atr == null || atr.typeName == null)
            {
                logger.error("atr or atr.typename = null");
                return null;
            }
            if (atr.typeName.equals(Attribute.WCMS_ATTRIBUTE_IMAGE)
                    && atr.imgSet.images == null && tmp_images != null) {
                atr.imgSet.images = new Image[tmp_images.size()];
                tmp_images.toArray(atr.imgSet.images);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            logger.error("-", ex);
            return null;
        } catch (Throwable e) {
            logger.error("- \n" + e.getStackTrace(), e);
        }

        Attribute[] res = new Attribute[attr_set.size()];
        logger.debug("-");

        return (Attribute[]) attr_set.toArray(res);
    }

    
    /***************************************************************************
     * 
     * @param con
     * @param attrs
     * @return @throws
     *         CriticalException
     */
    public static Long createAttributesFromTemplate(Connection con,
            Attribute[] attrs) throws CriticalException {

        logger.debug("+");
        Long attrSetId = null;

        // new AttributeSet
        attrSetId = DBHelper.getNextInsertId(con,
                "wcms_attribute_set_wcms_attribute_set_id_seq");
        PreparedStatement pstmt = null;

        try {

            pstmt = con.prepareStatement(insertAttributeSetSql);
            pstmt.setLong(1, attrSetId.longValue());
            pstmt.executeUpdate();
            pstmt.close();

            //add attributes
            for (int i = 0; i < attrs.length; ++i) {

                Long imgSetId = ImageSet.createImageSetFromTemplate(con,
                        attrs[i].imgSet);

                pstmt = con
                        .prepareStatement(Attribute.insertAttributeFromTemplate);
                /*
                 * " (image_set_id, wcms_attr_type_id, " +
                 * "wcms_attribute_set_id, name, class) VALUES ( ?,?,?,?,?) ";
                 */
                pstmt.setLong(1, imgSetId.longValue()); //image_set_id

                pstmt.setObject(2, attrs[i].attrTypeId, java.sql.Types.INTEGER); //wcms_attr_type_id

                pstmt.setObject(3, attrSetId, java.sql.Types.BIGINT);

                pstmt.setString(4, attrs[i].name); // name
                pstmt.setString(5, attrs[i].attributeClass); // class

                pstmt.executeUpdate();
                pstmt.close();

                if (attrs[i].imgSet.images != null) {
                    Image[] images = attrs[i].imgSet.images;
                    for (int j = 0; j < images.length; ++j) {
                        Image.createImageFromTemplate(con, images[j], imgSetId);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error(e);
            throw new CriticalException(e);
        }

        logger.debug("-");
        return attrSetId;
    }

    /***************************************************************************
     * 
     * @param con
     * @param template
     * @return id of the new Attribute
     * @throws CriticalException
     **************************************************************************/
    public static Long createAttributesFromTemplate(Connection con,
            WcmsTemplate template) throws CriticalException {
        logger.debug("+");

        ArrayList elems = template.getElements();
        if (elems == null || elems.size() == 0) {
            logger.error("- No template elements: null or quantity = 0");
            throw new CriticalException("No template elements");
        }

        PreparedStatement pstmt = null;
        Long newAttributeSetId = null;
        try {
            newAttributeSetId = DBHelper.getNextInsertId(con,
                    attributeSetSequenceId);
            pstmt = con.prepareStatement(Attribute.insertAttributeSetSql);

            pstmt.setLong(1, newAttributeSetId.longValue());
            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }

        for (int i = 0; i < elems.size(); ++i) {
            Long imgSetId = null;
            Long articleId = null;
            String strValue = null;
            WcmsTemplateElement element = (WcmsTemplateElement) elems.get(i);
            if (element.elementType == WcmsAttributeType.picture) {
                //create new image set
                imgSetId = Image.createImageSet(con, null);
            } else if (element.elementType == WcmsAttributeType.article) {
                logger.error("- Article attribute is not implemented");
                throw new UnsupportedOperationException(
                        "Article attribute id not omplemented");
            } else {
                logger.error("- Unknown attribute type");
                throw new UnsupportedOperationException(
                        "Unknown attribute type");
            }

            try {
                Long newId = DBHelper.getNextInsertId(con, sequenceId);

                pstmt = con.prepareStatement(insertAttributeSql);
                /*
                 * " INSERT INTO " + Attribute.tableId + " (wcms_attr_id, " + "
                 * image_set_id, " + " wcms_attr_type_id, " + "
                 * wcms_attribute_set_id, " + " name," + " str_value
                 *  
                 */
                pstmt.setLong(1, newId.longValue());
                pstmt.setLong(2, imgSetId.longValue());
                pstmt.setInt(3, element.typeId.intValue());
                pstmt.setLong(4, newAttributeSetId.longValue());
                pstmt.setString(5, element.attributeName);
                pstmt.setString(6, null);

                pstmt.executeUpdate();

                pstmt.close();
                pstmt = null;

            } catch (SQLException ex) {
                logger.error("-", ex);
                throw new CriticalException(ex);
            }
        }

        logger.debug("-");
        return null;
    }

    /**
     * 
     * @return
     */
    public Long getAtributeId() {
        return this.id;
    }

    public Long getAttrSetId() {
        return this.attrSetId;
    }

    public Integer getAttributeTypeId() {
        return this.typeId;
    }

    public WcmsAttributeType getType() {
        return this.type;
    }

    /*
     * public Image[] getImages() { return this.imgSet.images; }
     */
    public ImageSet getImageSet() {
        return this.imgSet;
    }

    public String getAttributeClass() {
        return this.attributeClass;
    }
    
    
    
/**
 * 
 * @param con
 * @param templateName
 * @return
 * @throws CriticalException
 */    
    public static Long createAttributesFromTemplate(Connection con,
            String templateName) throws CriticalException {
    	logger.debug("+ Template: " + templateName);
    	try {
    		PreparedStatement pstmt = 
    			con.prepareStatement(createAttributesFromTemplate);
    		pstmt.setString(1, templateName);
    		ResultSet rs = pstmt.executeQuery();
    		long new_id = -1;
    		if (rs.next()) {
    			new_id = rs.getLong(1);
                if(rs.wasNull()) {
                    new_id = -1;
                }
    		}
    		rs.close();
    		pstmt.close();
			
    		if (new_id<0) {
    			logger.debug("- Null");
    			return null;
    		}
    		logger.debug("- " + new_id);
    		return new Long(new_id);
    		
    	} catch (SQLException e) {
    		logger.error("-", e);
    		return null;
    	}
    }

}