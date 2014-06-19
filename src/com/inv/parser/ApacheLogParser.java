package com.inv.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inv.log.FetchLog;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class ApacheLogParser implements ILogParser {
	private SimpleDateFormat format = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss");
	private SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private SimpleDateFormat format2 = new SimpleDateFormat(
			"'logstash'-yyyy.MM.dd");
	private LookupService cl;

	public ApacheLogParser() {
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
			result = new String[13];
			result[0] = matcher.group(1);
			result[1] = matcher.group(2);
			result[2] = matcher.group(3);
			result[3] = matcher.group(4);
			result[4] = matcher.group(5);
			result[5] = matcher.group(6);
			result[6] = matcher.group(7);
			result[7] = matcher.group(8);
			result[8] = matcher.group(9);
			result[9] = matcher.group(10);
			result[10] = matcher.group(11);
			result[11] = matcher.group(12);
			result[12] = matcher.group(13);
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
					",", " ");
			String[] ipa = iplist.split("\\s");
			realip = ipa[0];
		} catch (Exception e) {
		}
		return realip;
	}

	@Override
	public HashMap<String, Object> getLogObj(String _log) {
		if (FetchLog.isDebug) {
			System.out.println("source:" + _log);
		}
		Location lo = null;
		_log = _log.substring(_log.indexOf(' ')).trim();
		HashMap returnObj = null;
		if (_log.indexOf('[') == -1)
			return null;
		String str2 = _log.substring(_log.indexOf('['));
		String logEntryPattern = "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) ([\\d|\\-]+) \"([^\"]*)\" \"([^\"]+)\" \"([^\"]+)\" (\\d+) (.{1}) (\\d+) (\\d+) (\\d+) (\\d+)";
		String[] result = this.getMatcher(logEntryPattern, str2);

		if (result != null) {
			DecimalFormat df = new DecimalFormat("#.###");

			double performance = -1;
			try {
				performance = Double.parseDouble(result[11]);
			} catch (Exception e) {
			}
			Date date = new Date();
			try {
				date = format.parse(result[0]);
			} catch (Exception e) {
			}
			String indexName = format2.format(date);
			String timestamp = format1.format(date);
			String realIP = this.getRealIP(_log);
			lo = this.getLocation(realIP);
			returnObj = new HashMap<String, Object>();
			//System.out.println(_log);
			returnObj.put("message", _log);
			returnObj.put("@indexName", indexName);
			returnObj.put("@timestamp", timestamp);
			returnObj.put("@version", 1);
			returnObj.put("referer", result[4]);
			returnObj.put("responseCode", result[2]);
			returnObj.put("responseTime", df.format(performance / 1000000));
			returnObj.put("useagent", result[5]);
			returnObj.put("IP", realIP);
			returnObj.put("method", result[1].split("\\s")[0]);
			returnObj.put("URL", result[1].split("\\s")[1]);
			returnObj.put("varnishhit", result[6]);
			returnObj.put("country_code", (lo == null) ? ("")
					: (lo.countryCode));
			returnObj.put("country_name", (lo == null) ? ("")
					: (lo.countryCode));
			returnObj.put("city", (lo == null) ? ("") : (lo.countryCode));
			returnObj.put("postalCode", (lo == null) ? ("") : (lo.countryCode));
			returnObj.put("loation", (lo == null) ? (new String[] { "", "" })
					: (new String[] { "" + lo.longitude, "" + lo.latitude }));
			returnObj.put("newcol", "newcol");
		} else {
			// System.out.println("Error:" + _log);
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

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"./web101www.investopedia.com.access_log-20140524"));
		String line = null;
		ApacheLogParser x = new ApacheLogParser();
		// String a =
		// "www.investopedia.com 176.9.155.84 192.168.137.182 - - [23/May/2014:03:18:00 +0000] \"GET /vcb_lib/smart_pixel2/google_license.php?tags=Fixed+Income%2CStock+Market+Terminology%2CBuzzword&key= HTTP/1.1\" 200 521 \"http://www.diana-teknologia.com\" \"Xerka WebBot v1.0.0 [GAIAELEC14]\" \"-\" 6444 + 425 673 11576 0";
		// HashMap aaa = x.getLogObj(a);
		// System.out.println(aaa);
		while ((line = br.readLine()) != null) {

			HashMap aaa = x.getLogObj(line);
			if (aaa == null)
				System.out.println(line);
			else
				System.out.println(aaa.get("responseTime") + " | " + line);
			// System.out.println(x.getLogObj(a));
		}

	}
}
