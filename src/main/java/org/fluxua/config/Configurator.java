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

import java.io.File;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author pranab
 */
public class Configurator {
    private boolean interactive;
    private boolean verbose;
    private String user;
    private String note;
    private String cluster;
    private List<JobConfig> jobConfigs;
    private List<FlowConfig> flowConfigs;
    
    private static Configurator configurator;

    public static void initialize(String configFile) throws Exception{
        ObjectMapper mapper = new ObjectMapper(); 
        configurator = mapper.readValue(new File(configFile), Configurator.class);
    }
    
    public static Configurator instance(){
        return configurator;
    }


    /**
     * @return the interactive
     */
    public boolean isInteractive() {
        return interactive;
    }

    /**
     * @param interactive the interactive to set
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    /**
     * @return the verbose
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * @param verbose the verbose to set
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * @return the jobConfigs
     */
    public List<JobConfig> getJobConfigs() {
        return jobConfigs;
    }

    /**
     * @param jobConfigs the jobConfigs to set
     */
    public void setJobConfigs(List<JobConfig> jobConfigs) {
        this.jobConfigs = jobConfigs;
    }

    /**
     * @return the flowConfigs
     */
    public List<FlowConfig> getFlowConfigs() {
        return flowConfigs;
    }

    /**
     * @param flowConfigs the flowConfigs to set
     */
    public void setFlowConfigs(List<FlowConfig> flowConfigs) {
        this.flowConfigs = flowConfigs;
    }
    
    public JobConfig findJobConfig(String name){
        JobConfig jobConfig = null;
        for (JobConfig thisJobConfig : jobConfigs){
            if (thisJobConfig.getName().equals(name)){
                jobConfig = thisJobConfig;
                break;
            }
        }
        
        return jobConfig;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the cluster
     */
    public String getCluster() {
        return cluster;
    }

    /**
     * @param cluster the cluster to set
     */
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

}
