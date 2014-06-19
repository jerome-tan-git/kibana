package com.inv.log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogObj {
	private String IP;
	private String timeStamp;
	private String URL;
	private String referer;
	private String responseCode;
	private String responseTime;
	private String useagent;
	private String indexName;
	private String requestMethod;
	
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getUseagent() {
		return useagent;
	}
	public void setUseagent(String useagent) {
		this.useagent = useagent;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	
	
	public static String getMatcher(String regex, String source) {  
        String result = "";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(source);  
        while (matcher.find()) {  
            result = matcher.group(0);  
        }  
        return result;  
    } 
	public static void main(String[] args)
	{
		String test = "210.186.140.229, 210.186.140.221 192.168.137.182 - - [13/Jun/2014:06:49:05 +0000] \"GET http://www.investopedia.com/vcb/assets_v2/js/taggroup_seg_mapping.js HTTP/1.1\" 304 2698 \"http://www.investopedia.com/terms/d/diversifiedcompany.asp\" \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.76 Safari/537.36\" hit 71.048737 0";

	}
}
