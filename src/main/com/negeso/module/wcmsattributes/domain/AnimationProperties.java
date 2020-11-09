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
 * 
 * @TODO
 * 
 * @author Ekaterina Dzhentemirova
 * @version $Revision: $
 * 
 */

public class AnimationProperties implements DbObject {

    private static Logger logger = Logger.getLogger(AnimationProperties.class);

    private static final String findByImageSetIdSql = " SELECT an.id, an.image_set_id, an.delay, "
	    + " an.step, an.speed_of_animation FROM animation_properties an"
	    + " WHERE an.image_set_id = ? ";

    private static final String updateAnimationPropertiesSql = " UPDATE animation_properties set delay = ?, "
    	+ " step = ?, speed_of_animation = ? WHERE id = ? ";

    private Long id = null;

    private Long attrSetId = null;

    private Long delay = null;

    private Long step = null;

    private Long speedOfAnimation = null;

    public Long getAttrSetId() {
    	return attrSetId;
    }

    public void setAttrSetId(Long attrSetId) {
    	this.attrSetId = attrSetId;
    }

    public Long getDelay() {
    	return delay;
    }

    public void setDelay(Long delay) {
    	this.delay = delay;
    }

    public Long getStep() {
    	return step;
    }

    public void setStep(Long step) {
    	this.step = step;
    }

    public Long getSpeedOfAnimation() {
	return speedOfAnimation;
    }

    public void setSpeedOfAnimation(Long speedOfAnimation) {
	this.speedOfAnimation = speedOfAnimation;
    }

    @Override
    public Long getId() {
    	return id;
    }

    @Override
    public void setId(Long id) {
    	this.id = id;
    }
    
    @Override
    public void delete(Connection con) throws CriticalException {
	// TODO Auto-generated method stub

    }

    @Override
    public int getFieldCount() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public String getInsertSql() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getTableId() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getUpdateSql() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Long insert(Connection con) throws CriticalException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Long load(ResultSet rs) throws CriticalException {
		// TODO Auto-generated method stub
		if (rs == null) {
		    logger.error("- rs is null");
		    return null;
		}
		try {
		    this.id = rs.getLong("id");
		    this.attrSetId = rs.getLong("image_set_id");
		    this.delay = rs.getLong("delay");
		    this.step = rs.getLong("step");
		    this.speedOfAnimation = rs.getLong("speed_of_animation");
	
		} catch (SQLException ex) {
		    logger.error("-", ex);
		    throw new CriticalException(ex);
		}
	
		return this.getId();
    }

    @Override
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
	    throws SQLException {
	// TODO Auto-generated method stub
    	stmt.setLong(1, this.delay);
    	stmt.setLong(2, this.step);            
    	stmt.setLong(3, this.speedOfAnimation);
    	stmt.setLong(4, this.id);
    	stmt.executeUpdate();
        return stmt;
    }

    /***************************************************************************
     * This method returns AnimationProperties
     * 
     * @param con
     * @param image_set_id
     * @return
     */
    public static AnimationProperties findByImageSetId(Connection con,
	    Long image_set_id) throws CriticalException {
		logger.debug("+");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		    pstmt = con.prepareStatement(findByImageSetIdSql);
		    pstmt.setLong(1, image_set_id);
		    rs = pstmt.executeQuery();
		    AnimationProperties animPro = new AnimationProperties();
		    if (rs.next()) {
		    	animPro.load(rs);
				logger.debug("-");
				return animPro;
		    } else {
		    	logger.debug("- not found image for id=" + animPro);
			    throw new CriticalException("AnimationProperties can't be found");
		    }
		} catch (SQLException e) {
		    logger.error("-", e);
		    throw new CriticalException("AnimationProperties can't be found");
		} finally {
			DBHelper.close(rs);
			DBHelper.close(pstmt);	    
		}
    }

    /**
     * 
     * " UPDATE animation_properties SET delay = ?, step = ?, fast_animation = ? "
     * 
     * @param imgIdValue
     * @param src
     * @throws CriticalException
     */
    @Override
    public void update(Connection con) throws CriticalException {
	// TODO Auto-generated method stub
		logger.debug("+");	
		try {
		    PreparedStatement pstmt = con
			    .prepareStatement(updateAnimationPropertiesSql);		    
        	saveIntoStatement(pstmt);
        	
		} catch (SQLException e) {
		    logger.error("-", e);
		    throw new CriticalException(e);
		}
		logger.debug("-");
	    }
	
	    @Override
	    public String getFindByIdSql() {
		// TODO Auto-generated method stub
		return null;
    }
}
