package com.negeso.framework.menu;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import com.negeso.framework.Env;
import com.negeso.framework.cache.Cachable;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.site.XmlUtils;

public class SiteMapCache implements Cachable{
	
	private static final String SITEMAP_CACHE_FOLDER ="/WEB-INF/generated/sitemap/";
	private static final String SITEMAP_XML ="sitemap%s.xml";
	
	static Long getCacheLifeTime() {
		return Long.parseLong(Env.getProperty("SITEMAP_MAP_EXPIRATION_PERIOD", "86400000"));
	}
	
	static boolean isCacheEnabled() {
		return Boolean.valueOf(Env.getProperty("TO_CACHE_SITEMAP_MAP"));
	}

	@Override
	public void resetCache() {
		try {
			File cacheFolder = getCacheFolder();
			if (cacheFolder.exists() && cacheFolder.isDirectory()) {
				FileUtils.cleanDirectory(cacheFolder);
			}
		} catch (IOException e) {
			throw new CriticalException("Unable to clear cache", e);
		}
	}
	
	static File getCacheFolder() throws IOException {
		return getCacheFolder(false);
	}
	
	static File getCacheFolder(boolean isForceMkdir) throws IOException {
		File cacheFolder = new File(Env.getRealPath(SITEMAP_CACHE_FOLDER));
		if (isForceMkdir) {
			FileUtils.forceMkdir(cacheFolder);			
		}
		return cacheFolder;
	}
	
	static boolean isCachedFileValid(File file) {
		return file.exists() 
				&& file.lastModified() + getCacheLifeTime() > System.currentTimeMillis();
	}

	static void generateCache(File xmlFile, Document buildSitemap) throws IOException {
		String sitemap = XmlUtils.getStringFromDocument(buildSitemap);
		PrintWriter writer = null;
		try {
			xmlFile.createNewFile();
			writer = new PrintWriter(xmlFile, "UTF-8");
			writer.write(sitemap);
		}  finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	static String getLanguageSpecificFileName(Language language) {
		return String.format(SITEMAP_XML, language != null ? "_" + language.getCode() : StringUtils.EMPTY);
	}

}
