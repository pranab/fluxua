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
import java.util.UUID;

public class JobRequest extends JobDetail{
	private String configFile = null;
    private List<String> jobsToSkip = new ArrayList<String>();
    private int opCode;
	private String replyChannel;
    public static final int OP_EXEC = 1;
    public static final int OP_STATUS = 2;
    
    public JobRequest() {
    	super();
    }

    public void createRequestID() {
    	requestID = UUID.randomUUID().toString();
    }
    
	public String getConfigFile() {
		return configFile;
	}
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	public List<String> getJobsToSkip() {
		return jobsToSkip;
	}
	public void setJobsToSkip(List<String> jobsToSkip) {
		this.jobsToSkip = jobsToSkip;
	}

	public int getOpCode() {
		return opCode;
	}
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
	public String getReplyChannel() {
		return replyChannel;
	}
	public void setReplyChannel(String replyChannel) {
		this.replyChannel = replyChannel;
	}
	
	public boolean validate() {
		boolean valid = true;
        if (null == configFile){
        	msg = "Missing config file... quiiting";
            valid = false;
        }
        if (valid){
            if (null == flow){
                msg = "Missing job flow ... quitting";
                valid = false;
            }
        }
        if (valid){
            if (null == instance){
            	msg =  "Missing instance name... quitting";
                valid = false;
            }
        }
        return valid;
	}
	
	public boolean isExecuteRequest() {
		return opCode == OP_EXEC;
	}

	public boolean isStatusRequest() {
		return opCode == OP_STATUS;
	}
	
	public void setConfigFullPath(String rootDir) {
		configFile = rootDir + configFile + ".json";
	}
	
	public boolean isNewJob() {
		return null == requestID;
	}
	
	public JobResponse createResponse() {
		JobResponse response = new JobResponse();
		response.setRequestID(requestID);
		response.setFlow(flow);
		response.setInstance(instance);
		response.setStatus(status);
		return response;
	}
}
