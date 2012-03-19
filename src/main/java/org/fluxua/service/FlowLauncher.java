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

import org.fluxua.config.Configurator;
import org.fluxua.driver.JobDriver;

public class FlowLauncher extends Thread {
	private JobRequest request;
	private JobResponse response;
	private ServiceManager svcMan;

	public FlowLauncher(JobRequest request, ServiceManager svcMan) {
		super();
		this.request = request;
		this.svcMan = svcMan;
		response = new JobResponse();
		response.setRequestID(request.getRequestID());
	}
	
    @Override
    public void run() {
    	boolean valid = request.validate();
    	if (!valid) {
    		response.setMsg(request.getErrorMsg());
     	   response.setSucceeded(false);
    	} else {
           try {
        	   Configurator.initialize(request.getConfigFile());
	           JobDriver driver = new JobDriver(request.getFlow(), request.getInstance(), request.getJobsToSkip());
	           driver.start();
	           if (driver.isInError()) {
	        	   response.setMsg(driver.getErrorMsg());
	        	   response.setSucceeded(false);
	           }
           } catch (Exception e) {
        	   response.setSucceeded(false);
           }
    	}
    	
    	svcMan.handleResponse(response);
    }	
	
	
}
