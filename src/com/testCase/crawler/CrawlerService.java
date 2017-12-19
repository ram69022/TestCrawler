package com.testCase.crawler;

import java.io.InputStream;
import java.util.Properties;

public class CrawlerService {
	
	
	Properties prop = new Properties();
	
	public CrawlerService()throws Exception{
		InputStream ins = getClass().getClassLoader().getResourceAsStream(CommonEnum.CONFIG_PROPERTIES.getEnumValue());
		if (ins != null) {
			prop.load(ins);
		}
		ins.close();
	}
	
	public void startCrawling(){
		try{
			
			CrawlerFactory cFactory = new CrawlerFactory();
			Crawler crawler = cFactory.getCrawler(CommonEnum.DOWNLOAD_EMAIL.getEnumValue());
			System.out.println("Crawling process started....");
			crawler.processCrawler();
			System.out.println("Crawling process ended....");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String []args){
		try{
			CrawlerService cs = new CrawlerService();
			cs.startCrawling();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
