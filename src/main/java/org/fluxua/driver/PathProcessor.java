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

import java.util.Map;

/**
 *
 * @author pranab
 */
public abstract class PathProcessor {
    protected String timeUnit;

    public abstract void initialize(Map<String, String> inputProcessorArgMap);
    
    public String processInputPath(String inputPath){
        return inputPath;
    }

    public String processOutputPath(String outputPath){
        return outputPath;
    }

    public void setTimeUnit(String timeUnit){
        this.timeUnit = timeUnit;
    }
}

