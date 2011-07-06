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
public class FlowConfig {
    private String name;
    private String description;
    private String author;
    private List<FlowNode> flowNodes;
    private String iterClassName;

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
     * @return the flowNodes
     */
    public List<FlowNode> getFlowNodes() {
        return flowNodes;
    }

    /**
     * @param flowNodes the flowNodes to set
     */
    public void setFlowNodes(List<FlowNode> flowNodes) {
        this.flowNodes = flowNodes;
    }

    /**
     * @return the iterClassName
     */
    public String getIterClassName() {
        return iterClassName;
    }

    /**
     * @param iterClassName the iterClassName to set
     */
    public void setIterClassName(String iterClassName) {
        this.iterClassName = iterClassName;
    }

}
