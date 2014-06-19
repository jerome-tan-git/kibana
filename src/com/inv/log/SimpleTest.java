package com.inv.log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import redis.clients.jedis.Jedis;

public class SimpleTest {
	public static void main(String[] args) throws ElasticsearchException,
			IOException, InterruptedException {

		Jedis jedis1 = new Jedis("10.16.3.38");
//		Settings settings = ImmutableSettings.settingsBuilder()
//				.put("cluster.name", "elasticsearch_group1").build();
//		TransportClient transportClient = new TransportClient(settings);
//		Client client = transportClient.addTransportAddress(
//				new InetSocketTransportAddress("task101.dev.la.mezimedia.com",
//						9301)).addTransportAddress(
//				new InetSocketTransportAddress("task101.dev.la.mezimedia.com",
//						9301));
		while (true) {
//			SimpleDateFormat format = new SimpleDateFormat(
//					"dd/MMM/yyyy:HH:mm:ss");
//			SimpleDateFormat format1 = new SimpleDateFormat(
//					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//			SimpleDateFormat format2 = new SimpleDateFormat(
//					"'logstash'-yyyy.MM.dd");
//			java.util.Calendar c = Calendar.getInstance();
//			c.add(Calendar.HOUR, -8);
//			Date date = c.getTime();
			try {
				System.out.println("a");
				List<String> values = jedis1.blpop(1, new String[]{"logstash_jerome:redis","aa"});
				System.out.println(values.size());
				//System.out.println(value);
//				try {
//					String str = (value.split("\\[")[1].split("\\]")[0]
//							.split("\\s")[0]);
//					date = format.parse(str);
//
//				} catch (Exception e) {
//					continue;
//				}
//				String indexName = format2.format(date);
//				//System.out.println(indexName);
//				IndexResponse response = client
//						.prepareIndex(indexName, "production",
//								UUID.randomUUID().toString())
//						.setSource(
//								jsonBuilder()
//										.startObject()
//										.field("message", value)
//										.field("@version", "1")
//										.field("@timestamp",
//												format1.format(date))
//										.field("ip", "213.52.50.8")
//
//										// .field("_id",
//										// UUID.randomUUID().toString())
//										.endObject()).execute().actionGet();
//				String _index = response.getIndex();
//				// Type name
//				String _type = response.getType();
//				// Document ID (generated or not)
//				String _id = response.getId();
//				// Version (if it's the first time you index this document, you
//				// will
//				// get: 1)
//				long _version = response.getVersion();
//				System.out.println(value);
//				System.out.println(FetchLog.parseLog(value) + " | " + value);
//				System.out.println(_index + " | " + _type + " | " + _id + " | "
//						+ _version + " | " + format1.format(date));

				// try
				// {
				// System.out.println(new Date().toLocaleString()+ ":"+
				// jedis1.llen("logstash_jerome:redis"));
				// }
				// catch(Exception e)
				// {
				//
				// }
				// Thread.sleep(1000);
			}

			catch (Exception e) {
				e.printStackTrace();
				Thread.sleep(500);
				continue;
			}
		}
	}
}
