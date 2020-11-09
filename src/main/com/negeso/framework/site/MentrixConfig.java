package com.negeso.framework.site;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

public class MentrixConfig extends AbstractDbObject {
	
	private static Logger logger = Logger.getLogger( MentrixConfig.class );
	
	private Long id;
	
	private String contactInfo;
	private String contentLinkColor;
	private String contentTextColor;
	private String copyright;
	private String currentBgImage;
	private String currentTextColor;
	private String defaultBgImage;
	private String defaultTextAlign;
	private String defaultTextColor;
	private String footerleftBgColor;
	private String footerleftName;
	private String footerleftScreenshot;	
	private String footerleftTextColor;
	private String header;
	private String headerLayout;
	private String headerName;
	private String level2Border;
	private String logo;
	private String menuBgColor;
	private String menuCurrentBgColor;
	private String menuLiHeight;
	private String menuLiMargin;
	private String menuLiPadding;
	private String menuLiWidth;
	private String menuName;
	private String menuPosition;
	private String menuScreenshot;
	private String menuUlMargin;
	private String menuUlWidth;
	private String pageBgColor;
	private String pageBgImage;
	private String pageBgName;
	private String pageBgScreenshot;
	private String pageFontBackground;
	private String pageFontFace;
	private String pageFontSizeBig;
	private String pathPrefix;
	private String sloganAlignment;
	private String sloganColor;
	private String sloganFontSize;
	private String sloganHeight;
	private String sloganMargins;
	private String sloganText;
	private String sloganWidth;
	private String categoryName;
	private String layoutName;
	private String languageCode;

	private long cacheTime = System.currentTimeMillis();
	
	private static final String findByIdSql = "SELECT * FROM mentrix_config WHERE id = ?";
	
	public void setCacheTime(long l) {
		this.cacheTime  = l;
	}
	
	public long getCacheTime() {
		return cacheTime;
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFindByIdSql() {
		return findByIdSql;
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
	public Long load(ResultSet rs) throws CriticalException {
		logger.debug( "+" );
        try{
            this.id = DBHelper.makeLong(rs.getLong("id"));            
            this.contactInfo = rs.getString("contact_info");
            this.contentLinkColor = rs.getString("content_link_color");
            this.contentTextColor = rs.getString("content_text_color");
            this.copyright = rs.getString("copyright");
            this.currentBgImage = rs.getString("current_bg_image");
            this.currentTextColor = rs.getString("current_text_color");
            this.defaultBgImage = rs.getString("default_bg_image");
            this.defaultTextAlign = rs.getString("default_text_align");
            this.defaultTextColor = rs.getString("default_text_color");
            this.footerleftBgColor = rs.getString("footerleft_bg_color");            
            this.footerleftName = rs.getString("footerleft_name");
            this.footerleftScreenshot = rs.getString("footerleft_screenshot");
            this.footerleftTextColor = rs.getString("footerleft_text_color");
            this.header = rs.getString("header");
            this.headerLayout = rs.getString("header_layout");
            this.headerName = rs.getString("header_name");
            this.level2Border = rs.getString("level2_border");
            this.logo = rs.getString("logo");
            this.menuBgColor = rs.getString("menu_bg_color");
            this.menuCurrentBgColor = rs.getString("menu_current_bg_color");            
            this.menuLiHeight = rs.getString("menu_li_height");
            this.menuLiMargin = rs.getString("menu_li_margin");
            this.menuLiPadding = rs.getString("menu_li_padding");
            this.menuLiWidth = rs.getString("menu_li_width");
            this.menuName = rs.getString("menu_name");
            this.menuPosition = rs.getString("menu_position");
            this.menuScreenshot = rs.getString("menu_screenshot");
            this.menuUlMargin = rs.getString("menu_ul_margin");
            this.menuUlWidth = rs.getString("menu_ul_width");
            this.pageBgColor = rs.getString("page_bg_color");
            this.pageBgImage = rs.getString("page_bg_image");
            this.pageBgName = rs.getString("page_bg_name");
            this.pageBgScreenshot = rs.getString("page_bg_screenshot");
            this.pageFontBackground = rs.getString("page_font_background");
            this.pageFontFace = rs.getString("page_font_face");
            this.pageFontSizeBig = rs.getString("page_font_size_big");
            this.pathPrefix = rs.getString("path_prefix");
            this.sloganAlignment = rs.getString("slogan_alignment");
            this.sloganColor = rs.getString("slogan_color");
            this.sloganFontSize = rs.getString("slogan_font_size");
            this.sloganHeight = rs.getString("slogan_height");
            this.sloganMargins = rs.getString("slogan_margins");
            this.sloganText = rs.getString("slogan_text");
            this.sloganWidth = rs.getString("slogan_width");
            this.categoryName = rs.getString("category_name");
            this.layoutName = rs.getString("layout_name");
            this.languageCode = rs.getString("language_code");
      	
            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
	}

	@Override
	public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Find MentrixConfig By id
     * 
     * @param siteId
     * @return
     * @throws CriticalException 
     */
    public static MentrixConfig findById(Connection con, Long siteId) {
        return (MentrixConfig) DBHelper.findDbObjectById(con, new MentrixConfig(), siteId);
    }

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getContentLinkColor() {
		return contentLinkColor;
	}

	public void setContentLinkColor(String contentLinkColor) {
		this.contentLinkColor = contentLinkColor;
	}

	public String getContentTextColor() {
		return contentTextColor;
	}

	public void setContentTextColor(String contentTextColor) {
		this.contentTextColor = contentTextColor;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getCurrentBgImage() {
		return currentBgImage;
	}

	public void setCurrentBgImage(String currentBgImage) {
		this.currentBgImage = currentBgImage;
	}

	public String getCurrentTextColor() {
		return currentTextColor;
	}

	public void setCurrentTextColor(String currentTextColor) {
		this.currentTextColor = currentTextColor;
	}

	public String getDefaultBgImage() {
		return defaultBgImage;
	}

	public void setDefaultBgImage(String defaultBgImage) {
		this.defaultBgImage = defaultBgImage;
	}

	public String getDefaultTextAlign() {
		return defaultTextAlign;
	}

	public void setDefaultTextAlign(String defaultTextAlign) {
		this.defaultTextAlign = defaultTextAlign;
	}

	public String getDefaultTextColor() {
		return defaultTextColor;
	}

	public void setDefaultTextColor(String defaultTextColor) {
		this.defaultTextColor = defaultTextColor;
	}

	public String getFooterleftBgColor() {
		return footerleftBgColor;
	}

	public void setFooterleftBgColor(String footerleftBgColor) {
		this.footerleftBgColor = footerleftBgColor;
	}

	public String getFooterleftName() {
		return footerleftName;
	}

	public void setFooterleftName(String footerleftName) {
		this.footerleftName = footerleftName;
	}

	public String getFooterleftScreenshot() {
		return footerleftScreenshot;
	}

	public void setFooterleftScreenshot(String footerleftScreenshot) {
		this.footerleftScreenshot = footerleftScreenshot;
	}

	public String getFooterleftTextColor() {
		return footerleftTextColor;
	}

	public void setFooterleftTextColor(String footerleftTextColor) {
		this.footerleftTextColor = footerleftTextColor;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeaderLayout() {
		return headerLayout;
	}

	public void setHeaderLayout(String headerLayout) {
		this.headerLayout = headerLayout;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public String getLevel2Border() {
		return level2Border;
	}

	public void setLevel2Border(String level2Border) {
		this.level2Border = level2Border;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMenuBgColor() {
		return menuBgColor;
	}

	public void setMenuBgColor(String menuBgColor) {
		this.menuBgColor = menuBgColor;
	}

	public String getMenuCurrentBgColor() {
		return menuCurrentBgColor;
	}

	public void setMenuCurrentBgColor(String menuCurrentBgColor) {
		this.menuCurrentBgColor = menuCurrentBgColor;
	}

	public String getMenuLiHeight() {
		return menuLiHeight;
	}

	public void setMenuLiHeight(String menuLiHeight) {
		this.menuLiHeight = menuLiHeight;
	}

	public String getMenuLiMargin() {
		return menuLiMargin;
	}

	public void setMenuLiMargin(String menuLiMargin) {
		this.menuLiMargin = menuLiMargin;
	}

	public String getMenuLiPadding() {
		return menuLiPadding;
	}

	public void setMenuLiPadding(String menuLiPadding) {
		this.menuLiPadding = menuLiPadding;
	}

	public String getMenuLiWidth() {
		return menuLiWidth;
	}

	public void setMenuLiWidth(String menuLiWidth) {
		this.menuLiWidth = menuLiWidth;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuPosition() {
		return menuPosition;
	}

	public void setMenuPosition(String menuPosition) {
		this.menuPosition = menuPosition;
	}

	public String getMenuScreenshot() {
		return menuScreenshot;
	}

	public void setMenuScreenshot(String menuScreenshot) {
		this.menuScreenshot = menuScreenshot;
	}

	public String getMenuUlMargin() {
		return menuUlMargin;
	}

	public void setMenuUlMargin(String menuUlMargin) {
		this.menuUlMargin = menuUlMargin;
	}

	public String getMenuUlWidth() {
		return menuUlWidth;
	}

	public void setMenuUlWidth(String menuUlWidth) {
		this.menuUlWidth = menuUlWidth;
	}

	public String getPageBgColor() {
		return pageBgColor;
	}

	public void setPageBgColor(String pageBgColor) {
		this.pageBgColor = pageBgColor;
	}

	public String getPageBgImage() {
		return pageBgImage;
	}

	public void setPageBgImage(String pageBgImage) {
		this.pageBgImage = pageBgImage;
	}

	public String getPageBgName() {
		return pageBgName;
	}

	public void setPageBgName(String pageBgName) {
		this.pageBgName = pageBgName;
	}

	public String getPageBgScreenshot() {
		return pageBgScreenshot;
	}

	public void setPageBgScreenshot(String pageBgScreenshot) {
		this.pageBgScreenshot = pageBgScreenshot;
	}

	public String getPageFontBackground() {
		return pageFontBackground;
	}

	public void setPageFontBackground(String pageFontBackground) {
		this.pageFontBackground = pageFontBackground;
	}

	public String getPageFontFace() {
		return pageFontFace;
	}

	public void setPageFontFace(String pageFontFace) {
		this.pageFontFace = pageFontFace;
	}

	public String getPageFontSizeBig() {
		return pageFontSizeBig;
	}

	public void setPageFontSizeBig(String pageFontSizeBig) {
		this.pageFontSizeBig = pageFontSizeBig;
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public String getSloganAlignment() {
		return sloganAlignment;
	}

	public void setSloganAlignment(String sloganAlignment) {
		this.sloganAlignment = sloganAlignment;
	}

	public String getSloganColor() {
		return sloganColor;
	}

	public void setSloganColor(String sloganColor) {
		this.sloganColor = sloganColor;
	}

	public String getSloganFontSize() {
		return sloganFontSize;
	}

	public void setSloganFontSize(String sloganFontSize) {
		this.sloganFontSize = sloganFontSize;
	}

	public String getSloganHeight() {
		return sloganHeight;
	}

	public void setSloganHeight(String sloganHeight) {
		this.sloganHeight = sloganHeight;
	}

	public String getSloganMargins() {
		return sloganMargins;
	}

	public void setSloganMargins(String sloganMargins) {
		this.sloganMargins = sloganMargins;
	}

	public String getSloganText() {
		return sloganText;
	}

	public void setSloganText(String sloganText) {
		this.sloganText = sloganText;
	}

	public String getSloganWidth() {
		return sloganWidth;
	}

	public void setSloganWidth(String sloganWidth) {
		this.sloganWidth = sloganWidth;
	}

}
