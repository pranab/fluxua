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

import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.fluxua.config.Configurator;
import org.fluxua.config.JobConfig;

/**
 *
 * @author pranab
 */
public class JobDriver {
    private JobAdmin jobAdmin;
    private FlowAdmin flowAdmin;
    private String flowName;
    private String instance;
    private BlockingQueue<JobDriver.JobStatus> queue = new ArrayBlockingQueue<JobDriver.JobStatus>(20);

    public JobDriver(String flowName, String instance) {
        FileInputStream fis = null;
        try {
            jobAdmin = new JobAdmin();
            flowAdmin = new FlowAdmin();
            System.out.println("Processed flows");
            this.flowName = flowName;
            this.instance = instance;
        } catch (Exception ex) {
            System.out.println("Failed to initialize JobDriver" + ex);
            ex.printStackTrace();
        } finally {
        }

    }

    private void start(){
        try {
            int jobInstanceCount = 0;
            int totalJobInstanceCount = 0;
            boolean inError = false;
            boolean interactive = Configurator.instance().isInteractive();
            while (true){
                if (!inError){
                    List <String> jobNames = flowAdmin.getReadyJobs(flowName);

                    System.out.println("\nReady jobs: " + jobNames);

                    //quit if no more new jobs and no pending jobs
                    if (jobNames.isEmpty() && 0 == jobInstanceCount){
                        System.out.println("Quitting .. no more jobs to run and no more pending jobs. Num of jobs ran: " + totalJobInstanceCount);
                        break;
                    }

                    //launch new jobs
                    for (String jobName : jobNames){
                        List<String> outputPaths = null;
                        boolean independent = flowAdmin.isJobIndependent(flowName, jobName);
                        System.out.println("\nNext job: " + jobName + " is " + (independent? "independent" : "dependent"));
                        JobConfig jobConfig = Configurator.instance().findJobConfig(jobName);

                        //job dependent and output of dependent jobs to be used
                        if (!independent && jobConfig.isUseDependentOutput()){
                            outputPaths = flowAdmin.getPreReqOutputPaths(flowName, jobName);
                            System.out.println("Output paths of pre req jobs: " + outputPaths);
                        }

                        //get job instances to run
                        List<JobAdmin.JobParameter> jobParams = jobAdmin.getJobParameter(jobName, instance, outputPaths);
                        for (JobAdmin.JobParameter jobParam : jobParams){
                            System.out.println("Next job: " + jobParam);

                            if (interactive){
                                String command = "";
                                System.out.println(">>Type c to continue or q to quit");
                                Scanner sc = new Scanner(System.in);
                                while (sc.hasNext()) {
                                    command = sc.next();
                                    if (command.equals("c") || command.equals("q")){
                                        break;
                                    }
                                    System.out.println(">>Type c to continue or q to quit");
                                }
                                if (command.equals("q")){
                                    inError = true;
                                    break;
                                }
                            }

                            if (!inError){
                                JobLauncher launcher = new JobLauncher(jobParam, queue);
                                launcher.start();
                                flowAdmin.notifyJobStart(flowName, jobParam.getJobName());
                                ++jobInstanceCount;
                                ++totalJobInstanceCount;
                            }
                        }
                    }
                }  else {
                    //quit if in error and no pending jobs
                    if (0 == jobInstanceCount){
                        break;
                    }
                }


                //blocking wait for status back
                if (jobInstanceCount > 0){
                    System.out.println("Going to wait for the next job to complete");
                    JobStatus status = queue.take();
                    System.out.println("Job completed: " + status.getJobName());
                    --jobInstanceCount;
                    if (status.isValid()){
                        flowAdmin.notifyJobComplete(flowName, status.getJobName(), status.outputPath);
                    } else {
                        flowAdmin.notifyJobFailed(flowName, status.getJobName());
                        inError = true;
                    }
                    System.out.println(status);
                }
            }

            if (!inError){
                System.out.println("Drive completed successfully, num of jobs run : " + totalJobInstanceCount);
            } else {
                System.out.println("Drive completed unsuccessfully, num of jobs run : " + totalJobInstanceCount);
            }
        } catch (Exception ex){
            System.out.println("Failed to run job: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    public static class JobStatus {
        private String jobName;
        private String jobInstance;
        private boolean valid;
        private String message;
        private String outputPath;

        public JobStatus(String jobName, String jobInstance, boolean valid, String message, String outputPath) {
            this.jobName = jobName;
            this.jobInstance = jobInstance;
            this.valid = valid;
            this.message = message;
            this.outputPath = outputPath;
        }

        /**
         * @return the jobName
         */
        public String getJobName() {
            return jobName;
        }

        /**
         * @return the status
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        public String toString(){
            StringBuilder stBuilder = new StringBuilder("JobStatus: job name: " + jobName + " instance: " + getJobInstance());
            if (valid){
                stBuilder.append(" completed successfully");
            } else {
                stBuilder.append(" failed.\n Error message: " + message);
            }
            return stBuilder.toString();

        }

        /**
         * @return the outputPath
         */
        public String getOutputPath() {
            return outputPath;
        }

        /**
         * @return the jobInstance
         */
        public String getJobInstance() {
            return jobInstance;
        }
    }

    public static void main(String[] cmdLineArgs) throws Exception {
        if (cmdLineArgs.length  >= 3){
            String configFile = cmdLineArgs[0];
            String flow = cmdLineArgs[1];
            String instance = cmdLineArgs[2];
            
            //intialize config
            Configurator.initialize(configFile);
            
            JobDriver driver = new JobDriver(flow, instance);
            driver.start();

        } else {
            System.out.println("Should provide at least config file,job flow and flow instance name to run");
        }
    }


}
