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

import java.util.ArrayList;
import java.util.List;

public class JobRequest {
	private String requestID;
	private String replyChannel;
	private String configFile = null;
	private String flow = null;
    private String instance = null;
    private List<String> jobsToSkip = new ArrayList<String>();
    private String errorMsg;

    public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public String getReplyChannel() {
		return replyChannel;
	}
	public void setReplyChannel(String replyChannel) {
		this.replyChannel = replyChannel;
	}
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public String getFlow() {
		return flow;
	}
	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public List<String> getJobsToSkip() {
		return jobsToSkip;
	}
	public void setJobsToSkip(List<String> jobsToSkip) {
		this.jobsToSkip = jobsToSkip;
	}

	public boolean validate() {
		boolean valid = true;
        if (null == configFile){
        	errorMsg = "Missing config file... quiiting";
            valid = false;
        }
        if (valid){
            if (null == flow){
                errorMsg = "Missing job flow ... quitting";
                valid = false;
            }
        }
        if (valid){
            if (null == instance){
            	errorMsg =  "Missing instance name... quitting";
                valid = false;
            }
        }
        return valid;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
