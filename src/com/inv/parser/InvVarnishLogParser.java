package com.inv.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inv.log.FetchLog;
import com.inv.log.LogObj;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class InvVarnishLogParser implements ILogParser {
	private SimpleDateFormat format = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss");
	private SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private SimpleDateFormat format2 = new SimpleDateFormat(
			"-yyyy.MM.dd");
	private LookupService cl;

	public InvVarnishLogParser() {
		try {
			cl = new LookupService("./GeoIPCity.dat",
					LookupService.GEOIP_MEMORY_CACHE);
		} catch (Exception e) {

		}
	}

	public String[] getMatcher(String regex, String source) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		String[] result = null;

		while (matcher.find()) {
			result = new String[9];
			result[0] = matcher.group(1);
			result[1] = matcher.group(2);
			result[2] = matcher.group(3);
			result[3] = matcher.group(4);
			result[4] = matcher.group(5);
			result[5] = matcher.group(6);
			result[6] = matcher.group(7);
			result[7] = matcher.group(8);
			result[8] = matcher.group(9);
		}

		return result;
	}

	@Override
	public String getRealIP(String _log) {
		String realip = "";
		try {
			String stra[] = _log.split("\\[");
			String str1 = stra[0];
			String iplist = str1.substring(0, str1.length() - 5).replaceAll(
					",", "");
			String[] ipa = iplist.split("\\s");
			realip = ipa[0];

		} catch (Exception e) {

		}
		return realip;
	}
	private String getServerName(String _log)
	{
		String serverName = null;
		if(_log.indexOf('|')!=-1 && _log.indexOf('|')<30)
		{
			serverName = _log.substring(0, _log.indexOf('|'));
		}
		return serverName;
	}
	private String removeServerName (String _log)
	{
		String result = _log;
		if(_log.indexOf('|')!=-1 && _log.indexOf('|')<30)
		{
			result = _log.substring( _log.indexOf('|')+1);
		}
		return result;
	}
	@Override
	public HashMap<String, Object> getLogObj(String _log, String _prefix) {
		if (FetchLog.isDebug) {
			System.out.println("source:" + _log);
		}
		Location lo = null;
		HashMap returnObj = null;
		String serverName = this.getServerName(_log);
		if(serverName != null)
		{
			_log = this.removeServerName(_log);
		}
		if (_log.indexOf('[') == -1) {

			System.out.println("bad:" + _log);
			return null;
		}
		String str2 = _log.substring(_log.indexOf('['));
		// String str2 = "[" + stra[1];
		String logEntryPattern = "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) ([\\d|\\-]+) \"([^\"]*)\" \"([^\"]+)\" (\\w+) ([0-9\\.]+) ([0-9]+)";
		String[] result = this.getMatcher(logEntryPattern, str2);
		if (result != null) {
			
			//DecimalFormat df = new DecimalFormat("#.###");

			double performance = -1;
			try {
				performance = Double.parseDouble(result[7]);
			} catch (Exception e) {
			}
			Date date = new Date();
			try {
				date = format.parse(result[0]);
			} catch (Exception e) {
			}
			String indexName = _prefix + format2.format(date);
			
			String timestamp = format1.format(date);
			String realIP = this.getRealIP(_log);
			lo = this.getLocation(realIP);
			returnObj = new HashMap<String, Object>();
			returnObj.put("message", _log);
			returnObj.put("@indexName", indexName);
			returnObj.put("@timestamp", timestamp);
			returnObj.put("@version", 1);
			returnObj.put("referer", result[4]);
			returnObj.put("responseCode", result[2]);
			try
			{
				returnObj.put("responseTime", (int)(performance / 1000));
			}
			catch(Exception e)
			{
				returnObj.put("responseTime", 9999);
			}
			returnObj.put("useagent", result[5]);
			returnObj.put("IP", realIP);
			if(serverName != null)
			{
				returnObj.put("serverName", serverName);
			}
			returnObj.put("method", result[1].split("\\s")[0]);
			returnObj.put("URL", result[1].split("\\s")[1]);
			returnObj.put("varnishhit", result[6]);
			returnObj.put("country_code", (lo == null) ? ("")
					: (lo.countryCode));
			returnObj.put("country_name", (lo == null) ? ("")
					: (lo.countryName));
			returnObj.put("city", (lo == null) ? ("") : (lo.city));
			returnObj.put("postalCode", (lo == null) ? ("") : (lo.postalCode));
			returnObj.put("loation", (lo == null) ? (new String[] { "", "" })
					: (new String[] { "" + lo.longitude, "" + lo.latitude }));
			returnObj.put("newcol", "newcol");
		} else {
			return null;
		}
		// System.out.println(returnObj);
		return returnObj;
	}

	public Location getLocation(String _ip) {
		Location result = null;
		if (this.cl != null) {
			result = cl.getLocation(_ip);
		}
		return result;
	}

	public static void main(String[] args) {
		String a = "66.249.69.58 192.168.137.183 - - [17/Jun/2014:04:05:14 +0000] \"GET http://www.investopedia.com/articles/retirement/?page=7&d_pv= HTTP/1.1\" 200 23736 \"-\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)\" miss 6522028.923035 6";
		InvVarnishLogParser aaa = new InvVarnishLogParser();
		System.out.println(aaa.getLogObj(a ,"test"));
	}
}
