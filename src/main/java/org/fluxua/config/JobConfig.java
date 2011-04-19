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

import java.util.List;

/**
 *
 * @author pranab
 */
public class JobConfig {
    private String name;
    private String description;
    private String author;
    private String className;
    private List<String> inputPaths;
    private String outputPath;
    private List<String> libjars;
    private List<String> files;
    private List<String> userParams;
    private boolean outputToBeDeleted;
    private boolean useDependentOutput;

    /**
     * @return the inputPaths
     */
    public List<String> getInputPaths() {
        return inputPaths;
    }

    /**
     * @param inputPaths the inputPaths to set
     */
    public void setInputPaths(List<String> inputPaths) {
        this.inputPaths = inputPaths;
    }

    /**
     * @return the outputPath
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * @param outputPath the outputPath to set
     */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * @return the libjars
     */
    public List<String> getLibjars() {
        return libjars;
    }

    /**
     * @param libjars the libjars to set
     */
    public void setLibjars(List<String> libjars) {
        this.libjars = libjars;
    }

    /**
     * @return the files
     */
    public List<String> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<String> files) {
        this.files = files;
    }

    /**
     * @return the userParams
     */
    public List<String> getUserParams() {
        return userParams;
    }

    /**
     * @param userParams the userParams to set
     */
    public void setUserParams(List<String> userParams) {
        this.userParams = userParams;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the outputToBeDeleted
     */
    public boolean isOutputToBeDeleted() {
        return outputToBeDeleted;
    }

    /**
     * @param outputToBeDeleted the outputToBeDeleted to set
     */
    public void setOutputToBeDeleted(boolean outputToBeDeleted) {
        this.outputToBeDeleted = outputToBeDeleted;
    }

    /**
     * @return the useDependentOutput
     */
    public boolean isUseDependentOutput() {
        return useDependentOutput;
    }

    /**
     * @param useDependentOutput the useDependentOutput to set
     */
    public void setUseDependentOutput(boolean useDependentOutput) {
        this.useDependentOutput = useDependentOutput;
    }

    
}
