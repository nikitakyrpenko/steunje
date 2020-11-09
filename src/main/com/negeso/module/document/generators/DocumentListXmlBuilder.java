package com.negeso.module.document.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.State;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.domain.DocumentEntry;


public class DocumentListXmlBuilder {
	private static String documentListSql = 
		"  ";
	private static String categoryListSqlUnauthorized = 
		" SELECT id, name " +
		" FROM document_category " +
		" WHERE container_id IS NULL AND ( lang_id IS NULL OR lang_id=? ) ";
	
	private static String fileListSqlUnauthorized = 
		" SELECT id, name, document_link, last_modified, owner, description, thumbnail_link  " +
		" FROM document ";
	
	private static Logger logger = Logger.getLogger( DocumentListXmlBuilder.class );
	
	public static void buildDocumentListXml(Connection conn, Element parent, State state, Long userId, Long langId) 
			throws Exception{
		
	}
	
	public static void buildDocumentListXmlUnauthorized(Connection conn, Element parent, State state, Long langId) 
			throws Exception{

		String sql = categoryListSqlUnauthorized;
		String fileSql = fileListSqlUnauthorized;
		if (state.getCurrentFolderId() == null){
			sql += " AND parent_id IS NULL ORDER BY name ";
			fileSql += " WHERE category_id IS NULL ORDER BY name ";
		}
		else{
			sql += " AND parent_id=? ORDER BY name ";
			fileSql += " WHERE category_id=? ORDER BY name ";
		}
		PreparedStatement stat = conn.prepareStatement(sql);
		stat.setLong(1, langId.longValue());
		if (state.getCurrentFolderId() != null){
			stat.setLong(2, state.getCurrentFolderId().longValue());	
		}
		ResultSet res = stat.executeQuery();
		buildCategoryXmlFromRS(parent, res);
		res.close();
		
		stat = conn.prepareStatement(fileSql);
		if (state.getCurrentFolderId() != null){
			stat.setLong(1, state.getCurrentFolderId().longValue());	
		}
		res = stat.executeQuery();
		buildFileXmlFromRS(parent, res);
		res.close();
	}
	
	private static void buildCategoryXmlFromRS(Element parent, ResultSet res) throws Exception{
		Element doc_category = null;
		while (res.next()){
			doc_category = Xbuilder.addEl(parent, "doc_category", null);
			doc_category.setAttribute("id", res.getString("id"));
			doc_category.setAttribute("name", res.getString("name"));
		}
	}
	
	private static void buildFileXmlFromRS(Element parent, ResultSet res) throws Exception{
		Element doc_file = null;
		while (res.next()){
			doc_file = Xbuilder.addEl(parent, "doc_file", null);
			doc_file.setAttribute("id", res.getString("id"));
			doc_file.setAttribute("name", res.getString("name"));
			doc_file.setAttribute("link", res.getString("document_link"));

			
			Xbuilder.setAttr(doc_file, "thumbnail_link", res.getString("thumbnail_link"));
			if (res.getString("thumbnail_link") != null) {
				Xbuilder.setAttr(doc_file, "type", "image");
			} else {
				Xbuilder.setAttr(doc_file, "type", "file");
			}
			
 			Timestamp mod = res.getTimestamp("last_modified");
			if (mod!=null){
                Calendar cl = Calendar.getInstance();
                cl.setTime(mod);
                doc_file.setAttribute("modified",
                        cl.get(Calendar.YEAR)+"-"
                        + (cl.get(Calendar.MONTH)+1)+"-"
                        + cl.get(Calendar.DAY_OF_MONTH));
			}
			doc_file.setAttribute("owner", res.getString("owner"));
			doc_file.setAttribute("description", res.getString("description"));
		}
	}
    
    
    public static Element buildDocumentDetailsXml(
        Long docId, Connection con, Element parent, Long langId, Long userId) 
        throws CriticalException
    {
        Element catEl = null;
        try{
            DocumentEntry doc = DocumentEntry.findById(con, docId);
            
            catEl = CategoryXmlBuilder.buildPathXml(doc.getCategoryId(), con, parent, userId, langId, false);
            Xbuilder.setAttr(catEl, "is_current", "true");

            Category cat = doc.getCategory(con);
            
            Element docEl = Xbuilder.addEl(catEl, "document", null);
            Xbuilder.setAttr(docEl, "id", doc.getId());
            Xbuilder.setAttr(docEl, "name", doc.getName());
            Xbuilder.setAttr(docEl, "document_link", doc.getDocumentLink());
            Xbuilder.setAttr(docEl, "description", doc.getDescription());
            Xbuilder.setAttr(docEl, "owner", doc.getOwner());
            Xbuilder.setAttr(docEl, "last_modified", Env.formatRoundDate(doc.getLastModified()));
			Xbuilder.setAttr(docEl, "thumbnail_link", doc.getThumbnailLink());
            
            Xbuilder.setAttr(
                docEl, 
                "can_edit",
                SecurityGuard.canEdit(userId, cat.getContainerId())
            );
        }
        catch(Exception e){
            logger.error("-errror", e);
            throw new CriticalException(e);
        }
        return catEl;
    }
}
