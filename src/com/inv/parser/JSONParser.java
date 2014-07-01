package com.inv.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inv.log.FetchLog;

public class JSONParser implements ILogParser {
	private SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private SimpleDateFormat format2 = new SimpleDateFormat("-yyyy.MM.dd");

	@Override
	public String getRealIP(String _log) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Object> getLogObj(String _log, String _prefix) {

		JSONObject obj = null;
		HashMap<String, Object> result = null;
		try {

			obj = JSON.parseObject(_log);
			result = new HashMap<String, Object>();
			Set<String> keys = obj.keySet();
			for (String key : keys) {
				result.put(key, obj.get(key));
			}

			if (!result.containsKey("@timestamp")) {
				Date date = new Date();
				String timestamp = format1.format(date);
				result.put("@timestamp", timestamp);
			}
			String timeStr = result.get("@timestamp").toString();
			String indexName = _prefix + format2.format(format1.parse(timeStr));
			result.put("@indexName", indexName);
			result.put("source", _log);
			result.put("@version", "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (FetchLog.isDebug) {
			System.out.println(result);
		}
		return result;
	}

	public static void main(String args[]) {
		JSONParser jp = new JSONParser();
		jp.getLogObj(
				" {\"barAge\":1118357669,\"barDate\":1322062233062,\"barName\":\"sss_0.9961642\"} ",
				"a");
	}

}
