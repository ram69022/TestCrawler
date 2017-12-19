package com.testCase.crawler;

public class CrawlerFactory {

	public Crawler getCrawler(String crawlerType){
		try{
			if(crawlerType == null){
				return null;
			}
			
			if(crawlerType.equalsIgnoreCase(CommonEnum.DOWNLOAD_EMAIL.getEnumValue())){
				return new ApacheMavenCrawler();
			}// add blocks for handling other crawlers
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
