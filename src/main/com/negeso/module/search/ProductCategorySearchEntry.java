package com.negeso.module.search;

/**
 * Expansion of {@link SearchEntry} class for {@link com.negeso.module.webshop.entity.ProductCategory} object.
 * User for indexing entity.
 *
 * @author Alexander Podolyak
 */
public class ProductCategorySearchEntry extends SearchEntry {

    private String categoryName;

    public ProductCategorySearchEntry(String categoryName, String uri, String langCode, String title, String contents, String imagePreviewUri, Integer multipleOf, String productNumber, String orderCode) {
        super(uri, langCode, title, contents, imagePreviewUri, multipleOf, productNumber, orderCode);
        this.categoryName = categoryName;
    }


    /**
     * @return category name.
     */
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ProductCategorySearchEntry{" +
                "categoryName='" + categoryName + '\'' +
                super.toString() +
                '}';
    }
}
