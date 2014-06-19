package com.inv.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {
	public static void main(String[] args)
	{
		String a = "\\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) ([\\d|\\-]+) \"([^\"]*)\" \"([^\"]+)\" \"([^\"]+)\" (\\d+) (.{1}) (\\d+) (\\d+) (\\d+) (\\d+)";
		String src = "[23/May/2014:03:21:38 +0000] \"GET /vcb_lib/smart_pixel2/google_license.php?tags=&key= HTTP/1.1\" 200 - \"http://www.investopedia.com/terms/m/m0.asp\" \"Mozilla/5.0 (Windows NT 5.1; rv:26.0) Gecko/20100101 Firefox/26.0\" \"-\" 6455 + 997 150 9072 0";
		Pattern pattern = Pattern.compile(a);
		Matcher matcher = pattern.matcher(src);
		System.out.println(matcher.find());
	}
}
