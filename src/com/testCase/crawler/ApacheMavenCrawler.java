package com.testCase.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ApacheMavenCrawler extends Crawler{

	Properties prop = new Properties();
	public ApacheMavenCrawler() throws Exception{
		super();
		InputStream ins = getClass().getClassLoader().getResourceAsStream(CommonEnum.CONFIG_PROPERTIES.getEnumValue());
		if (ins != null) {
			prop.load(ins);
		}
		ins.close();
	}
	
	public void processCrawler(){
		try{
			String baseURL = prop.getProperty("BaseURL");
			String yearFilter = prop.getProperty("MailYear");
			String downloadsDirectory = prop.getProperty("MailDownLoadDir");
			
			String yearRegex = "(http://)?[a-zA-Z-._/]+"+yearFilter +"[0-9]{2}[.a-zA-Z/]+thread";
			Document docThreadHTML = Jsoup.connect(baseURL).get();
			Elements baseElements = docThreadHTML.select("a");
			for(Element indvLink: baseElements){
				List<String> links = new ArrayList<String>();
				String threadURL = indvLink.attr("abs:href");
				if(yearThreadMatch(threadURL, yearRegex)){
					//collecting links for each thread
					links.addAll(getLinksPerThread(threadURL, yearFilter));
					System.out.println("No of emails for year_month "+ threadURL.substring(53,59)+" :: "+links.size());
					//downloading collected links 
					int emailLinkNumber =1;
					String directoryPath = downloadsDirectory+"_"+threadURL.substring(53,59);
					for(String link:links){
						File directory = new File(directoryPath);
						if (!directory.exists())
							directory.mkdir();
						File file = new File(directory, "EmailFile_" + (emailLinkNumber)+ ".txt");
						writeToFileFromURL(link, file);
						emailLinkNumber++;
					}
				}
			}	
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	public boolean yearThreadMatch(String tempURL,String yearRegex){
		Pattern yrPattern = Pattern.compile(yearRegex);
		Matcher yrMatcher = yrPattern.matcher(tempURL);
		if(yrMatcher.find()){
			return true;
		}
		return false;
	}
	
	public List<String> getLinksPerThread(String indvThreadURL,String yearFilter){
		List<String> indvThreadLinks = new ArrayList<String>();
		Set<String> internalThreadURLS = new HashSet<String>();
		try{
			String yearRegexExt = "(http://)?[a-zA-Z-._/]+"+yearFilter +"[0-9]{2}[.a-zA-Z/]+thread[?][1-9]+";
			String mailRegex = "(http://)?[a-zA-Z-._/]+" + yearFilter +"[0-9]{2}[.a-zA-Z/]+%[a-zA-Z0-9-+=$._@]+%";
			Document docHTML = Jsoup.connect(indvThreadURL).get();
			Elements threadElements = docHTML.select("a");
			for(Element indvTLink: threadElements){
				Pattern pattern = Pattern.compile(mailRegex);
				String absoluteUrl = indvTLink.attr("abs:href");
				Matcher matcher = pattern.matcher(absoluteUrl);
				if(matcher.find()){
					indvThreadLinks.add(absoluteUrl);
				}else{
					//to get mail links (../thread?#) from other pages apart from the first page
					//as first page is already collected by thread (../thread)
					if(yearThreadMatch(absoluteUrl, yearRegexExt)){
						internalThreadURLS.add(absoluteUrl);
					}
				}
			}
			
			if(!internalThreadURLS.isEmpty()){
				for(String intTemURL:internalThreadURLS){
					List<String> internalThreadLinks = getLinksPerThread(intTemURL,yearFilter);
					if(internalThreadLinks!=null){
						indvThreadLinks.addAll(internalThreadLinks);
					}
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return indvThreadLinks;
	}
	
	private void writeToFileFromURL(String url, File file) throws IOException {
		URL emailUrl;
		URLConnection conn;
		OutputStream outputStream = new FileOutputStream(file);
		try {
			emailUrl = new URL(url);
			conn = emailUrl.openConnection();
			IOUtils.copy(conn.getInputStream(), outputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
