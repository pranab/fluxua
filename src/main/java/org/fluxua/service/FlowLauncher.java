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

import java.util.concurrent.BlockingQueue;

import org.fluxua.config.Configurator;
import org.fluxua.driver.JobDriver;

public class FlowLauncher extends Thread {
	private JobRequest request;
	private JobResponse response;
	private BlockingQueue<JobResponse> queue;

	public FlowLauncher(JobRequest request, BlockingQueue<JobResponse> queue) {
		super();
		this.request = request;
		this.queue = queue;
		response = request.createResponse();
	}
	
    @Override
    public void run() {
    	boolean valid = request.validate();
    	if (!valid) {
    		response.setMsg(request.getMsg());
     	    response.setStatus(JobResponse.ST_INVALID);
    	} else {
           try {
        	   Configurator.initialize(request.getConfigFile());
	           JobDriver driver = new JobDriver(request.getFlow(), request.getInstance(), request.getJobsToSkip());
	           driver.start();
	           if (driver.isInError()) {
	        	   response.setMsg(driver.getErrorMsg());
	        	   response.setStatus(JobResponse.ST_FAILED);
	           } else {
	        	   response.setStatus(JobResponse.ST_SUCCEEDED);
	           }
           } catch (Exception e) {
        	   response.setStatus(JobResponse.ST_FAILED);
           }
    	}
    	try {
			queue.put(response);
		} catch (InterruptedException e) {
		}
    }	
	
	
}
