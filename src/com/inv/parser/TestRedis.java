package com.inv.parser;



import java.util.List;

import redis.clients.jedis.Jedis;

public class TestRedis {
	public static void main(String[] args) throws InterruptedException {
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss [Z]");
//		String json = "{message:'Monitor phone works well at:\n "+ sdf.format(new Date())+"',phoneNumber:'18501755307',property:'property',type:'sms-info'}";
////		String a = new String(base64.encode(json.getBytes()));
////		System.out.println(a);
////		
//		Jedis jedis = new Jedis("192.168.103.18");
//		jedis.auth("123456redis");
//		jedis.rpush("sendQueue", json);
//		jedis.sadd("validPhone", "13671605961");
//		System.out.println(jedis.smembers("validPhone"));
//		jedis.lpush("validPhone", "13671605961");
//		jedis.lpush("validPhone", "13671605961");
////		jedis.lpush("access:123456789", System.currentTimeMillis()+"");
////		jedis.lpush("access:123456789", System.currentTimeMillis()+"");
//		jedis.ltrim("access:123456789", 0, 2);
//		System.out.println(jedis.lrange("access:1", 0, 2));
//		jedis.sadd("validPhone", "1");
//		jedis.sadd("validPhone", "2");
//		jedis.sadd("validPhone", "3");
//		System.out.println(jedis.sismember("validPhone", "5"));
//		jedis.incr("temp:123");
//		System.out.println(jedis.get("temp:1231"));
//		jedis.decr("temp:123");
//		System.out.println(jedis.get("temp:123"));
//		String a = jedis.lpop("receiveQueue");
//		System.out.println(a);
//		while (true) {
//			try {
////				Jedis jedis = new Jedis("192.168.103.18");
////				jedis.auth("123456redis");
//				System.out.println("Read data!");
//				String value = jedis.lpop("toSend");
//				if (value == null) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//					}
//				} else {
////					MessageObject o = JSON.parseObject(value, MessageObject.class);
//					System.out.println(value);
//				}
//			} catch (Exception e) {
//				if (e instanceof redis.clients.jedis.exceptions.JedisDataException)
//				{
//					jedis.del("toSend");
//					System.out.println("delete wrong data");
//					continue;
//				}
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//			}
//		}
//		MessageObject o = JSON.parseObject(json, MessageObject.class);
//		System.out.println(o);
		// jedis.set("testJson", json);
		// String value = jedis.get("testJson");
		// // JSO
		// 
		// //System.out.println(o.getPhoneNumber());
		// System.out.println(value);
		Jedis jedis1 = new Jedis("192.168.145.238");
		while(true)
		{
//			try
//			{
			//System.out.println(new Date().toLocaleString()+ ":"+ jedis1.llen("cmus_apache"));
			List<String> values = jedis1.blpop(1, new String[]{"pruk_apache"});
			String _log = (values.get(1));
			String host = _log.substring(0, _log.indexOf(' '));
				System.out.println(host + " | " + values.get(1));
//				if(a.indexOf('[')==-1)
//				{
				//	System.out.println(values);
//				}
//				else
//				{
					//System.out.println("aaa" + a);
//				}
//			}
//			catch(Exception e)
//			{
//				
//			}
//			Thread.sleep(1000);
		}

	}
}