/*
 * @(#)Id: BannerService.java, 13.12.2007 19:31:36, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.page.Page;
import com.negeso.framework.site_map.PagesHandler;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.bo.Banner2Group;
import com.negeso.module.banner_module.bo.Banner2Page;
import com.negeso.module.banner_module.bo.BannerStatistics;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.dao.Banner2GroupDao;
import com.negeso.module.banner_module.dao.Banner2PageDao;
import com.negeso.module.banner_module.dao.BannerDao;
import com.negeso.module.banner_module.dao.BannerStatisticsDao;
import com.negeso.module.banner_module.dao.HibernateBannerDao;
import com.negeso.module.banner_module.web.controller.BannerCategoryListController;
import com.negeso.module.user.domain.Group;

/**
 * 
 * @TODO
 * 
 * @version Revision:
 * @author Dmitry Fedotov
 * 
 */
@Transactional
public class BannerService {

	static Logger logger = Logger.getLogger(BannerService.class);

	public static final String PAGE_TYPE_MENUITEM = "menuitem";
	public static final String PAGE_TYPE_UNLINKED = "unlinked";
	public static final String PAGE_TYPE_PRODUCT = "product";
	public static final String PAGE_TYPE_CATEGORY = "category";

	private BannerDao bannerDao;
	private Banner2PageDao banner2PageDao;
	private Banner2GroupDao banner2GroupDao;
	private HibernateBannerDao hibernateBannerDao;
	private BannerTypeService bannerTypeService;
	private BannerStatisticsDao bannerStatisticsDao;

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public List<Banner> findAllBanners() {
		logger.debug("+ -");
		return bannerDao.listAllBanners();
	}

	public Banner findById(Long bannerId) {
		logger.debug("+ -");
		return bannerDao.read(bannerId);
	}

	private String prepareQuery(Long categoryId, Map<String, Object> params) {
		logger.debug("+");
		StringBuilder query = new StringBuilder(128);
		
		query
		.append("from Banner b where b.categoryId = ")
		.append(categoryId);

		if (params.get(BannerCategoryListController.FILTER_PRIORITY) != null) {
			query
			.append(" and priority = ")
			.append(params.get(BannerCategoryListController.FILTER_PRIORITY));
		}

		if (params.get(BannerCategoryListController.FILTER_ORDER) != null) {
			if (params.get(BannerCategoryListController.FILTER_ORDER).equals("priority")) {
				query.append(" order by priority ");
			} else if (params.get(BannerCategoryListController.FILTER_ORDER).equals("clicks")) {
				query.append(" order by max_clicks ");
			} else if (params.get(BannerCategoryListController.FILTER_ORDER).equals("views")) {
				query.append(" order by max_views ");
			} else {
				query.append(" order by title ");
			}
		} else {
			query.append(" order by title ");
		}

		Boolean bool = (Boolean) params.get(BannerCategoryListController.FILTER_DIRECTION);
		if (bool != null && !bool) {
			query.append(" desc");
		} else {
			query.append(" asc");
		}
		
		logger.debug("-");
		return query.toString();
	}
	
	public List<Banner> findParametrizedBannerList(Long categoryId,
			Map<String, Object> params) {
		logger.debug("+");
		
		String query = prepareQuery(categoryId, params);
		logger.debug("query=" + query);
		logger.debug("?=" + params.get(BannerCategoryListController.FILTER_EXPIRED));

		List<Banner> banners = hibernateBannerDao.findParametrizedBannerList(
				categoryId, query);
		List<Banner> parametrizedBanners = banners;

		if (params.get(BannerCategoryListController.FILTER_EXPIRED) != null
				&& banners != null) {
			parametrizedBanners = new ArrayList<Banner>();
			for (Banner banner : banners) {
				logger.debug("b=" + banner);
				Date currentDate = new Date(System.currentTimeMillis());
				boolean isBefore = (banner.getPublishDate() == null) ?
						false : banner.getPublishDate().before(currentDate); 
				boolean isExpired = (banner.getExpiredDate() == null) ?
						false : banner.getExpiredDate().after(currentDate); 
				if (isBefore && (banner.getExpiredDate() == null || isExpired)) {
					if ((params.get(BannerCategoryListController.FILTER_EXPIRED)).equals(Boolean.FALSE)) {
						parametrizedBanners.add(banner);
						logger.debug("+parametrized banner (filter expired=false): " + banner);
					}
				} else {
					if ((params.get(BannerCategoryListController.FILTER_EXPIRED)).equals(Boolean.TRUE)) {
						parametrizedBanners.add(banner);
						logger.debug("+parametrized banner (filter expired=true): " + banner);
					}
				}
			}
		} else {
			parametrizedBanners = banners;
		}

		return parametrizedBanners;
	}

	public List<Banner> findBannersByTypeId(Long typeId) {
		logger.debug("+ -");
		return bannerDao.listBannersByTypeId(typeId);
	}
	
	public List<Banner> findBanners(Long pageId, Long userId, Long pmCatId,
			Long pmProdId, Long langId) {
		logger.debug("+");

		if (logger.isDebugEnabled()) {
			logger.debug(String.format(
					"pageId=%s, userId=%s, pmCatId=%s, pmProdId=%s, langId=%s",
					pageId, userId, pmCatId, pmProdId, langId));
		}
		List<Group> groups = getGroups(userId);
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (Group g : groups)
				sb.append(ReflectionToStringBuilder.toString(g)).append("\n");
			logger.debug(String.format("groups=%s", sb.toString()));
		}
		List<Banner> filteredByPage = getFilteredByPage(langId, pageId, pmCatId, pmProdId, groups);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("banners, filtered by page=%s", filteredByPage));
		}
		Map<Long, List<Banner>> dByType = getTypes(filteredByPage);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("banner types=%s", dByType));
		}
		List<Banner> banners = getBanners(dByType);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("banners=%s", banners));
		}

		logger.debug("-");
		return banners;
	}

	private List<Group> getGroups(Long userId) {
		List<Group> groups = new ArrayList<Group>();
		if (userId != null) {
			try {
				groups = Group.findByUserId(userId);
			} catch (Exception e) {
				logger.debug("cannot find group");
				groups.add(Group.getGuestGroup());
			}
		} else {
			groups.add(Group.getGuestGroup());
		}
		return groups;
	}
	
	private List<Banner> getFilteredByPage(Long langId, Long pageId, Long pmCatId, Long pmProdId, List<Group> groups) {
		List<Banner> filteredByPage = null;

		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			String fName = getProductModulePageName(langId);
			if (logger.isDebugEnabled())
				logger.debug("product page=" + fName);
			
			if(fName != null){
				Page productPage = Page.findByFileName(fName, conn);
			if (productPage != null && productPage.getId().equals(pageId)) {
				logger.debug("find banners for product pages");
				filteredByPage = findProductBanners(pageId, pmCatId, pmProdId);
				}
			}
			if(filteredByPage == null){
				filteredByPage = findBannersByPageId(pageId);
			}	
			filteredByPage = getAvaiableBanners(filteredByPage);
			for (Group group : groups) {
				try {
					filteredByPage = filterExistListByGroupId(filteredByPage,
							group.getId());
				} catch (Exception e) {
					logger.error("error filtering ba group:" + e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("error getting product page: " + e.getMessage());
			filteredByPage = new ArrayList<Banner>();
		} finally {
			DBHelper.close(conn);
		}
		return filteredByPage;
	}
	
	public String getProductModulePageName(Long langId) throws CriticalException{
		PagesHandler pmPagesHandler = SiteMapBuilder.getInstance().getHandlers().get("product");
		if (pmPagesHandler == null ){
			throw new CriticalException("Product module is not activated");
		}
		return pmPagesHandler.getPages(langId, 0L).getHref();
	}
	
	private Map<Long, List<Banner>> getTypes(List<Banner> filteredByPage) {
		Map<Long, List<Banner>> typesMap = new LinkedHashMap<Long, List<Banner>>();

		try {
			List<BannerType> types = bannerTypeService.findAllTypes();
		
			for (BannerType bannerType : types) {
				List<Banner> list = new ArrayList<Banner>();
				for (Banner banner : filteredByPage) {
					if (banner.getBannerTypeId().equals(bannerType.getId())) {
						list.add(banner);
					}
				}
				typesMap.put(bannerType.getId(), list);
			}
		} catch (Exception e) {
			logger.error("error, cannot find banner types:" + e.getMessage());
		}
		return typesMap;
	}

	private List<Banner> getBanners(Map<Long, List<Banner>> types) {
		Set<Long> s = types.keySet();

		List<Banner> result = new ArrayList<Banner>();
		for (Long typeId : s) {
			Banner b = getTheBestBanner(types.get(typeId));
			if (b != null) {
				try {
					incBannerView(b);
				} catch (Exception e) {
					logger.debug("- error incrementing banner view:" + e.getMessage());
				}
				result.add(b);
			}
		}
		return result;
	}
	
	public void incBannerView(Banner banner) {
		logger.debug("+");

		Timestamp currTimeStamp = null;
		try {
			currTimeStamp = new Timestamp(DATE_FORMAT.parse(DATE_FORMAT.format(new Date())).getTime());
		} catch (ParseException e) {
			logger.error("error parsing date: " + e.getMessage());
		}
		BannerStatistics bStatistics = bannerStatisticsDao.findByDate(banner.getId(), currTimeStamp);
		if (bStatistics != null) {
			bStatistics.setViews(bStatistics.getViews() + 1L);
			bannerStatisticsDao.update(bStatistics);
		} else {
			bStatistics = new BannerStatistics();
			bStatistics.setBannerId(banner.getId());
			bStatistics.setViews(1L);
			bStatistics.setDate(currTimeStamp);
			bannerStatisticsDao.create(bStatistics);
		}
		logger.debug("-");
	}

	public void incBannerClick(Banner banner) {
		logger.debug("+");
		
		Timestamp currTimeStamp = null;
		try {
			currTimeStamp = new Timestamp(DATE_FORMAT.parse(DATE_FORMAT.format(new Date())).getTime());
		} catch (ParseException e) {
			logger.error("error parsing date: " + e.getMessage());
		}
		BannerStatistics bStatistics = bannerStatisticsDao.findByDate(banner.getId(), currTimeStamp);
		if (bStatistics != null) {
			bStatistics.setClicks(bStatistics.getClicks() + 1L);
			bannerStatisticsDao.update(bStatistics);
		} else {
			bStatistics = new BannerStatistics();
			bStatistics.setBannerId(banner.getId());
			bStatistics.setClicks(1L);
			bStatistics.setDate(currTimeStamp);
			bannerStatisticsDao.create(bStatistics);
		}
		logger.debug("-");
	}

	private Banner getTheBestBanner(List<Banner> banners) {
		logger.debug("+");

		if (banners.size() == 1)
			return banners.get(0);

		Long sum = 0L;
		for (Banner banner : banners)
			sum += banner.getPriority();
		
		logger.debug("sum=" + sum);
		
		List<Long> priors = new ArrayList<Long>();
		priors.add(0L);
		for (int i = 0; i < banners.size(); i++) {
			Long l = new Long((sum - banners.get(i).getPriority()) * 100 / sum);
			logger.debug("banner=" + banners.get(i));
			logger.debug("prior =" + l);
			priors.add((l + priors.get(i)));
		}
		Long r = Long.valueOf(String.valueOf(new Random().nextInt(100)));
		logger.debug("random value = " + r);

		for (int i = 0; i < priors.size() - 1; i++) {
			logger.debug(priors.get(i) + "|" + r + "|" + priors.get(i + 1));
			if (r >= priors.get(i) && r <= priors.get(i + 1)) {
				logger.debug("    selected -" + i);
				return banners.get(i);
			}
		}
		logger.debug("cannot find most prioriet banner");
		return null;
	}

	private List<Banner> findProductBanners(Long pageId, Long pmCatId,
			Long pmProdId) {
		logger.debug("+");
		if (pmCatId != null) {
			logger.debug("-");
			return banner2PageDao.listBannersByPmCategoryId(pageId, pmCatId);
		} else if (pmProdId != null) {
			logger.debug("-");
			return banner2PageDao.listBannersByPmProductId(pageId, pmProdId);
		}
		logger.debug("cannot find any product banners");
		return new ArrayList<Banner>();
	}

	private List<Banner> filterExistListByGroupId(List<Banner> banners,
			Long groupId) {
		logger.debug("+");
		List<Banner> resList = new ArrayList<Banner>();
		for (Banner banner : banners) {
			if (banner != null && isInBannerGroup(banner, groupId)) {
				resList.add(banner);
			}
		}
		logger.debug("-");
		return resList;
	}

	private boolean isInBannerGroup(Banner banner, Long gId) {
		logger.debug("+ -");
		return banner2GroupDao.findByBannerIdByGroupId(banner.getId(), gId) != null;
	}

	private List<Banner> getAvaiableBanners(List<Banner> banners) {
		logger.debug("+");
		List<Banner> resList = new ArrayList<Banner>();
		Date currentDate = new Date(System.currentTimeMillis());
		for (Banner banner : banners) {
			if (banner.getActivated()){
				if (banner.getExpiredDate() == null || banner.getExpiredDate().after(currentDate)){
					if (banner.getPublishDate().before(currentDate)){
						Long countTotalClicks = hibernateBannerDao.countTotalClicksCount(banner.getId());
						Long countTotalViews = hibernateBannerDao.countTotalViewsCount(banner.getId());
						if ((banner.getMaxClicks() == 0L || countTotalClicks < banner.getMaxClicks())
								&& (banner.getMaxViews() == 0L || countTotalViews < banner.getMaxViews())){
							resList.add(banner);
						}
					}
				}
			}
		}
		logger.debug("-");
		return resList;
	}

	public void createBanner(Banner banner) {
		logger.debug("+");
		bannerDao.create(banner);
		logger.debug("-");
	}

	public void updateBanner(Banner banner) {
		logger.debug("+");
		bannerDao.update(banner);
		logger.debug("-");
	}

	public void deleteBanner(Long bannerId) {
		logger.debug("+");
		Banner banner = bannerDao.read(bannerId);
		bannerDao.delete(banner);
		logger.debug("-");
	}

	public String findStringBannerPagesIds(Long bannerId) {

		StringBuilder ids = new StringBuilder(4096);

		for (Banner2Page banner2Page : listBannerPages(bannerId, PAGE_TYPE_MENUITEM)) {
			ids.append(banner2Page.getPageId()).append(";");
		}
		ids.append("#");
		for (Banner2Page banner2Page : listBannerPages(bannerId, PAGE_TYPE_UNLINKED)) {
			ids.append(banner2Page.getPageId()).append(";");
		}
		ids.append("#");
		for (Banner2Page banner2Page : listBannerPages(bannerId, PAGE_TYPE_CATEGORY)) {
			ids.append(banner2Page.getPmCategoryId()).append(";");
		}
		ids.append("#");
		for (Banner2Page banner2Page : listBannerPages(bannerId, PAGE_TYPE_PRODUCT)) {
			ids.append(banner2Page.getPmProductId()).append(";");
		}
		ids.append("#");
		return ids.toString();
	}

	public void updateBanner2Page(Long bannerId, String[] ids, Long langId)
			throws Exception {
		
		logger.debug("+");
		
		if (ids == null) {
			logger.debug("- ids are empty.");
			return;
		}

		deleteBanner2PageGroup(bannerId, PAGE_TYPE_MENUITEM);
		if (ids.length > 0 && ids[0] != null) {
			updateMenuItemPages(bannerId, ids[0].split(";"), PAGE_TYPE_MENUITEM);
		}

		deleteBanner2PageGroup(bannerId, PAGE_TYPE_UNLINKED);
		if (ids.length > 1 && ids[1] != null) {
			updateMenuItemPages(bannerId, ids[1].split(";"), PAGE_TYPE_UNLINKED);
		}

		deleteBanner2PageGroup(bannerId, PAGE_TYPE_CATEGORY);
		if (ids.length > 2 && ids[2] != null) {
			updateProductPages(bannerId, ids[2].split(";"), PAGE_TYPE_CATEGORY,
					langId);
		}

		deleteBanner2PageGroup(bannerId, PAGE_TYPE_PRODUCT);
		if (ids.length > 3 && ids[3] != null) {
			updateProductPages(bannerId, ids[3].split(";"), PAGE_TYPE_PRODUCT,
					langId);
		}
		logger.debug("-");
	}

	private void deleteBanner2PageGroup(Long bannerId, String type) {
		List<Long> currBanner2GroupIds = findBanner2PagesIds(bannerId, type);
		for (Long currId : currBanner2GroupIds) {
			deleteBanner2Page(currId);
		}
	}

	private void updateProductPages(Long bannerId, String[] ids, String type,
			Long langId) {
		logger.debug("+");
		if (ids == null) {
			logger.debug("- ids are empty.");
			return;
		}
		List<Long> lids = new ArrayList<Long>();
		for (String s : ids) {
			if (s == null || s.trim().length() == 0) {
				break;
			}
			try {
				lids.add(Long.parseLong(s));
			} catch (Exception e) {
				logger.debug("- error parsing string id to long");
				break;
			}
		}

		Connection conn = null;
		Page productPage = null;
		try {
			conn = DBHelper.getConnection();
			String fName = getProductModulePageName(langId);
			productPage = Page.findByFileName(fName, conn);
		} catch (SQLException e) {
			logger.error("cannot find product module page");
		} catch (ObjectNotFoundException e) {
			logger.error("cannot find product page: " + e.getMessage());
		} finally {
			DBHelper.close(conn);
		}

		for (Long currId : lids) {
			if (type.equals(PAGE_TYPE_CATEGORY)) {
				createBanner2Page(new Banner2Page(bannerId,
						productPage.getId(), null, currId, type));
			} else if (type.equals(PAGE_TYPE_PRODUCT)) {
				createBanner2Page(new Banner2Page(bannerId,
						productPage.getId(), currId, null, type));
			}
		}
		logger.debug("-");
	}

	private void updateMenuItemPages(Long bannerId, String[] ids, String type) {
		logger.debug("+");
		if (ids == null) {
			logger.debug("- ids are empty.");
			return;
		}
		List<Long> lids = new ArrayList<Long>();
		for (String s : ids) {
			if (s == null || s.trim().length() == 0) {
				break;
			}
			try {
				lids.add(Long.parseLong(s));
			} catch (Exception e) {
				logger.debug("- error parsing string id to long");
				break;
			}
		}

		for (Long currId : lids) {
			createBanner2Page(new Banner2Page(bannerId, currId, null, null,
					type));
		}
		logger.debug("-");
	}

	public void createBanner2Page(Banner2Page banner2Page) {
		logger.debug("+");
		banner2PageDao.create(banner2Page);
		logger.debug("-");
	}

	public void deleteBanner2Page(Long Id) {
		logger.debug("+");
		Banner2Page banner2Page = banner2PageDao.read(Id);
		banner2PageDao.delete(banner2Page);
		logger.debug("-");
	}

	public List<Long> findBanner2PagesIds(Long bannerId, String type) {
		logger.debug("+ -");
		return banner2PageDao.listBanner2PagesIds(bannerId, type);
	}

	public Banner2Page findBanner2PageByProductCategoryId(Long bannerId,
			Long productCategoryId) {
		logger.debug("+ -");
		return banner2PageDao.findBanner2PageByProductCategoryId(bannerId,
				productCategoryId, PAGE_TYPE_CATEGORY);
	}

	public Banner2Page findBanner2PageByProductId(Long bannerId, Long productId) {
		logger.debug("+ -");
		return banner2PageDao.findBanner2PageByProductId(bannerId, productId,
				PAGE_TYPE_PRODUCT);
	}

	public List<Banner> findBannersByPageId(Long pageId) {
		logger.debug("+ -");
		return banner2PageDao.listBannersByPageId(pageId);
	}

	public List<Banner2Page> listBannerPages(Long bannerId, String type) {
		logger.debug("+ -");
		return banner2PageDao.listBannerPages(bannerId, type);
	}

	public String findStringBannerGroupIds(Long bannerId) {
		logger.debug("+");
		String str = "";
		for (Banner2Group banner2Group : findBannerGroups(bannerId)) {
			str += banner2Group.getGroupId() + ";";
		}
		logger.debug("-");
		return str;
	}

	public List<Banner2Group> findBannerGroups(Long bannerId) {
		logger.debug("+ -");
		return banner2GroupDao.listBannerGroups(bannerId);
	}

	public List<Long> findBanner2GroupIds(Long bannerId) {
		logger.debug("+ -");
		return banner2GroupDao.listBanner2GroupIds(bannerId);
	}

	public void updateBanner2Group(Long bannerId, String[] ids)
			throws Exception {
		logger.debug("+");
		if (ids == null) {
			logger.debug("- ids are empty.");
			return;
		}
		List<Long> lids = new ArrayList<Long>();
		for (String s : ids) {
			if (s == null || s.trim().length() == 0) {
				break;
			}
			try {
				lids.add(Long.parseLong(s));
			} catch (Exception e) {
				logger.debug("- error parsin string id to long");
				break;
			}
		}
		List<Long> currBanner2GroupIds = findBanner2GroupIds(bannerId);
		for (Long currId : currBanner2GroupIds) {
			deleteBanner2Group(currId);
		}
		for (Long currId : lids) {
			insertBanner2Group(new Banner2Group(bannerId, currId));
		}
		logger.debug("-");
	}

	public Map<Long, Long> getAllBannerClicks() {
		Map<Long, Long> clicksStat = new LinkedHashMap<Long, Long>();
		List<Banner> banners = bannerDao.listAllBanners();
		for (Banner banner : banners) {
			clicksStat.put(banner.getId(), hibernateBannerDao
					.countTotalClicksCount(banner.getId()));
		}
		return clicksStat;
	}

	public Map<Long, Long> getAllBannerViews() {
		Map<Long, Long> clicksStat = new LinkedHashMap<Long, Long>();
		List<Banner> banners = bannerDao.listAllBanners();
		for (Banner banner : banners) {
			clicksStat.put(banner.getId(), hibernateBannerDao
					.countTotalViewsCount(banner.getId()));
		}
		return clicksStat;
	}

	public Map<Long, Long> getBannerClicksByCategoryId(Long categoryId) {
		Map<Long, Long> clicksStat = new LinkedHashMap<Long, Long>();
		List<Banner> banners = bannerDao.listBannersByParentId(categoryId);
		for (Banner banner : banners) {
			clicksStat.put(banner.getId(), hibernateBannerDao
					.countTotalClicksCount(banner.getId()));
		}
		return clicksStat;
	}

	public Long getBannerClicks(Long bannerId) {
		logger.debug("+ -");
		return hibernateBannerDao.countTotalClicksCount(bannerId);
	}

	public Long getBannerViews(Long bannerId) {
		logger.debug("+ -");
		return hibernateBannerDao.countTotalViewsCount(bannerId);
	}

	public Map<Long, Long> getBannerViewsByCategoryId(Long categoryId) {
		Map<Long, Long> viewsStat = new LinkedHashMap<Long, Long>();
		List<Banner> banners = bannerDao.listBannersByParentId(categoryId);
		for (Banner banner : banners) {
			viewsStat.put(banner.getId(), hibernateBannerDao
					.countTotalViewsCount(banner.getId()));
		}
		return viewsStat;
	}

	public List<BannerStatistics> findStatisticsByDates(Long bannerId,
			Timestamp d1, Timestamp d2) {
		return bannerStatisticsDao.listByDates(bannerId, d1, d2);
	}

	public Long getClicksByDate(Long bannerId, String t1, String t2)
			throws Exception {
		Timestamp times1 = new Timestamp(Env.parseRoundDate(t1).getTime());
		Timestamp times2 = new Timestamp(Env.parseRoundDate(t2).getTime());
		List<BannerStatistics> stat = findStatisticsByDates(bannerId, times1,
				times2);
		Long buf = 0L;
		for (BannerStatistics s : stat) {
			buf += s.getClicks();
		}
		return buf;
	}

	public Long getViewsByDate(Long bannerId, String t1, String t2)
			throws Exception {
		Timestamp times1 = new Timestamp(Env.parseRoundDate(t1).getTime());
		Timestamp times2 = new Timestamp(Env.parseRoundDate(t2).getTime());
		List<BannerStatistics> stat = findStatisticsByDates(bannerId, times1,
				times2);
		Long buf = 0L;
		for (BannerStatistics s : stat) {
			buf += s.getViews();
		}
		return buf;
	}

	public void deleteBanner2Group(Long banner2GroupId) {
		logger.debug("+");
		Banner2Group banner2Group = banner2GroupDao.read(banner2GroupId);
		banner2GroupDao.delete(banner2Group);
		logger.debug("-");
	}

	public void insertBanner2Group(Banner2Group banner2Group) {
		logger.debug("+");
		banner2GroupDao.create(banner2Group);
		logger.debug("-");
	}

	public void setBannerDao(BannerDao bannerDao) {
		this.bannerDao = bannerDao;
	}

	public void setBannerTypeService(BannerTypeService bannerTypeService) {
		this.bannerTypeService = bannerTypeService;
	}

	public void setBanner2PageDao(Banner2PageDao banner2PageDao) {
		this.banner2PageDao = banner2PageDao;
	}

	public void setBanner2GroupDao(Banner2GroupDao banner2GroupDao) {
		this.banner2GroupDao = banner2GroupDao;
	}

	public void setHibernateBannerDao(HibernateBannerDao hibernateBannerDao) {
		this.hibernateBannerDao = hibernateBannerDao;
	}

	public void setBannerStatisticsDao(BannerStatisticsDao bannerStatisticsDao) {
		this.bannerStatisticsDao = bannerStatisticsDao;
	}
}