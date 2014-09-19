package com.inv.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.index.query.QueryBuilders.*;

public class TestQuery {
	public static void main(String[] args) throws IOException {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "elasticsearch_group1").build();
		TransportClient transportClient = new TransportClient(settings);
		Client client = transportClient.addTransportAddress(
				new InetSocketTransportAddress("task101.dev.la.mezimedia.com", 9301))
				.addTransportAddress(
						new InetSocketTransportAddress("task101.dev.la.mezimedia.com", 9301));
		
		PrintWriter pw = new PrintWriter(new FileWriter("./a.csv"));
		SearchResponse scrollResp  = client.prepareSearch("logstash-2014.09.19", "logstash-2014.09.18")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setScroll(new TimeValue(60000))
		        .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("varnishhit","pass")).must(QueryBuilders.termQuery("source","inv_varnish"))).setPostFilter(FilterBuilders.rangeFilter("@timestamp").from(1411074234817l).to(1411095834818l))     
		        .setSize(100).execute()
		        .actionGet();
		while (true) {
			int i=0;
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
		    for (SearchHit hit : scrollResp.getHits()) {
		    	Map<String, Object> pair = hit.getSource(); 
		    	String URL = (String) pair.get("URL");
		    	if(URL.contains("investopedia.com"))
		    	{
		    		pw.println(pair.get("method") + ", " + pair.get("varnishhit") + " , " +URL.replaceAll("http://www.investopedia.com", ""));	
		    		System.out.println(pair.get("method") + ", " + pair.get("varnishhit") + " , " +URL.replaceAll("http://www.investopedia.com", ""));
		    	}
//		    	Marp
		        
		    }
		    //Break condition: No hits are returned
		    if (scrollResp.getHits().getHits().length == 0) {
		    	System.out.println("Done");
		        break;
		    }
		}
		pw.flush();
		pw.close();
		
	}
}
