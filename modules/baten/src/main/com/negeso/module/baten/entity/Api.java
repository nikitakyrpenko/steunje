package com.negeso.module.baten.entity;

public class Api {
	private String url;
	private String generatedUrl;
	private String consumes;
	private String produces;
	private String[] expectedKeys;
	private String description;

	public String getUrl() {
		return url;
	}

	public Api url(String url) {
		this.url = url;
		return this;
	}

	public String getGeneratedUrl() {
		return generatedUrl;
	}

	public Api generatedUrl(String generatedUrl) {
		this.generatedUrl = generatedUrl + this.url;
		return this;
	}

	public String getConsumes() {
		return consumes;
	}

	public Api consumes(String consumes) {
		this.consumes = consumes;
		return this;
	}

	public String getProduces() {
		return produces;
	}

	public Api produces(String produces) {
		this.produces = produces;
		return this;
	}

	public String[] getExpectedKeys() {
		return expectedKeys;
	}

	public Api expectedKeys(String[] expectedKeys) {
		this.expectedKeys = expectedKeys;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Api description(String description) {
		this.description = description;
		return this;
	}
}
