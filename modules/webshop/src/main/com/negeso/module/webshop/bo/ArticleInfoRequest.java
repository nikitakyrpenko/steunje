package com.negeso.module.webshop.bo;

public class ArticleInfoRequest {
	private String barcode;
	private Integer count;
	private String error;
	private ArticleInfo info;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public ArticleInfo getInfo() {
		return info;
	}

	public void setInfo(ArticleInfo info) {
		this.info = info;
	}
}
