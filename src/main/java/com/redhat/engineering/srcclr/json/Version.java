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
package com.redhat.engineering.srcclr.json;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "releaseDate",
    "sha1",
    "sha2",
    "bytecodeHash",
    "platform",
    "licenses",
    "_links"
})
public class Version {

    @JsonProperty("version")
    private String version;
    @JsonProperty("releaseDate")
    private String releaseDate;
    @JsonProperty("sha1")
    private String sha1;
    @JsonProperty("sha2")
    private String sha2;
    @JsonProperty("bytecodeHash")
    private String bytecodeHash;
    @JsonProperty("platform")
    private String platform;
    @JsonProperty("licenses")
    private List<License> licenses = new ArrayList<License>();
    @JsonProperty("_links")
    private Links links;

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("releaseDate")
    public String getReleaseDate() {
        return releaseDate;
    }

    @JsonProperty("releaseDate")
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @JsonProperty("sha1")
    public String getSha1() {
        return sha1;
    }

    @JsonProperty("sha1")
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @JsonProperty("sha2")
    public String getSha2() {
        return sha2;
    }

    @JsonProperty("sha2")
    public void setSha2(String sha2) {
        this.sha2 = sha2;
    }

    @JsonProperty("bytecodeHash")
    public String getBytecodeHash() {
        return bytecodeHash;
    }

    @JsonProperty("bytecodeHash")
    public void setBytecodeHash(String bytecodeHash) {
        this.bytecodeHash = bytecodeHash;
    }

    @JsonProperty("platform")
    public String getPlatform() {
        return platform;
    }

    @JsonProperty("platform")
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @JsonProperty("licenses")
    public List<License> getLicenses() {
        return licenses;
    }

    @JsonProperty("licenses")
    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    @JsonProperty("_links")
    public Links getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("version", version).append("releaseDate", releaseDate).append("sha1", sha1).append("sha2", sha2).append("bytecodeHash", bytecodeHash).append("platform", platform).append("licenses", licenses).append("links", links).toString();
    }

}
