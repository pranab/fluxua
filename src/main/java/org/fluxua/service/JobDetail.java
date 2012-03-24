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

public class JobDetail {
	protected String requestID;
	protected String flow = null;
	protected String instance = null;
	protected String msg;
	protected int status;
    public static final int ST_REFUSED = -3;
    public static final int ST_FAILED = -2;
    public static final int ST_INVALID = -1;
    public static final int ST_PENDING = 0;
    public static final int ST_SUCCEEDED = 1;
    
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
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
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
