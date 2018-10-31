/*
 * Copyright (C) 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.engineering.srcclr.json.sourceclear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "metadata",
    "graphs",
    "libraries",
    "vulnerabilities",
    "unmatchedLibraries",
    "vulnMethods"
})
public class Record {

    @JsonProperty("metadata")
    private Metadata_ metadata;
    @JsonProperty("graphs")
    private List<Graph> graphs = new ArrayList<Graph>();
    @JsonProperty("libraries")
    private List<Library> libraries = new ArrayList<Library>();
    @JsonProperty("vulnerabilities")
    private List<Vulnerability> vulnerabilities = new ArrayList<Vulnerability>();
    @JsonProperty("unmatchedLibraries")
    private List<Object> unmatchedLibraries = new ArrayList<Object>();
    @JsonProperty("vulnMethods")
    private List<Object> vulnMethods = new ArrayList<Object>();

    @JsonProperty("metadata")
    public Metadata_ getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata_ metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("graphs")
    public List<Graph> getGraphs() {
        return graphs;
    }

    @JsonProperty("graphs")
    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }

    @JsonProperty("libraries")
    public List<Library> getLibraries() {
        return libraries;
    }

    @JsonProperty("libraries")
    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    @JsonProperty("vulnerabilities")
    public List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    @JsonProperty("vulnerabilities")
    public void setVulnerabilities(List<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    @JsonProperty("unmatchedLibraries")
    public List<Object> getUnmatchedLibraries() {
        return unmatchedLibraries;
    }

    @JsonProperty("unmatchedLibraries")
    public void setUnmatchedLibraries(List<Object> unmatchedLibraries) {
        this.unmatchedLibraries = unmatchedLibraries;
    }

    @JsonProperty("vulnMethods")
    public List<Object> getVulnMethods() {
        return vulnMethods;
    }

    @JsonProperty("vulnMethods")
    public void setVulnMethods(List<Object> vulnMethods) {
        this.vulnMethods = vulnMethods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("metadata", metadata).append("graphs", graphs).append("libraries", libraries).append("vulnerabilities", vulnerabilities).append("unmatchedLibraries", unmatchedLibraries).append("vulnMethods", vulnMethods).toString();
    }

}
