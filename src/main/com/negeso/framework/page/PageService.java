package com.negeso.framework.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.dao.GenericService;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;

public class PageService extends GenericService<PageH>{
	private static Logger logger = Logger.getLogger(PageService.class);

	private PageDao pageDao;
	private SessionFactory sessionFactory;
	private static PageService pageService = null;

	public static PageService getInstance() {
		if (pageService == null) {
			pageService = (PageService)DispatchersContainer.getInstance().getBean("core", "pageService");
		}
		return pageService;
	}

	private final static String SQL_INSERT = " INSERT INTO page("
			+ " id,"
			+ " lang_id,"
			+ " filename,"
			+ " title,"
			+ " contents,"
			+ " class,"
			+ " category,"
			+ " last_modified,"
			+ " protected,"
			+ " publish_date,"
			+ " expired_date,"
			+ " visible,"
			+ " container_id,"
			+ " attribute_set_id,"
			+ " site_id, "
			+ " meta_description,"
			+ " meta_keywords,"
			+ " property_value,"
			+ " google_script,"
			+ " is_search,"
			+ " edit_user,"
			+ " edit_date,"
			+ " meta_title, "
			+ " is_sitemap, "
			+ " sitemap_prior, "
			+ " sitemap_freq "
			+ " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,now(),?,?,?,?)";

	private final static String SQL_FIND_BY_NAME = " SELECT * FROM page WHERE site_id = ? AND lower(filename) = ? ";

	public PageService() {
		super();
	}

	public PageH findById(Long id) {
		return pageDao.findById(id, Env.getSiteId());
	}

	public PageH findByFileName(String fileName) {
		return pageDao.findByFileName(fileName, Env.getSiteId());
	}

	public PageH findByFileNameAndLang(String fileName, Long langId) {
		return pageDao.findByFileNameAndLang(fileName, langId, Env.getSiteId());
	}

	public PageH findByFileNameAndLang(String fileName, String langCode) {
		Language lang = null;
		try {
			lang = Language.findByCode(langCode);
			return findByFileNameAndLang(fileName, lang.getId());
		} catch (Exception e) {
			logger.error("Language not foung by code:" + langCode);
		}
		return null;
	}

	public List<PageH> listByLanguage(Long langId) {
		return pageDao.listByLanguage(langId, Env.getSiteId());
	}

	public List<PageH> listAll() {
		return pageDao.listAll(Env.getSiteId());
	}

	public List<PageH> listUnlinkedPages() {
		return pageDao.listUnlinkedPages(Env.getSiteId());
	}

	public List<PageH> listUnlinkedPages(Long langId) {
		return pageDao.listUnlinkedPagesByLang(langId, Env.getSiteId());
	}

	public List<PageH> listByCategory(String category, Long langId) {
		return pageDao.listByCategory(category, langId, Env.getSiteId());
	}

	public PageH findByCategoryt(String category, Long langId) {
		List<PageH> pages = listByCategory(category, langId);
		if (pages == null || pages.isEmpty()) {
			return null;
		} else {
			return pages.get(0);
		}
	}

	public PageH findByCategory(String category, String langCode) {
		Language lang = null;
		try {
			lang = Language.findByCode(langCode);
			return findByCategoryt(category, lang.getId());
		} catch (Exception e) {
			logger.error("Language not foung by code:" + langCode);
		}
		return null;
	}

	public List<PageH> listByClass(String klass, Long langId) {
		return pageDao.listByClass(klass, langId, Env.getSiteId());
	}

	public PageH findByClass(String klass, Long langId) {
		List<PageH> pages = listByClass(klass, langId);
		if (pages == null || pages.isEmpty()) {
			return null;
		} else {
			return pages.get(0);
		}
	}

	public List<PageH> listByClassAndObligatoryComponent(String klass,
			Long langId, String component) {
		return pageDao.getListByClassAndObligatoryComponent(klass, langId, component, Env.getSiteId());
	}

	public PageH findByClassAndObligatoryComponent(String klass, Long langId, String component) {
		List<PageH> pages = listByClassAndObligatoryComponent(klass, langId, component);
		if (pages == null || pages.isEmpty()) {
			return null;
		} else {
			return pages.get(0);
		}
	}

	public PageH findByClassAndObligatoryComponentT(String klass, Long langId,
			String component) {

		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		session.setFlushMode(FlushMode.AUTO);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

		List<PageH> pages = listByClassAndObligatoryComponent(klass, langId, component);

		TransactionSynchronizationManager.unbindResource(sessionFactory);
		session.flush();
		SessionFactoryUtils.releaseSession(session, sessionFactory);

		if (pages == null || pages.isEmpty()) {
			return null;
		} else {
			return pages.get(0);
		}
	}

	public Long createPage(PageH page, String template) {
		return pageDao.createPage(page, template);
	}

	public void save(PageH page) {
		pageDao.createOrUpdate(page);
		pageDao.flush();
	}

	public void delete(PageH page) {
		pageDao.delete(page);
		pageDao.flush();
	}

	public void delete(Long pageId) {
		PageH page = findById(pageId);
		if (page != null) {
			pageDao.delete(page);
			pageDao.flush();
		}

	}

	public Long insert(Connection connection, PageH page) {
		PreparedStatement insertStatement = null;
		try {
			page.setId(DBHelper.getNextInsertId(connection, "page_id_seq"));
			insertStatement = connection.prepareStatement(SQL_INSERT);
			insertStatement = saveIntoStatement(insertStatement, page);
			insertStatement.execute();
			logger.debug("-");
			return page.getId();
		} catch (SQLException ex) {
			logger.error("- SQLException", ex);
			throw new CriticalException(ex);
		}
	}

	public PreparedStatement saveIntoStatement(PreparedStatement stmt,
			PageH page) throws SQLException {
		logger.debug("+");
		stmt.setObject(1, page.getId());
		stmt.setObject(2, page.getLangId(), Types.BIGINT);
		stmt.setString(3, page.getFilename());
		stmt.setString(4, page.getTitle());
		stmt.setString(5, page.getContents());
		stmt.setString(6, page.getClass_());
		stmt.setString(7, page.getCategory());
		stmt.setTimestamp(8, page.getLastModified());
		stmt.setString(9, page.getProtected_());
		stmt.setTimestamp(10, page.getPublishDate());
		stmt.setTimestamp(11, page.getExpiredDate());
		stmt.setBoolean(12, page.isVisible());
		stmt.setObject(13, page.getContainerId());
		stmt.setObject(14, page.getAttributeSetId());
		stmt.setObject(15, Env.getSiteId());
		stmt.setString(16, page.getMetaDescription());
		stmt.setString(17, page.getMetaKeywords());
		stmt.setString(18, page.getPropertyValue());
		stmt.setString(19, page.getGoogleScript());
		stmt.setBoolean(20, page.isSearch());
		stmt.setString(21, page.getEditUser());
		stmt.setString(22, page.getMetaTitle());
		stmt.setBoolean(23, page.isSitemap());
		stmt.setString(24, page.getSitemapPrior());
		stmt.setString(25, page.getSitemapFreq());
		logger.debug("-");
		return stmt;
	}

	public PageH findByFileName(String fileName, Connection connection)
			throws SQLException {
		PreparedStatement findStatement = null;
		ResultSet rs = null;
		try {
			PageH result = null;
			findStatement = connection.prepareStatement(SQL_FIND_BY_NAME);
			findStatement.setLong(1, Env.getSiteId());
			findStatement.setString(2, fileName.toLowerCase());
			rs = findStatement.executeQuery();
			if (rs.next()) {
				result = PageH.load(rs);
			}
			logger.debug("-");
			return result;
		} catch (SQLException ex) {
			logger.error("- SQLException", ex);
			throw ex;
		} finally {
			DBHelper.close(rs);
			DBHelper.close(findStatement);
		}
	}

	public PageDao getPageDao() {
		return pageDao;
	}

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void createOrUpdate(PageH entity) {
		pageDao.createOrUpdate(entity);
		pageDao.flush();
	}

}
