package com.negeso.module.imp.extension;

import java.util.Map;

public class ImportConfiguration {

	private Importer importer;
	private ImportDescription description;
	
	private Map<String, Object> attributes;
	
	public Importer getImporter() {
		return importer;
	}
	public void setImporter(Importer importer) {
		this.importer = importer;
	}
	public ImportDescription getDescription() {
		return description;
	}
	public void setDescription(ImportDescription description) {
		this.description = description;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
