package com.inv.log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
//{"message":"74.3.128.130 192.168.137.183 - - [12/Jun/2014:02:25:05 +0000] \"GET http://www.investopedia.com/vcb/assets_v2/js/website_ver_1_254.js?v=5.022 HTTP/1.1\" 304 460 \"-\" \"Mozilla/4.0 (compatible;)\" hit 132.799149 0",
//"@version":"1","@timestamp":"2014-06-12T02:25:05.000Z","type":"INV-VARNISH","host":"task101.dev.la.mezimedia.com","log.ip":"74.3.128.130","log.lbip":"192.168.137.183","log.url":"http://www.investopedia.com/vcb/assets_v2/js/website_ver_1_254.js?v=5.022","log.reponosecode":"304","log.request":"GET","log.responsetime":"132.799149","log.contentsize":"460","log.status_code":"304","log.referer":"-","log.useragent":"Mozilla/4.0 (compatible;)"}

public class TestSearch {
	public static void main(String[] args) throws ElasticsearchException,
			IOException, InterruptedException {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		
		TimeZone tztz = TimeZone.getTimeZone("UTC");
		LookupService cl = new LookupService("./GeoIPCity.dat", LookupService.GEOIP_MEMORY_CACHE);

		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch_group1").build();
		TransportClient transportClient = new TransportClient(settings);
		Client client = transportClient.addTransportAddress(
				new InetSocketTransportAddress("task101.dev.la.mezimedia.com", 9301))
				.addTransportAddress(
						new InetSocketTransportAddress("task101.dev.la.mezimedia.com", 9301));
		for (int i = 0; i < 10000; i++) {
			java.util.Calendar c = Calendar.getInstance();
			c.add(Calendar.HOUR, -8);
			Location l2 = cl.getLocation("213.52.50.8");
			IndexResponse response = client
					.prepareIndex("logstash-2014.06.12", "production", UUID.randomUUID().toString())
					.setSource(
							jsonBuilder()
									.startObject()
									.field("message",
											"74.3.128.130 192.168.137.183 - - [12/Jun/2014:02:25:05 +0000]")
									.field("@version", "1")
									.field("@timestamp",
											formatter.format(c.getTime()))
									.field("ip","213.52.50.8")
									.field("location",new String[]{""+l2.longitude,""+l2.latitude})
//									.field("_id", UUID.randomUUID().toString())
									.endObject()).execute().actionGet();
			String _index = response.getIndex();
			// Type name
			String _type = response.getType();
			// Document ID (generated or not)
			String _id = response.getId();
			// Version (if it's the first time you index this document, you will
			// get: 1)
			long _version = response.getVersion();

			System.out.println(_index + " | " + _type + " | " + _id + " | "
					+ _version + " | " + formatter.format(c.getTime()));
			Thread.sleep(500);
		}
	}

}
