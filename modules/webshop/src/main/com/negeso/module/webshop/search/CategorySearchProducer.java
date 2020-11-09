package com.negeso.module.webshop.search;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.search.ProductCategorySearchEntry;
import com.negeso.module.search.SearchEntry;
import com.negeso.module.search.SearchIndexCallBack;
import com.negeso.module.webshop.service.ProductCategoryService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CategorySearchProducer extends AbstractSearchEntryProducer {

    private ProductCategoryService productCategoryService;

    private final static String FIND_ALL_CATEGORIES = "" +
            " SELECT name, title, url.link FROM ws_category c " +
            " LEFT JOIN friendly_url url ON c.name=url.entity_id" +
            " WHERE c.visible = TRUE";

    @Override
    public void indexEntries(SearchIndexCallBack callBack) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(FIND_ALL_CATEGORIES);
            while (rs.next()) {
                String title = rs.getString("title");
                String name = rs.getString("name");
                String uri = rs.getString("link");

                String langCode = "nl";
                String url = uri == null ? "/webshop_nl.html?category=" + name : "/webshop_nl/" + uri + ".html";
                String imageUri = productCategoryService == null ? null : productCategoryService.resolveImageUri(name);

//                ProductCategorySearchEntry entry = new ProductCategorySearchEntry(name, url, langCode, title, title, imageUri, null, null, null);
				SearchEntry entry = new SearchEntry(name, url, langCode, title, title, imageUri, null, null, null);
                callBack.index(entry);
            }
        } catch (Exception ex) {
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(rs, stmt, conn);
            DBHelper.close(stmt);
        }
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
}
