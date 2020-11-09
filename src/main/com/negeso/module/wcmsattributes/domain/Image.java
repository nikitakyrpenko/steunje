/** Created on 06.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;

/**
 * @author OLyebyedyev
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Image implements DbObject {

    private static Logger logger = Logger.getLogger(Image.class);

    private static final String tableId = "image";

    private static final String imageSequenceName = "image_image_id_seq";

    private static final String imageSetTableId = "images_set";

    private static final String imageSetSequenceName = "images_set_image_set_id_seq";

    private Long id = null;

    private Long image_set_id = null;

    private String src = null;

    private String alt = null;

    private Integer max_width = null;

    private Integer max_height = null;

    private Integer min_height = null;

    private Integer min_width = null;

    private Integer width = null;

    private Integer height = null;

    private Integer order = null;
    
    private String link = null;
    
    private String target = null;
	
	/*
     * private static final String select_image = " SELECT image_id,
     * image_set_id, src, alt, max_width, " + " max_height, min_width,
     * min_height width, height " + " FROM " + Image.tableId;
     */
    private static final String insert_image_set_sql = " INSERT INTO "
            + Image.imageSetTableId + " (image_set_id, name) VALUES (?, ?) ";

    private static final String createImageFromTemplateSql = " INSERT INTO image "
            + " (image_set_id, max_width, max_height, required_height, required_width, _order, link, target) "
            + " VALUES (?,?,?,?,?,(SELECT max(_order)+1 FROM image WHERE image_set_id = ?),?,?) ";

    private static final String updateImageSrcSql = " UPDATE image SET src = ? WHERE image_id = ? ";
    
    private static final String updateImageSql = " UPDATE image SET src = ?, alt = ?, max_width = ?, max_height = ?, "+
    	"min_width = ?, min_height = ?, required_height = ?, required_width = ?, "+
    	"_order = ?, link = ?, target = ? WHERE image_id = ? ";
    
    private static final String updateImageSizeSql = " UPDATE image SET max_height = ?, max_width = ? WHERE image_id = ? "; 
    
    private static final String deleteImageSql = " DELETE FROM image WHERE image_id = ? AND "
            + " EXISTS (SELECT 1 FROM image i1, "
            + " (SELECT image_id, image_set_id FROM image WHERE image_id = ?) i2 "
            + " WHERE "
            + " i1.image_id <> i2.image_id "
            + " AND i1.image_set_id = i2.image_set_id) ";

    private static final String findByIdSql = " SELECT image_id, image_set_id, src, alt, max_width, max_height, "
            + " required_height, required_width, _order, link, target FROM image WHERE image_id = ? ";
    
    
    private static final String reorderimagesSql = 
        " SELECT reorder_images(?, ?) ";

    private static final int MAX_ROWS_IN_RESULT = 100;
    
    private static final String getPagesByImageSql = 
        " SELECT count(1), null FROM page p, wcms_attribute_set was, wcms_attribute wa, " +
        " images_set iset, image i " +
        " WHERE i.src = ? " +
        " AND i.image_set_id = iset.image_set_id " +
        " AND i.image_set_id = wa.image_set_id " +
        " AND was.wcms_attribute_set_id = wa.wcms_attribute_set_id " +
        " AND p.attribute_set_id = was.wcms_attribute_set_id " +
        " UNION " +
        " SELECT null, filename FROM page p, wcms_attribute_set was, wcms_attribute wa, " +
        " images_set iset, image i " +
        " WHERE i.src = ? " +
        " AND i.image_set_id = iset.image_set_id " +
        " AND i.image_set_id = wa.image_set_id " +
        " AND was.wcms_attribute_set_id = wa.wcms_attribute_set_id " +
        " AND p.attribute_set_id = was.wcms_attribute_set_id LIMIT " + MAX_ROWS_IN_RESULT;
    
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
        id = newId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet) "
     *      SELECT image_id, image_set_id, src, alt, max_width, max_height,
     *      min_width, min_height width, height "
     */
    public Long load(ResultSet rs) throws CriticalException {

        if (rs == null) {
            logger.error("- rs is null");
            return null;
        }

        try {
            this.setId(new Long(rs.getLong("image_id")));

            long tmp_set_id = rs.getLong("image_set_id");
            if (rs.wasNull())
                this.image_set_id = null;
            else
                this.image_set_id = new Long(tmp_set_id);

            this.src = rs.getString("src");

            this.alt = rs.getString("alt");

            int tmp = rs.getInt("max_width");
            if (rs.wasNull())
                this.max_width = null;
            else
                this.max_width = new Integer(tmp);

            tmp = rs.getInt("max_height");
            if (rs.wasNull())
                this.max_height = null;
            else
                this.max_height = new Integer(tmp);

            tmp = rs.getInt("required_height");
            if (rs.wasNull())
                this.height = null;
            else
                this.height = new Integer(tmp);

            tmp = rs.getInt("required_width");
            if (rs.wasNull())
                this.width = null;
            else
                this.width = new Integer(tmp);

            tmp = rs.getInt("_order");
            if (rs.wasNull())
                this.order = null;
            else
                this.order = new Integer(tmp);
            
            this.link = rs.getString("link");
            
            this.target = rs.getString("target");
            
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        }

        return this.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        // TODO Auto-generated method stub
        return Image.tableId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement pstmt)
            throws SQLException {
        // TODO Auto-generated method stub
	    	pstmt.setString(1, this.src);
	        pstmt.setString(2, this.alt);            
	        pstmt.setObject(3, this.max_width, java.sql.Types.INTEGER);
	        pstmt.setObject(4, this.max_height, java.sql.Types.INTEGER);
	        pstmt.setObject(5, this.min_height, java.sql.Types.INTEGER);            
	        pstmt.setObject(6, this.min_width, java.sql.Types.INTEGER);	        
	        pstmt.setObject(8, this.width, java.sql.Types.INTEGER);
	        pstmt.setObject(7, this.height, java.sql.Types.INTEGER);
	        pstmt.setObject(9, this.order, java.sql.Types.INTEGER);
	        pstmt.setString(10, this.link);
	        pstmt.setString(11, this.target);
	        pstmt.setLong(12, this.id);
	        pstmt.executeUpdate();
        return pstmt;
    }

    public Long getImageSetId() {
        return this.image_set_id;
    }

    public void setImageSetId(Long arg) {
        this.image_set_id = arg;
    }

    public String getSrc() {
        return this.src;
    }

    public void setSrc(String arg) {
        this.src = arg;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String arg) {
        this.alt = arg;
    }

    public Integer getMaxWidth() {
        return this.max_width;
    }

    public void setMaxWidth(Integer arg) {
        this.max_width = arg;
    }

    public Integer getMaxHeight() {
        return this.max_height;
    }

    public void setMaxHeight(Integer arg) {
        this.max_height = arg;
    }

    public Integer getMinWidth() {
        return this.min_width;
    }

    public void setMinWidth(Integer arg) {
        this.min_width = arg;
    }

    public Integer getMinHeight() {
        return this.min_height;
    }

    public void setMinHeight(Integer arg) {
        this.min_height = arg;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer arg) {
        this.width = arg;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer arg) {
        this.height = arg;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer arg) {
        this.order = arg;
    }
    
    public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}


    /**
     * 
     * @param con
     * @param name
     * @return New image_set_id
     * @throws CriticalException
     */
    public static Long createImageSet(Connection con, String name)
            throws CriticalException {
        logger.debug("+");
        Long newId = DBHelper.getNextInsertId(con, Image.imageSetSequenceName);
        try {

            PreparedStatement pstmt = con
                    .prepareStatement(Image.insert_image_set_sql);
            pstmt.setLong(1, newId.longValue());
            pstmt.setString(2, name);
            pstmt.execute();

            pstmt.close();
        } catch (SQLException ex) {
            logger.error("- SQL Exception", ex);
            throw new CriticalException(ex);
        }

        logger.debug("-");
        return newId;
    }

    /**
     * 
     * @param con
     * @param templ
     * @param imgSetId
     * @throws CriticalException
     */
    public static void createImageFromTemplate(Connection con, Image templ,
            Long imgSetId) throws CriticalException {
        logger.debug("+");

        try {
            /*
             * image_set_id, max_width, max_height, required_width,
             * required_height, _order ) " + " VALUES (?,?,?,?,?,(SELECT
             * max(_order)+1 FROM image WHERE image_set_id = ?))
             */
            PreparedStatement pstmt = con
                    .prepareStatement(Image.createImageFromTemplateSql);

            pstmt.setLong(1, imgSetId.longValue());

            pstmt.setObject(2, templ.max_width, java.sql.Types.INTEGER);

            pstmt.setObject(3, templ.max_height, java.sql.Types.INTEGER);

            pstmt.setObject(4, templ.width, java.sql.Types.INTEGER);

            pstmt.setObject(5, templ.height, java.sql.Types.INTEGER);

            pstmt.setLong(6, imgSetId.longValue());

            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
    }

    /**
     * " UPDATE image SET src = ? WHERE image_id = ? "
     * 
     * @param imgIdValue
     * @param src
     * @throws CriticalException
     */
    public static void updateImageSrc(Connection con, long imgId, String src)
            throws CriticalException {
        logger.debug("+");

        if (imgId <= 0) {
            logger.error("Incorrect image id : " + imgId);
            return;
        }
        try {
            PreparedStatement pstmt = con.prepareStatement(updateImageSrcSql);
            pstmt.setString(1, src);
            pstmt.setLong(2, imgId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
    }

    /**
     * " UPDATE image SET src = ? WHERE image_id = ? "
     * 
     * @param imgIdValue
     * @param src
     * @throws CriticalException
     */
    public void update(Connection con)
            throws CriticalException {
        logger.debug("+");

        if (this.id <= 0) {
            logger.error("Incorrect image id : " + this.id);
            return;
        }
        PreparedStatement pstmt = null;
        try {
        	pstmt = con.prepareStatement(updateImageSql);
        	saveIntoStatement(pstmt);

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }finally{
        	DBHelper.close(pstmt);
        }
        logger.debug("-");
    }
    /**
     * " DELETE image WHERE image_id = ? "
     * 
     * @param con
     * @param imgId
     * @throws CriticalException
     */
    public static void deleteImage(Connection con, long imgId)
            throws CriticalException {
        logger.debug("+");

        if (imgId <= 0) {
            logger.error("Incorrect image id : " + imgId);
            return;
        }
        try {
            PreparedStatement pstmt = con.prepareStatement(deleteImageSql);
            pstmt.setLong(1, imgId);
            pstmt.setLong(2, imgId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
    }

    /**
     * 
     * @param con
     * @param imgId
     * @return
     */
    public static Image findById(Connection con, long imgId) {
        logger.debug("+");
        if (imgId <= 0) {
            logger.error("- Incorrect argument imageId: " + imgId);
            return null;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(Image.findByIdSql);
            pstmt.setLong(1, imgId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Image img = new Image();
                img.load(rs);
                logger.debug("-");
                return img;
            } else {
                logger.debug("- not found image for id=" + imgId);
                return null;
            }
        } catch (CriticalException e) {
            logger.error("-", e);
            return null;
        } catch (SQLException e) {
            logger.error("-", e);
            return null;
        } finally {
            try {
                if (rs != null)
                    rs.close();
           
            if (pstmt != null)
                pstmt.close();
            }catch (SQLException e) {
                logger.error("Exception in finally blok of Image.findById:" , e);
            }
        }

    }
    
    
    /**
     * 
     * @param imgId
     * @param direction up or down. >0 - up, <0 - down
     * @return
     */
    public static boolean reorderimages(Connection con, long imgId, int direction ) {
        logger.debug("+");
        
        if (con == null || imgId <=0 || direction  == 0) {
            logger.error("- Incorrect arguments. con == null or " +
                    " imgId <=0 or derection == 0: ");
            logger.error("Connection is null: "+ (con==null ? "true" : "false") );
            logger.error("imgId= " + imgId + "; direction=" + direction);
            return false;
        }
        
        try {
            PreparedStatement pstmt = con.prepareStatement(Image.reorderimagesSql);
            //" SELECT reorder_images(?, ?) ";
            con.setAutoCommit(false);
            pstmt.setLong(1, imgId);
            pstmt.setInt(2, direction);
            pstmt.executeQuery();
            pstmt.close();
            con.commit();
            con.setAutoCommit(true);
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                logger.error(ex);
            }
            logger.error("-", e);
            return false;
        }
        
        
        logger.debug("-");
        return true;
    }
    
    
/******************************************************************************
 * Get pages that have arg as image attribute 
 * @param con - Connection
 * @param imgSrc - path/name of image
 * @return null in case of error. String [] of page.filename on success
 * Max number of result pages is limited. 100 by default
 *****************************************************************************/    
    public static String[] getPagesHavingImageAttribute(Connection con, String imgSrc) {
        logger.debug("+ Looking pages for image: " + imgSrc);
        
        if (imgSrc == null ) {
            logger.error("- Null argue iamge src");
            return null;
        }
        
        String [] res = null;
        try  {
            PreparedStatement pstmt = con.prepareStatement(Image.getPagesByImageSql);
            pstmt.setString(1, imgSrc);
            pstmt.setString(2, imgSrc);
            ResultSet rs = pstmt.executeQuery();
            //first result  - quntity of result rows
            rs.next();
            int quantitiyOfResults = rs.getInt(1);
            if (quantitiyOfResults > MAX_ROWS_IN_RESULT-1) // -1 because 1-st row is count(1)
                quantitiyOfResults = MAX_ROWS_IN_RESULT-1;
            res = new String [quantitiyOfResults];
            for (int i=0; i<quantitiyOfResults; ++i) {
                rs.next(); 
                res[i] = rs.getString(2);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            logger.error("-", e);
            return null;
        }
        logger.debug("- found " + res.length + " pages ");
        return res;
    }
    
    public static void updateImageSize(Connection con, long imgId, long height, long width) 
    throws CriticalException { 
    	logger.debug("+"); 
    	 
    	if (imgId <= 0) { 
    		logger.error("Incorrect image id : " + imgId); 
    		return; 
    	}    	
    	try { 
    		PreparedStatement pstmt = con.prepareStatement(updateImageSizeSql); 
    		pstmt.setLong(1, height);
    		pstmt.setLong(2, width);
    		/*if (width>0)
    			pstmt.setLong(2, width);
    		else
    			pstmt.setString(2, null);*/    		
    		pstmt.setLong(3, imgId); 
    		pstmt.executeUpdate(); 
    		pstmt.close(); 
    		 
    	} catch (SQLException e) { 
    		logger.error("-", e); 
    		throw new CriticalException(e); 
    	} 
    	logger.debug("-"); 
    } 
     

}