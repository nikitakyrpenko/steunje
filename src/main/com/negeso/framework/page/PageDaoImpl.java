package com.negeso.framework.page;

import java.sql.Connection;
import java.util.List;


import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.negeso.framework.dao.impl.GenericDaoHibernateImpl;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.wcmsattributes.domain.Attribute;

public class PageDaoImpl extends GenericDaoHibernateImpl<PageH, Long>  implements PageDao{

	private static Logger logger = Logger.getLogger( PageDaoImpl.class );
	
	 private final static String SQL_FIND_BY_CLASS_AND_COMPONENT_NAME =
	        " SELECT {page.*} FROM page " +
	        " WHERE class = ? AND lang_id = ? AND   (select count(*) from page_component where class_name = ? and page_id = page.id) > 0 AND site_id = ?";
	
	
	public PageDaoImpl(){ 
		super(PageH.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PageH> getListByClassAndObligatoryComponent(String klass, Long langId, String component, Long siteId) {
		
		List<PageH> pages = getHibernateTemplate()
							.getSessionFactory()
							.getCurrentSession()
							.createSQLQuery(SQL_FIND_BY_CLASS_AND_COMPONENT_NAME)
							.addEntity("page",PageH.class)
							.setString(0, klass)
							.setLong(1, langId)
							.setString(2, component)
							.setLong(3, siteId)
							.list();
		
	
		
		return pages;
	}


	public Long createPage(PageH page, String template) {
		Connection conn  = null;
		try {
			conn = DBHelper.getConnection();
			page.setAttributeSetId( Attribute.createAttributesFromTemplate(conn, template));
		} catch (Exception e) {
			throw new CriticalException("can't obtain atribute set for page");
		} finally{
			DBHelper.close(conn);
		}
		createOrUpdate(page);
		flush();
		
		
		Query  query = getHibernateTemplate()
		.getSessionFactory()
		.getCurrentSession()
		.createSQLQuery("select tpl_page(?,?,?)");
		
		
		logger.debug(query.getQueryString());
		
		query.setInteger(0,new Integer(page.getId().intValue())) 
				.setString(1, template)
				.setString(2, null).uniqueResult();
		
		return page.getId();
	}

	@Override
	public List<PageH> listByCategory(String category, Long langId, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public List<PageH> listByClass(String klass, Long langId, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public PageH findByFileName(String fileName, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public PageH findByFileNameAndLang(String fileName, Long langId, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public List<PageH> listByLanguage(Long langId, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public List<PageH> listAll(Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public PageH findById(Long id, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public List<PageH> listUnlinkedPages(Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}

	@Override
	public List<PageH> listUnlinkedPagesByLang(Long langId, Long siteId) {
		throw new NotImplementedException("this metod is realized by named query and AOP.");
	}
}
