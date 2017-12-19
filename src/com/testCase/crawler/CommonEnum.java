package com.testCase.crawler;

public enum CommonEnum {

	CONFIG_PROPERTIES("CrawlConfig.properties"),
	DOWNLOAD_EMAIL("download_email"),
	
	;
	private String value;
	private CommonEnum(String value){
		this.value = value;
	}
	
	public String getEnumValue(){
		return value;
	}
}
