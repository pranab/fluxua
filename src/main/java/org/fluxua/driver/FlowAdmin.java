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

package org.fluxua.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.fluxua.config.Configurator;
import org.fluxua.config.FlowConfig;
import org.fluxua.config.FlowNode;

/**
 *
 * @author pranab
 * Maanges DAG based hadoop job workflow.
 */
public class FlowAdmin {
    private Map<String, Flow> flows = new HashMap<String, Flow>();

    public FlowAdmin() throws Exception {
        List<FlowConfig> flowConfigs = Configurator.instance().getFlowConfigs();
        for (FlowConfig flowConfig : flowConfigs){
            String flowName = flowConfig.getName();
            
            System.out.println("configuring flow: " + flowName);
            List<Job> flowJobs = new ArrayList<Job>();
            for (FlowNode flowNode : flowConfig.getFlowNodes()){
                Job job = new Job(flowNode.getJob(), flowNode.getPreReqJobs());
                flowJobs.add(job);
            }
            String iterClassName = flowConfig.getIterClassName();
            Flow flow = new Flow(flowName, flowJobs, iterClassName);
            flows.put(flowName, flow);
        }

    }

    public List<String> getReadyJobs(String flowName)throws Exception {
        List<String> readyJobs = new ArrayList<String>();
        Flow flow = flows.get(flowName);
        if (null != flow){
            List<Job> jobs = flow.getJobs();
            for (Job job : jobs){
                if (job.isReady()){
                    readyJobs.add(job.getName());
                }
            }
        } else {
            throw new Exception("Invalid flow: " + flowName);
        }

        return readyJobs;
    }
 
    public Iterator getFlowIterator(String flowName){
        Iterator iter = null;
        Flow flow = flows.get(flowName);
        return flow.getIterator();
    }
    
    public boolean isJobIndependent(String flowName, String jobName) throws Exception{
        Job job = findJob(flowName, jobName);
        return job.isIndependent();
    }

    public void notifyJobStart(String flowName, String jobName) throws Exception {
        Job job = findJob(flowName, jobName);
        job.notifyJobStart();
        System.out.println("Job " + jobName + " notified start " );
    }

    public void notifyJobComplete(String flowName, String jobName, String outputPath) throws Exception {
        Job job = findJob(flowName, jobName);
        job.addToOutputPath(outputPath);
        job.notifyJobComplete();

        List<Job> jobs = flows.get(flowName).getJobs();
        for (Job thisJob : jobs){
            thisJob.notifyPreReqComplete(jobName);
        }
        System.out.println("Job " + jobName + " notified completion");
    }

    public void notifyJobFailed(String flowName, String jobName) throws Exception {
        Job job = findJob(flowName, jobName);
        job.notifyJobFailed();
        System.out.println("Job " + jobName + " notified failure");
    }

    public void addToOutputPath(String flowName, String jobName, String outputPath)throws Exception {
        Job job = findJob(flowName, jobName);
        job.addToOutputPath(outputPath);
    }

    public List<String> getPreReqOutputPaths(String flowName, String jobName)throws Exception {
        List<String> outputPaths = new ArrayList<String>();
        Job job = findJob(flowName, jobName);

        for (String preReqJobName : job.getPreReqsCopy()){
            Job preReqJob = findJob(flowName, preReqJobName);
            outputPaths.addAll(preReqJob.outputPaths);
        }

        return outputPaths;
    }

    private Job findJob(String flowName, String jobName) throws Exception{
        Job targetJob = null;
        Flow flow = flows.get(flowName);
        if (null != flow){
            targetJob = flow.findJobByBame(jobName);
            if (null == targetJob){
                throw new Exception("invalid job: " + jobName);
            }
        }else {
            throw new Exception("Invalid flow: " + flowName);
        }

        return targetJob;
    }

    private static class Flow {
        private String name;
        private List<Job> jobs;
        private Iterator iterator;

        public Flow(String name, List<Job> jobs, String iterClassName) throws Exception {
            this.name = name;
            this.jobs = jobs;
            if (null != iterClassName){
                Class<?> iterCls = Class.forName(iterClassName);
                iterator = (Iterator)iterCls.newInstance();
            }
        }

        /**
         * @return the jobs
         */
        public List<Job> getJobs() {
            return jobs;
        }

        public Job findJobByBame(String jobName){
            Job targetJob = null;
            for (Job job : jobs){
                if (job.getName().equals(jobName)){
                    targetJob = job;
                    break;
                }
            }
            return targetJob;
        }

        /**
         * @return the iterator
         */
        public Iterator getIterator() {
            return iterator;
        }

        /**
         * @param iterator the iterator to set
         */
        public void setIterator(Iterator iterator) {
            this.iterator = iterator;
        }

    }
    
    public enum JobState {
        READY, BLOCKED, RUNNING, SUCCEEDED, FAILED
    }
    
    private static class Job {
        private String name;
        private List<String> preReqs = new ArrayList<String>();
        private List<String> preReqsCopy = new ArrayList<String>();
        private boolean independent;
        private List<String> outputPaths = new ArrayList<String>();
        private JobState state;

        public Job(String name, List<String> preReqs) {
            this.name = name;
            if (null != preReqs) {
            	this.preReqs = preReqs;
            	preReqsCopy.addAll(this.preReqs);
            }
            independent = this.preReqs.isEmpty();
            state = this.preReqs.isEmpty()? JobState.READY : JobState.BLOCKED;
        }

        public boolean isReady(){
            return state == JobState.READY;
        }
        
        public void notifyJobStart(){
            state = JobState.RUNNING;
        }

        public void notifyPreReqComplete(String jobName){
            preReqs.remove(jobName);
	    if (state == JobState.BLOCKED && preReqs.isEmpty()){
             	state = JobState.READY;
            }
        }

        public void notifyJobComplete(){
            state = JobState.SUCCEEDED;
        }

        public void notifyJobFailed(){
            state = JobState.FAILED;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the independent
         */
        public boolean isIndependent() {
            return independent;
        }

        public void addToOutputPath(String outputPath){
            System.out.println("Adding to complete job output path: " + outputPath);
            outputPaths.add(outputPath);
        }

        /**
         * @return the outputPaths
         */
        public List<String> getOutputPaths() {
            return outputPaths;
        }

        /**
         * @return the preReqsCopy
         */
        public List<String> getPreReqsCopy() {
            return preReqsCopy;
        }

    }

}
