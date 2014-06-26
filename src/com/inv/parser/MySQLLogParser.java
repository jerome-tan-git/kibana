package com.inv.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.inv.log.FetchLog;
import com.maxmind.geoip.Location;

public class MySQLLogParser implements ILogParser{
	
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MMM-dd HH:mm:ss");
	private SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private SimpleDateFormat format2 = new SimpleDateFormat(
			"-yyyy.MM.dd");
	
	@Override
	public String getRealIP(String _log) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public HashMap<String, Object> getLogObj(String _log, String _prefix) {
		if (FetchLog.isDebug) {
			System.out.println("source:" + _log);
		}
		
		  
		HashMap returnObj = null;
		String[] result = _log.split("\t");

		if (result != null && result.length>=4) {
		

			Date date = new Date();
			try {
				date = format.parse(result[0]);
			} catch (Exception e) {
			}
			String indexName = _prefix + format2.format(date);
			String timestamp = format1.format(date);
			
			returnObj = new HashMap<String, Object>();
			//System.out.println(_log);
			returnObj.put("message", _log);
			returnObj.put("@indexName", indexName);
			returnObj.put("@timestamp", timestamp);
			returnObj.put("@version", 1);
			returnObj.put("user", result[1]);
			returnObj.put("processID", result[2]);
			returnObj.put("sql", result[3]);
		} else {
			// System.out.println("Error:" + _log);
			return null;
		}
		// System.out.println(returnObj);
		return returnObj;
	}

}
