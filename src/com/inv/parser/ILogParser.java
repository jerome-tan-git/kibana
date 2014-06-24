package com.inv.parser;

import java.util.HashMap;

import com.inv.log.LogObj;

public interface ILogParser {
	public String getRealIP(String _log);
	public HashMap<String, Object> getLogObj(String _log, String _prefix);
}
