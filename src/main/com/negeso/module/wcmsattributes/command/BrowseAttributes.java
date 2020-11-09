/*
 * Created on 04.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.XmlHelper;

import java.sql.*;

import com.negeso.module.wcmsattributes.WcmsAttributesXmlBuilder;

/**
 * @author OLyebyedyev
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BrowseAttributes extends AbstractCommand {
	private static Logger logger = Logger.getLogger(BrowseAttributes.class);

	private static final String getAtteibuteSetIdSql = " SELECT attribute_set_id FROM page "
			+ " WHERE id = ? ";

	public static final String WCMS_ATTRIBUTE_SET_ID = "wcms_attribute_set_id";

	public static final String PAGE_ID = "page_id";

	public BrowseAttributes() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {
		logger.debug("+");

		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		Map resultMap = response.getResultMap();

		String asetId = request
				.getParameter(BrowseAttributes.WCMS_ATTRIBUTE_SET_ID);
		String pageId = request.getParameter(BrowseAttributes.PAGE_ID);

		if (asetId == null && pageId == null) {
			logger.error("- Attribute Set Id and Page Id are NULL");
			response.setResultName(AbstractCommand.RESULT_FAILURE);
			return response;
		}

		Connection con = null;

		//generate XML with attributes
		Long attr_set_id = null;
		try {
			if (asetId != null) {
				attr_set_id = new Long(asetId);
			} else if (pageId != null) { // get attribute set id
				long pageIdValue = Long.parseLong(pageId);

				con = DBHelper.getConnection();
				PreparedStatement pstmt = con
						.prepareStatement(getAtteibuteSetIdSql);
				pstmt.setLong(1, pageIdValue);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					long tmp = rs.getLong(1);
					if (rs.wasNull())
						attr_set_id = null;
					else
						attr_set_id = new Long(tmp);
				}
				rs.close();
				pstmt.close();
				if (attr_set_id == null) {
					logger.error(" - Corresponded attribute set id not found. "
							+ " Page id is null or not exists: " + pageId);
					response.setResultName(RESULT_FAILURE);
					return response;
				}
			}
		} catch (NumberFormatException e) {
			response.setResultName(RESULT_FAILURE);
			logger.error("- Incorrect attribute set id or page id:" + asetId
					+ ", " + pageId);
			return response;
		} catch (SQLException e) {
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		} finally {
			DBHelper.close(con);
			con = null;
		}

		try {
			/*
			 * if (!SecurityGuard.canContribute(request.getSession().getUser(),
			 * null)) { response.setResultName(RESULT_ACCESS_DENIED);
			 * logger.debug("-"); return response; }
			 */

			con = DBHelper.getConnection();

			int languageId = request.getSession().getLanguage().getId()
					.intValue();

			String languageCode = request.getSession().getLanguage().getCode();


			Element page = XmlHelper.createPageElement(request);

			Element wcmsattrs = WcmsAttributesXmlBuilder.getAttributes(page
					.getOwnerDocument(), con, attr_set_id, false);

			if (wcmsattrs != null)
				page.appendChild(wcmsattrs);

			resultMap.put(OUTPUT_XML, page.getOwnerDocument());
			response.setResultName(RESULT_SUCCESS);

		} catch (CriticalException ex) {
			response.setResultName(RESULT_FAILURE);
			logger.error("- error " + ex, ex);
		} catch (SQLException ex) {
			response.setResultName(RESULT_FAILURE);
			logger.error("- error " + ex, ex);
		} finally {
			DBHelper.close(con);
		}

		logger.debug("-");
		return response;
	}
}