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
    "name",
    "description",
    "author",
    "authorUrl",
    "language",
    "coordinateType",
    "coordinate1",
    "coordinate2",
    "bugTrackerUrl",
    "codeRepoType",
    "codeRepoUrl",
    "latestRelease",
    "latestReleaseDate",
    "versions",
    "_links"
})
public class Library {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("author")
    private Object author;
    @JsonProperty("authorUrl")
    private String authorUrl;
    @JsonProperty("language")
    private String language;
    @JsonProperty("coordinateType")
    private String coordinateType;
    @JsonProperty("coordinate1")
    private String coordinate1;
    @JsonProperty("coordinate2")
    private String coordinate2;
    @JsonProperty("bugTrackerUrl")
    private Object bugTrackerUrl;
    @JsonProperty("codeRepoType")
    private Object codeRepoType;
    @JsonProperty("codeRepoUrl")
    private String codeRepoUrl;
    @JsonProperty("latestRelease")
    private String latestRelease;
    @JsonProperty("latestReleaseDate")
    private String latestReleaseDate;
    @JsonProperty("versions")
    private List<Version> versions = new ArrayList<Version>();
    @JsonProperty("_links")
    private Links_ links;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("author")
    public Object getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Object author) {
        this.author = author;
    }

    @JsonProperty("authorUrl")
    public String getAuthorUrl() {
        return authorUrl;
    }

    @JsonProperty("authorUrl")
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("coordinateType")
    public String getCoordinateType() {
        return coordinateType;
    }

    @JsonProperty("coordinateType")
    public void setCoordinateType(String coordinateType) {
        this.coordinateType = coordinateType;
    }

    @JsonProperty("coordinate1")
    public String getCoordinate1() {
        return coordinate1;
    }

    @JsonProperty("coordinate1")
    public void setCoordinate1(String coordinate1) {
        this.coordinate1 = coordinate1;
    }

    @JsonProperty("coordinate2")
    public String getCoordinate2() {
        return coordinate2;
    }

    @JsonProperty("coordinate2")
    public void setCoordinate2(String coordinate2) {
        this.coordinate2 = coordinate2;
    }

    @JsonProperty("bugTrackerUrl")
    public Object getBugTrackerUrl() {
        return bugTrackerUrl;
    }

    @JsonProperty("bugTrackerUrl")
    public void setBugTrackerUrl(Object bugTrackerUrl) {
        this.bugTrackerUrl = bugTrackerUrl;
    }

    @JsonProperty("codeRepoType")
    public Object getCodeRepoType() {
        return codeRepoType;
    }

    @JsonProperty("codeRepoType")
    public void setCodeRepoType(Object codeRepoType) {
        this.codeRepoType = codeRepoType;
    }

    @JsonProperty("codeRepoUrl")
    public String getCodeRepoUrl() {
        return codeRepoUrl;
    }

    @JsonProperty("codeRepoUrl")
    public void setCodeRepoUrl(String codeRepoUrl) {
        this.codeRepoUrl = codeRepoUrl;
    }

    @JsonProperty("latestRelease")
    public String getLatestRelease() {
        return latestRelease;
    }

    @JsonProperty("latestRelease")
    public void setLatestRelease(String latestRelease) {
        this.latestRelease = latestRelease;
    }

    @JsonProperty("latestReleaseDate")
    public String getLatestReleaseDate() {
        return latestReleaseDate;
    }

    @JsonProperty("latestReleaseDate")
    public void setLatestReleaseDate(String latestReleaseDate) {
        this.latestReleaseDate = latestReleaseDate;
    }

    @JsonProperty("versions")
    public List<Version> getVersions() {
        return versions;
    }

    @JsonProperty("versions")
    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    @JsonProperty("_links")
    public Links_ getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links_ links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("description", description).append("author", author).append("authorUrl", authorUrl).append("language", language).append("coordinateType", coordinateType).append("coordinate1", coordinate1).append("coordinate2", coordinate2).append("bugTrackerUrl", bugTrackerUrl).append("codeRepoType", codeRepoType).append("codeRepoUrl", codeRepoUrl).append("latestRelease", latestRelease).append("latestReleaseDate", latestReleaseDate).append("versions", versions).append("links", links).toString();
    }

}
