package com.negeso.module.wcmsattributes.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.wcmsattributes.domain.AnimationProperties;

/**
 * 
 * @TODO
 * 
 * @author		Ekaterina Dzhentemirova
 * @version		$Revision: $
 *
 */

public class BrowseAnimationProperties extends AbstractCommand {
	
	private static Logger logger = Logger.getLogger(BrowseAnimationProperties.class);
	
	private static final String IMG_SET_ID = "image_set_id";
	
	private static final String DELAY= "delay";
    
    private static final String STEP = "step";    
    
    private static final String SPEEDOFANIMATION= "speed_of_animation";
    
    public static final String ACTION_GET = "get";
    
    public static final String ACTION_UPDATE = "update";
    

    public BrowseAnimationProperties() {
		super();
	}
	@Override
	public ResponseContext execute() {
		logger.debug("+");
		
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map resultMap = response.getResultMap();
		
		// pass security check
		if (!SecurityGuard.isContributor(request.getSession().getUser())) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
		}
		String action = request.getParameter("action");
		Element page = null;		
		Connection con = null;
		Long imageSetId = null;
		try{
			con = DBHelper.getConnection();
			imageSetId = request.getLong(IMG_SET_ID);
			if (imageSetId == null){
				throw new CriticalException();
			} 
			
			AnimationProperties animaPro = AnimationProperties.findByImageSetId(
					con,
					imageSetId);
			if("update".equalsIgnoreCase(action)){
				animaPro.setDelay(
						request.getLong(DELAY));
				animaPro.setStep(
						request.getLong(STEP));
				animaPro.setSpeedOfAnimation(
						request.getLong(SPEEDOFANIMATION));
				animaPro.update(con);				
			}
			page = buildResultXml(request, animaPro);
		}
		catch(SQLException e){
			logger.error("Object: AnimationProperties not found by id: " + imageSetId, e);
		}
		catch (CriticalException e) {
			response.setResultName(RESULT_FAILURE);
			return response;
        }
		finally{
			DBHelper.close(con);
		}

		response.setResultName(RESULT_SUCCESS);
		response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());		

		logger.debug("-");
		return response;
	}
	
	private Element buildResultXml(RequestContext request, AnimationProperties animaPro) {
		Element page;
		page = XmlHelper.createPageElement(request);
		Element elementPage = Xbuilder.addEl(page, "properties", null);
		Xbuilder.setAttr(elementPage,"image_set_id", animaPro.getId());
		Xbuilder.setAttr(elementPage,"delay", animaPro.getDelay());		
		Xbuilder.setAttr(elementPage,"step", animaPro.getStep());		
		Xbuilder.setAttr(elementPage,"speed_of_animation", animaPro.getSpeedOfAnimation());		
		return page;
	}

}
