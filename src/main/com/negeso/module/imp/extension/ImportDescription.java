package com.negeso.module.imp.extension;

public final class ImportDescription {
	
	private String importerId;
	private String importerName;
	private String moduleId;
	
	public ImportDescription(String importerId, String importerName, String moduleId) {
		this.importerId = importerId;
		this.importerName = importerName;
		this.moduleId = moduleId;
	}
	
	public String getImporterId() {
		return importerId;
	}
	
	public String getImporterName() {
		return importerName;
	}
	
	public String getModuleId() {
		return moduleId;
	}
}
