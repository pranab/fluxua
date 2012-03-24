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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.fluxua.driver.JobDriver;

import redis.clients.jedis.Jedis;

public class ServiceManager implements Runnable {
	private String flowConfigRootDir;
	private static final String COM_STOP = "stop";
	private static final String COM_STATUS = "status";
	private List<JobRequest> requests = new  ArrayList<JobRequest>();
    private BlockingQueue<JobResponse> queue = new ArrayBlockingQueue<JobResponse>(20);
    private ObjectMapper mapper = new ObjectMapper(); 
    private MessagingService msgSvc;
    private boolean pendingShutdown;
	
	public ServiceManager(String propFile)  throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(propFile));
		
		flowConfigRootDir = prop.getProperty("flow.config.root.dir"); 
		msgSvc = new MessagingService(prop);
	}

	@Override
	public void run() {
		String responseSt;
		JobResponse response;
		
		while (true) {
			//request queue
			String requestSt = msgSvc.receiveJobRequest();
			if (null != requestSt) {
				System.out.println("got from request queue:" + requestSt);
		        try {
					JobRequest request = mapper.readValue(requestSt, JobRequest.class);
					if (request.isExecuteRequest()) {
						if (!pendingShutdown) {
				        	//launch the flow
							request.setConfigFullPath(flowConfigRootDir);
							requests.add(request);
							FlowLauncher launcher = new FlowLauncher(request, queue);
							launcher.start();
							
							response = request.createResponse();
							response.setStatus(JobResponse.ST_PENDING);
						} else {
							//refuse because of pending shutdown command
							response = request.createResponse();
							response.setStatus(JobResponse.ST_REFUSED);
						}
				        responseSt = mapper.writeValueAsString(response);
				        msgSvc.replyJobRequest(request.getReplyChannel(), responseSt);
					} else {
						//return status
						JobRequest req = getRequest(request.getRequestID());
						if (null  != req) {
							response = req.createResponse();
					        responseSt = mapper.writeValueAsString(response);
					        msgSvc.replyJobRequest(req.getReplyChannel(), responseSt);
						}
					}
				} catch (Exception e) {
					System.out.println("invalid request");
				}
			}

			//check response queue for flow completion
			try {
				response = queue.poll(1, TimeUnit.SECONDS);
				if (null != response) {
			        responseSt = mapper.writeValueAsString(response);
			        JobRequest req = getRequest(response.getRequestID());
			        req.setStatus(response.getStatus());
			        msgSvc.replyJobRequest(req.getReplyChannel(), responseSt);
			        
			        //if no pending jobs and received shutdown request then quit
					int numPending = numPendingJobs();
					if (pendingShutdown && numPending == 0) {
						//no pending jobs
						msgSvc.replyAdminRequest("shutting down");
						msgSvc.close();
						break;
					} 
				}
			} catch (Exception e) {
				
			}		
			
			//admin queue
			//String adminCom = jedis.rpop(adminQueueIn);
			String adminCom = msgSvc.receiveAdminRequest();
			if (null != adminCom) {
				System.out.println("got from admin queue:" + adminCom);
				if ( adminCom.equals(COM_STOP)) {
					System.out.println("got stop command from admin queue....  shutting down");
					int numPending = numPendingJobs();
					if (numPending == 0) {
						//no pending jobs
						msgSvc.replyAdminRequest("shutting down");
						msgSvc.close();
						break;
					} else {
						pendingShutdown = true;
						msgSvc.replyAdminRequest("will shutting down when " + numPending + " pending jobs complete" );
					}
				}
			}
		}
	}
	
	private  JobRequest getRequest(String requestID) {
		JobRequest req = null;
		for (JobRequest request : requests) {
			if (request.getRequestID().equals(requestID)) {
				req = request;
				break;
			}
		}
		
		return req;
	}
	
	
	public int numPendingJobs() {
		int numPending = 0;
		for (JobRequest request : requests) {
			if (request.getStatus() == JobDetail.ST_PENDING ) {
				++numPending;
			}
		}		
		return numPending;
	}

}
