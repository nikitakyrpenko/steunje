package com.negeso.module.webshop.search;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.search.SearchEntry;
import com.negeso.module.search.SearchIndexCallBack;
import com.negeso.module.webshop.service.ProductService;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProductSearchProducer extends AbstractSearchEntryProducer {
    private final static Logger logger = Logger.getLogger(ProductSearchProducer.class);
    private ProductService productService;

    private final static String FIND_ALL_PRODUCTS = "" +
            " SELECT order_code, product_number, title, ean, brand, url.link, keywords, multiple_of, category_name FROM ws_products p" +
            " LEFT JOIN friendly_url url ON p.order_code=url.entity_id" +
            " WHERE p.visible = TRUE " +
            " ORDER BY p.order_number ASC";

    @Override
    public void indexEntries(SearchIndexCallBack callBack) {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(FIND_ALL_PRODUCTS);
            while (rs.next()) {
                String title = rs.getString("title");
                String ean = rs.getString("ean");
                String orderCode = rs.getString("order_code");
                String productNumber = rs.getString("product_number");
                String brand = rs.getString("brand");
                String uri = rs.getString("link");

                String keywords = "";
                if (rs.getString("keywords") != null) {
                    keywords = rs.getString("keywords").replace(",", " ");
                }

                String langCode = "nl";
                String url = uri == null ? "/webshop_nl.html?product=" + orderCode : "/webshop_nl/" + uri + ".html";
                String imageUri = productService != null ? productService.resolveImageUri(orderCode) : null;

                Integer multipleOf = rs.getInt("multiple_of");
                String categoryName = rs.getString("category_name");

                SearchEntry entry = new SearchEntry(categoryName, url, langCode, title, super.separateBySpaces(orderCode, "EAN: " + ean, title, brand, productNumber, "keywords: " + keywords),
                        imageUri, multipleOf, productNumber, orderCode);

                callBack.index(entry);
            }
        } catch (Exception ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(rs, stmt, conn);
            DBHelper.close(stmt);
        }
    }

    //it won't be autowired automatically
    //see com.negeso.module.webshop.service.SearchService
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
