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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.util.Tool;
import org.fluxua.config.Configurator;
import org.fluxua.config.JobConfig;

/**
 *
 * @author pranab
 */
public class JobAdmin {

    public JobAdmin() {
    }

    public List<JobParameter> getJobParameter(String jobName, String jobInstance, List<String> inputPaths) 
        throws Exception{
        List<JobParameter> jobParams = new ArrayList<JobParameter>();
        System.out.println("Configuring job parameters job name :" + jobName + 
                (null != inputPaths? (" intput paths: " + inputPaths) : ""));

        JobConfig jobConfig = Configurator.instance().findJobConfig(jobName);
        if (null != jobConfig){
            String className = jobConfig.getClassName();
            Class<?> cls = Class.forName(className);
            List<String> args = new ArrayList<String>();
            Tool job = (Tool)cls.newInstance();

            List<String> distFiles = jobConfig.getFiles();
            if (null != distFiles && !distFiles.isEmpty()){
                String fileSt = buildComaSepString(distFiles);
                args.add("-files");
                args.add(fileSt);
            }
            
            List<String> libJars = jobConfig.getLibjars();
            if (null != libJars && !libJars.isEmpty()){
                String libJarSt = buildComaSepString(libJars);
                args.add("-libjars");
                args.add(libJarSt);
            }
            
            List<String> params = jobConfig.getUserParams();
            if (null != params && !params.isEmpty()){
                for (String param : params){
                    args.add("-D");
                    args.add(param);
                }
            }   
            
            args.add("-D");
            args.add("job.name=" + jobConfig.getName());
            args.add("-D");
            args.add("job.instance=" + jobInstance);
            
            List<String> allInputPaths = new ArrayList<String>();
            List<String> confInputPaths = jobConfig.getInputPaths();
            
            //input and output  paths
            String outputPath = jobConfig.getOutputPath();
            if (null != confInputPaths && !confInputPaths.isEmpty()){
                String inpProcClass = jobConfig.getInputProcessorClass();
                if (null != inpProcClass){
                    //process input and output paths
                    Class<?> pathCls = Class.forName(inpProcClass);
                    PathProcessor pathProc = (PathProcessor)pathCls.newInstance();
                    pathProc.initialize(jobConfig.getInputProcessorArgMap());
                    List<String> processedPaths = new ArrayList<String>();
                    for (String inpPath : confInputPaths){
                       processedPaths.add(pathProc.processInputPath(inpPath)); 
                    }
                    allInputPaths.addAll(processedPaths);
                    
                    outputPath = pathProc.processOutputPath(outputPath);
                } else {
                    allInputPaths.addAll(confInputPaths);
                }
            }            
            
            //dependent job output paths
            if (null != inputPaths && !inputPaths.isEmpty()){
                allInputPaths.addAll(inputPaths);
            }   
            
            String inputPathSt = buildComaSepString(allInputPaths);
            args.add(inputPathSt);
            
            args.add(outputPath);
            
            String[] argArr = {};
            argArr = args.toArray(argArr);
            JobParameter jobParam = new JobParameter(jobName, jobInstance, job, argArr);
            jobParams.add(jobParam);
            
            
        } else {
            throw new Exception("Invalid job name " + jobName);
        }
        
        return jobParams;

    }

    private String buildComaSepString(List<String> items){
        StringBuilder stBuilder = new StringBuilder();
        for (int i = 0; i <  items.size(); ++i){
            if (i > 0){
                stBuilder.append(",");
            }
            stBuilder.append(items.get(i));
        }
        return stBuilder.toString();
    }

    public static class JobParameter {
        private String jobName;
        private String jobInstance;
        private Tool job;
        private String[] args;

        public JobParameter(String jobName, String jobInstance, Tool job, String[] args) {
            this.jobName = jobName;
            this.jobInstance = jobInstance;
            this.job = job;
            this.args = args;
        }

        /**
         * @return the job
         */
        public Tool getJob() {
            return job;
        }

        /**
         * @return the args
         */
        public String[] getArgs() {
            return args;
        }

        /**
         * @return the jobName
         */
        public String getJobName() {
            return jobName;
        }

        /**
         * @return the instanceId
         */
        public String getJobInstance() {
            return jobInstance;
        }

        public String toString(){
            StringBuilder stBuilder = new StringBuilder("JobParameter: job name: " + jobName + " instance: " + 
                    jobInstance + "\n" + " args ");
            for (String arg : args) {
                stBuilder.append(arg).append("  ");
            }
            return stBuilder.toString();

        }


    }

}
