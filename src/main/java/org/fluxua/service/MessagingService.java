/*
 * Fluxua: A simple Hadoop map reduce workflow engine
 * Author: Pranab Ghosh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.fluxua.service;

import java.util.Properties;

import redis.clients.jedis.Jedis;

public class MessagingService {
	private Jedis jedis;
	private String requestQueue;
	private String adminQueueIn;
	private String adminQueueOut;
	
	public MessagingService(Properties prop) {
		String redisHost = prop.getProperty("redis.server.host", "localhost");
		int redisPort = new Integer(prop.getProperty("redis.server.port", "6379"));
		System.out.println("host:" +redisHost + " redisPort:" + redisPort );
		jedis = new Jedis(redisHost, redisPort);
		
		requestQueue = prop.getProperty("request.queue");
		adminQueueIn = prop.getProperty("admin.queue.in");
		adminQueueOut = prop.getProperty("admin.queue.out");
		System.out.println("requestQueue:" +requestQueue + " adminQueueIn:" + adminQueueIn );
	}
	
	public String receiveJobRequest() {
		String requestSt = jedis.rpop(requestQueue);
		return requestSt;
	}

	public void replyJobRequest(String repChannel, String response) {
        jedis.lpush(repChannel, response);
	}
	
	public String receiveAdminRequest() {
		String requestSt = jedis.rpop(adminQueueIn);
		return requestSt;
	}
	
	public void replyAdminRequest( String response) {
        jedis.lpush(adminQueueOut, response);
	}
	
	public void close() {
		jedis.quit();
	}
}
