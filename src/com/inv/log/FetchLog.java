package com.inv.log;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import redis.clients.jedis.Jedis;

import com.inv.parser.ILogParser;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class FetchLog {
	private static SimpleDateFormat format = new SimpleDateFormat(
			"dd/MMM/yyyy:HH:mm:ss");
	private static SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat format2 = new SimpleDateFormat(
			"-yyyy.MM.dd");
	public static boolean isDebug = false;

	private static HashMap<String, ILogParser> parserMap = new HashMap<String, ILogParser>();
	private static HashMap<String, String> indexMap = new HashMap<String, String>();
	public static void setLogParser(HashMap<String, ILogParser> _parserMap) {
		FetchLog.parserMap = _parserMap;
	}

	public static void doFetch(int _duration, List<ServerObj> _server,
			String _redisIP, String[] _queueNames) throws IOException {

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		TimeZone tztz = TimeZone.getTimeZone("UTC");
		java.util.Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -8);

		LookupService cl = null;
		try {
			cl = new LookupService("./GeoIPCity.dat",
					LookupService.GEOIP_MEMORY_CACHE);
		} catch (Exception e) {

		}

		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch_group1").build();
		TransportClient transportClient = new TransportClient(settings);
		Client client = null;

		for (ServerObj s : _server) {
			client = transportClient
					.addTransportAddress(new InetSocketTransportAddress(s
							.getServerIP(), s.getServerPort()));
		}

		Jedis jedis1 = new Jedis(_redisIP);
		while (true) {
			List<String> values = null;
			try {
				values = jedis1.blpop(1, _queueNames);
				if (FetchLog.isDebug) {
					System.out.println("Get redis:" + values);
				}
			} catch (Exception e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			}
			if (values == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			for (int i = 0; i < values.size(); i = i + 2) {
				String source = values.get(i);
				String value = values.get(i + 1);
				if (value.indexOf('[') == -1) {
					if (FetchLog.isDebug) {
						System.out.println(values);
					}
				}
				ILogParser logParser = FetchLog.parserMap.get(source+".parser");
				String type = "production";
				String status = "ok";
				Location lo = null;
				String realIP = "";
				String indexPrefix = indexMap.get(source + ".index");

				if(indexPrefix == null)
				{
					indexPrefix = "logstash";
				}
				else if(indexPrefix.trim().equals(""))
				{
					indexPrefix = "logstash";
				}
				HashMap<String, Object> result = null;
				if (logParser != null) {
					// realIP = logParser.getRealIP(value);
					result = logParser.getLogObj(value, indexPrefix);
					
					if (result == null) {
						status = "not_matched";
					}
				} else {
					status = "no_parser";
				}
				if (FetchLog.isDebug) {
					System.out.println("Parser:" + logParser);
				}
				
				
				
				// System.out.println(realIP);
				if (result == null) {
					Date date = new Date();
					String indexName = indexPrefix + format2.format(date);
					String timeStamp = format1.format(date);

					IndexResponse response = client
							.prepareIndex(indexName, type,
									UUID.randomUUID().toString())
							.setSource(
									jsonBuilder().startObject()
											.field("message", value)
											.field("@version", "1")
											.field("@timestamp", timeStamp)
											.field("source", source)
											.field("status", status)
											.endObject()).execute().actionGet();

				} else {
					// System.out.println(result);
					XContentBuilder obj = jsonBuilder().startObject();
					for (String key : result.keySet()) {
						obj.field(key.toString(), result.get(key));
					}
					obj.field("status", status);
					obj.field("source", source);
					obj.endObject();
					if(FetchLog.isDebug)
					{
						System.out.println("Index name:" + result.get("@indexName").toString() + ">>>" + result);
					}
					IndexResponse response = client
							.prepareIndex(result.get("@indexName").toString(),
									type, UUID.randomUUID().toString())
							.setSource(obj).execute().actionGet();
				}
			}
		}
	}

	public static HashMap<String, ILogParser> getLogParser() throws IOException {
		HashMap<String, ILogParser> result = new HashMap<String, ILogParser>();
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("logParser.properties");
		prop.load(fis);
		Set<Object> keys = prop.keySet();
		for (Object key : keys) {
			try {
				if (key.toString().endsWith(".parser")) {
					Class<?> c = Class
							.forName(prop.getProperty(key.toString()));
					Constructor<?> cons[] = c.getConstructors();
					ILogParser f1 = (ILogParser) cons[0].newInstance();
					result.put(key.toString(), f1);
				} else if (key.toString().endsWith(".index")) {
					indexMap.put(key.toString(), prop.getProperty(key.toString()));
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption("h", false, "Lists short help");
		options.addOption("t", true, "Time duration second");
		options.addOption("s", true,
				"elasticsearch service, server:port,server1:port");
		options.addOption("r", true, "redis ip");
		options.addOption("q", true, "redis queue name");
		options.addOption("g", true, "debug?");
		boolean debug = false;
		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(options, args);
			if (cl.getOptions().length > 0) {
				if (cl.hasOption('h')) {
					HelpFormatter hf = new HelpFormatter();
					hf.printHelp("Options", options);
				} else {
					String timeduration = cl.getOptionValue("t");
					String servers = cl.getOptionValue("s");
					String redis = cl.getOptionValue("r");
					String queue = cl.getOptionValue("q");
					String debugStr = cl.getOptionValue("g");
					if (debugStr != null && debugStr.trim().equals("1")) {
						System.out.println("debug opened...........");
						FetchLog.isDebug = true;
					}
					String[] queueNames = queue.split(",");
					if (timeduration == null || servers == null
							|| redis == null || queue == null) {
						HelpFormatter hf = new HelpFormatter();
						hf.printHelp("Options", options);
						System.exit(1);
					}
					int time = 60;
					try {
						time = Integer.parseInt(timeduration);
					} catch (Exception e) {

					}

					String serverList[] = servers.split(",");
					List<ServerObj> serverObjs = new ArrayList<ServerObj>();
					if (serverList.length > 0) {
						for (String serverStr : serverList) {
							String serverInfo[] = serverStr.split("\\:");
							String serverIP = serverInfo[0];
							String serverPort = serverInfo[1];
							int port = -1;
							try {
								port = Integer.parseInt(serverPort);
							} catch (Exception e) {
								continue;
							}
							ServerObj x = new ServerObj();
							x.setServerIP(serverIP);
							x.setServerPort(port);
							serverObjs.add(x);
						}
					}
					HashMap<String, ILogParser> logParser = FetchLog
							.getLogParser();
					FetchLog.setLogParser(logParser);
					FetchLog.doFetch(time, serverObjs, redis, queueNames);
				}
			} else {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("Options", options);
			}
		} catch (ParseException e) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp("Options", options);
		}
	}
}
