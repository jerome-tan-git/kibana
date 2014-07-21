package com.inv.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.inv.log.FetchLog;

public class MySQL_slow implements ILogParser{
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
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
		
		if (result != null && result.length>=10) {
		
//			System.out.println("result 0 : " + result[0]);
			Date date = new Date();
			try {
				date = format.parse(result[0]);
			} catch (Exception e) {
				e.printStackTrace();
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
			returnObj.put("query_time", result[2]);
			returnObj.put("lock_time", result[3]);
			returnObj.put("rows_sent", result[4]);
			returnObj.put("rows_examined", result[5]);
			returnObj.put("db", result[6]);
			returnObj.put("sqltext", result[7]);
			returnObj.put("sqltype", result[8]);
			returnObj.put("website", result[9]);
			
		} else {
			if (FetchLog.isDebug) {
				
			 System.out.println("Error:" + _log);
			}
			return null;
		}
		// System.out.println(returnObj);
		return returnObj;
	}

}
