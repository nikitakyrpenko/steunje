package com.negeso.framework.page;

import java.util.List;

import com.negeso.framework.dao.GenericDao;

public interface PageDao extends GenericDao<PageH, Long>{
	
	 public Long createPage(PageH page, String template);
	 public PageH findById(Long id, Long siteId);
	 public PageH findByFileName(String fileName, Long siteId);
	 public PageH findByFileNameAndLang(String fileName, Long langId, Long siteId);
	 public List<PageH> listByLanguage(Long langId, Long siteId);
	 public List<PageH> listAll(Long siteId);
	 public List<PageH> listUnlinkedPages(Long siteId);
	 public List<PageH> listUnlinkedPagesByLang(Long langId,Long siteId);
	 public List<PageH> listByCategory(String category, Long langId, Long siteId);
	 public List<PageH> listByClass(String klass, Long langId, Long siteId);
	 public List<PageH> getListByClassAndObligatoryComponent(String klass,Long langId,String component,Long siteId);
	

}
