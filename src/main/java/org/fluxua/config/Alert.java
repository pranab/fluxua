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

package org.fluxua.config;

public class Alert {
	private String counterGroup;
	private String counter;
	private String expression;
	private String message;
	
	public String getCounterGroup() {
		return counterGroup;
	}
	public void setCounterGroup(String counterGroup) {
		this.counterGroup = counterGroup;
	}
	public String getCounter() {
		return counter;
	}
	public void setCounter(String counter) {
		this.counter = counter;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

}
