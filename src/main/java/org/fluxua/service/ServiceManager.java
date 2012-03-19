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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;

import redis.clients.jedis.Jedis;

public class ServiceManager implements Runnable {
	private Jedis jedis;
	private String requestQueue;
	private String adminQueueIn;
	private String adminQueueOut;
	private static final String COM_STOP = "stop";
	private static final String COM_STATUS = "status";
	private List<JobRequest> requests = new  ArrayList<JobRequest>();
	
	public ServiceManager(String propFile)  throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(propFile));
		String redisHost = prop.getProperty("redis.server.host", "localhost");
		int redisPort = new Integer(prop.getProperty("redis.server.port", "6379"));
		System.out.println("host:" +redisHost + " redisPort:" + redisPort );
		jedis = new Jedis(redisHost, redisPort);
		
		requestQueue = prop.getProperty("request.queue");
		adminQueueIn = prop.getProperty("admin.queue.in");
		adminQueueOut = prop.getProperty("admin.queue.out");
		System.out.println("requestQueue:" +requestQueue + " adminQueueIn:" + adminQueueIn );
	}

	@Override
	public void run() {
		while (true) {
			//request queue
			String requestSt = jedis.rpop(requestQueue);
			if (null != requestSt) {
				System.out.println("got from request queue:" + requestSt);
		        ObjectMapper mapper = new ObjectMapper(); 
		        try {
		        	//launch the flow
					JobRequest request = mapper.readValue(requestSt, JobRequest.class);
					requests.add(request);
					FlowLauncher launcher = new FlowLauncher(request, this);
					launcher.start();
				} catch (Exception e) {
					System.out.println("invalid request");
				}
				
			}
			
			//admin queue
			String adminCom = jedis.rpop(adminQueueIn);
			if (null != adminCom) {
				System.out.println("got from admin queue:" + adminCom);
				
				if ( adminCom.equals(COM_STOP)) {
					System.out.println("got stop command from admin queue....  shutting down");
					jedis.lpush(adminQueueOut,"shutting down");
					jedis.quit();
					break;
				}
			}
		}
	}

   public void handleResponse(JobResponse response) {
	   
   }
}
