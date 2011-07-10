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

import org.fluxua.driver.JobAdmin.JobParameter;
import org.fluxua.driver.JobDriver.JobStatus;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author pranab
 * Launches the hadoop job in the same thread and waits for completion communicates
 * status back to the job driver, through the blocking queue.
 */
public class JobLauncher extends Thread {
    public static ThreadLocal<JobContext> jobContextThreadLoc = new ThreadLocal<JobContext>();
    
    private JobAdmin.JobParameter jobParameter;
    private BlockingQueue<JobDriver.JobStatus> queue;

    public JobLauncher(JobParameter jobParameter, BlockingQueue<JobStatus> queue) {
        this.jobParameter = jobParameter;
        this.queue = queue;
    }


    @Override
    public void run() {
        //start job
        JobDriver.JobStatus status = null;
        String[] args = jobParameter.getArgs();
        String outputPath = args[args.length - 1];
        
        //set job context thread local
        String jobName = jobParameter.getJobName();
        String instanceName = jobParameter.getJobInstance();
        JobContext jobContext = new JobContext();
        jobContext.setJobName(jobName);
        jobContext.setInstanceName(instanceName);
        setJobContext(jobContext);
        
        try {
            //launch job
            int exitCode = ToolRunner.run(jobParameter.getJob(), args);
            status = 0 == exitCode ? 
                new JobDriver.JobStatus(jobParameter.getJobName(), jobParameter.getJobInstance(), true, "job completed successfully", outputPath) :
                new JobDriver.JobStatus(jobParameter.getJobName(), jobParameter.getJobInstance(), false, "job failed", outputPath);
        
            if (0 == exitCode){
                collectJobMetrics();
            }
        } catch (Exception ex) {
            status = new JobDriver.JobStatus(jobParameter.getJobName(), jobParameter.getJobInstance(), false, ex.getMessage(), outputPath);
        }
        
        //notify driver
        try {
            queue.put(status);
        } catch (InterruptedException ex) {
            System.out.println("Failed to notify driver aabout job status");
        }
    }
    
    private void collectJobMetrics() {
        
    }
    
    public static JobContext getJobContext(){
        return jobContextThreadLoc.get();
    }
    
    public static void setJobContext(JobContext jobContext){
        jobContextThreadLoc.set(jobContext);
    }

}
