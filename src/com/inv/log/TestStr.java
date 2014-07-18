package com.inv.log;

import com.inv.parser.InvVarnishLogParser;

public class TestStr {
	public static void main(String[] args) {
		//String a = "98.148.240.149 192.168.137.183 - - [16/Jun/2014:04:11:45 +0000] \"POST http://www.investopedia.com/vcb_api/calculate/millionairecal_initial_porfolio/ HTTP/1.1\" 200 485 \"http://www.investopedia.com/calculator/millionairecal.aspx\" \"Mozilla/5.0 (iPhone; CPU iPhone OS 7_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D167 [FBAN/FBIOS;FBAV/10.0.0.26.21;FBBV/2441354;FBDV/iPhone6,1;FBMD/iPhone;FBSN/iPhone OS;FBSV/7.1;FBSS/2; FBCR/AT&T;FBID/phone;FBLC/en_US\" pass 76926.946640 0";
//		String a =  "[16/Jun/2014:06:37:57 +0000] \"GET http://www.investopedia.com/accounts/signupnewsletter/ HTTP/1.1\" 200 14719 \"\" \"Mozilla/4.0 (compatible; MSIE 999.1; Unknown)\" pass 306226.015091 0";
//		String logEntryPattern = "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]+)\" (\\w+) ([0-9\\.]+) ([0-9]+)";
//		System.out.println(new InvVarnishLogParser().getMatcher(logEntryPattern, a)[4]);
		System.out.println(System.currentTimeMillis());
	}
}
